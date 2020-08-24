package io.github.artemptushkin.spring.cloud.config.locator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.artemptushkin.spring.cloud.config.config.ExternalScriptsProperties;
import io.github.artemptushkin.spring.cloud.config.conversion.BeanDefinitionHttpMessageConverter;
import io.github.artemptushkin.spring.cloud.config.source.KotlinScriptPropertySource;
import io.github.artemptushkin.spring.cloud.config.source.ScriptPropertySource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.origin.Origin;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigClientStateHolder;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.Retryable;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;
import static org.springframework.cloud.config.client.ConfigClientProperties.STATE_HEADER;
import static org.springframework.cloud.config.client.ConfigClientProperties.TOKEN_HEADER;

public class ScriptFilesLocator implements ScriptPropertySourceLocator {

	private static final Log LOGGER = LogFactory.getLog(ScriptFilesLocator.class);

	private RestTemplate restTemplate;
	private ConfigClientProperties configClientProperties;
	private ExternalScriptsProperties externalScriptsProperties;

	public ScriptFilesLocator(ConfigClientProperties configClientProperties, ExternalScriptsProperties externalScriptsProperties) {
		this.configClientProperties = configClientProperties;
		this.externalScriptsProperties = externalScriptsProperties;
	}

	@Override
	@Retryable(interceptor = "configServerRetryInterceptor")
	public Collection<ScriptPropertySource<?>> locate(
			org.springframework.core.env.Environment environment) {
		ConfigClientProperties properties = this.configClientProperties.override(environment);
		List<ScriptPropertySource<?>> scriptPropertySources = new ArrayList<>();
		RestTemplate restTemplate = this.restTemplate == null
				? getSecureRestTemplate(properties) : this.restTemplate;
		Exception error = null;
		String errorBody = null;
		try {
			String[] labels = new String[] { "" };
			if (StringUtils.hasText(properties.getLabel())) {
				labels = StringUtils
						.commaDelimitedListToStringArray(properties.getLabel());
			}
			String state = ConfigClientStateHolder.getState();
			// Try all the labels until one works
			for (String label : labels) {
				KotlinScriptPropertySource result = getRemoteEnvironment(restTemplate, properties, label.trim(), state);
				if (result != null) {
					log(result);
					scriptPropertySources.add(result);
				}
			}
			errorBody = String.format("None of labels %s found", Arrays.toString(labels));
		}
		catch (HttpServerErrorException e) {
			error = e;
			if (MediaType.APPLICATION_JSON
					.includes(e.getResponseHeaders().getContentType())) {
				errorBody = e.getResponseBodyAsString();
			}
		}
		catch (Exception e) {
			error = e;
			LOGGER.error("Unexpected error during getting script", e);
		}
		if (properties.isFailFast()) {
			throw new IllegalStateException(
					"Could not locate PropertySource and the fail fast property is set, failing"
							+ (errorBody == null ? "" : ": " + errorBody),
					error);
		}
		LOGGER.warn("Could not locate PropertySource: "
				+ (error != null ? error.getMessage() : errorBody));
		return scriptPropertySources;

	}

	private void log(KotlinScriptPropertySource result) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(String.format(
					"Located kotlin script file: name=%s, profiles=%s, label=%s",
					result.getName(),
					result.getProfile() == null ? "" : Collections.singletonList(result.getProfile()),
					result.getLabel()));
		}
	}

	private KotlinScriptPropertySource getRemoteEnvironment(RestTemplate restTemplate,
			ConfigClientProperties properties, String label, String state) {
		String path = "/{name}/{profile}";
		String name = properties.getName();
		String profile = properties.getProfile();
		String token = properties.getToken();
		int noOfUrls = properties.getUri().length;
		if (noOfUrls > 1) {
			LOGGER.info("Multiple Config Server Urls found listed.");
		}

		Object[] args = new String[] { name, profile };
		if (StringUtils.hasText(label)) {
			// workaround for Spring MVC matching / in paths
			label = org.springframework.cloud.config.environment.Environment.denormalize(label);
			args = new String[] { name, profile, label };
			path = path + "/{label}";
		}
		ResponseEntity<BeanDefinitionDsl> response = null;

		for (int i = 0; i < noOfUrls; i++) {
			ConfigClientProperties.Credentials credentials = properties.getCredentials(i);
			String uri = credentials.getUri();
			String username = credentials.getUsername();
			String password = credentials.getPassword();

			LOGGER.info("Fetching config from server at : " + uri);

			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setAccept(
						Collections.singletonList(MediaType.TEXT_PLAIN));
				addAuthorizationToken(properties, headers, username, password);
				if (StringUtils.hasText(token)) {
					headers.add(TOKEN_HEADER, token);
				}
				if (StringUtils.hasText(state) && properties.isSendState()) {
					headers.add(STATE_HEADER, state);
				}

				final HttpEntity<Void> entity = new HttpEntity<>((Void) null, headers);
				response = restTemplate.exchange(uri + path + "/script-beans.kts", HttpMethod.GET, entity,
						BeanDefinitionDsl.class, args);
			}
			catch (HttpClientErrorException e) {
				if (e.getStatusCode() != HttpStatus.NOT_FOUND) {
					throw e;
				}
			}
			catch (ResourceAccessException e) {
				LOGGER.info("Connect Timeout Exception on Url - " + uri
						+ ". Will be trying the next url if available");
				if (i == noOfUrls - 1) {
					throw e;
				}
				else {
					continue;
				}
			}

			if (response == null || response.getStatusCode() != HttpStatus.OK) {
				return null;
			}

			return new KotlinScriptPropertySource(response.getBody(), "script-beans", profile, label);
		}

		return null;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private RestTemplate getSecureRestTemplate(ConfigClientProperties client) {
		SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
		if (client.getRequestReadTimeout() < 0) {
			throw new IllegalStateException("Invalid Value for Read Timeout set.");
		}
		if (client.getRequestConnectTimeout() < 0) {
			throw new IllegalStateException("Invalid Value for Connect Timeout set.");
		}
		requestFactory.setReadTimeout(client.getRequestReadTimeout());
		requestFactory.setConnectTimeout(client.getRequestConnectTimeout());
		RestTemplate template = new RestTemplate(requestFactory);
		Map<String, String> headers = new HashMap<>(client.getHeaders());
		if (headers.containsKey(AUTHORIZATION)) {
			headers.remove(AUTHORIZATION); // To avoid redundant addition of header
		}
		if (!headers.isEmpty()) {
			template.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(
					new GenericRequestHeaderInterceptor(headers)));
		}
		template.getMessageConverters().add(new BeanDefinitionHttpMessageConverter());

		return template;
	}

	private void addAuthorizationToken(ConfigClientProperties configClientProperties,
			HttpHeaders httpHeaders, String username, String password) {
		String authorization = configClientProperties.getHeaders().get(AUTHORIZATION);

		if (password != null && authorization != null) {
			throw new IllegalStateException(
					"You must set either 'password' or 'authorization'");
		}

		if (password != null) {
			byte[] token = Base64Utils.encode((username + ":" + password).getBytes());
			httpHeaders.add("Authorization", "Basic " + new String(token));
		}
		else if (authorization != null) {
			httpHeaders.add("Authorization", authorization);
		}

	}

	/**
	 * Adds the provided headers to the request.
	 */
	public static class GenericRequestHeaderInterceptor
			implements ClientHttpRequestInterceptor {

		private final Map<String, String> headers;

		public GenericRequestHeaderInterceptor(Map<String, String> headers) {
			this.headers = headers;
		}

		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body,
				ClientHttpRequestExecution execution) throws IOException {
			for (Map.Entry<String, String> header : this.headers.entrySet()) {
				request.getHeaders().add(header.getKey(), header.getValue());
			}
			return execution.execute(request, body);
		}

		protected Map<String, String> getHeaders() {
			return this.headers;
		}

	}

	static class ConfigServiceOrigin implements Origin {

		private final String remotePropertySource;

		private final Object origin;

		ConfigServiceOrigin(String remotePropertySource, Object origin) {
			this.remotePropertySource = remotePropertySource;
			Assert.notNull(origin, "origin may not be null");
			this.origin = origin;

		}

		@Override
		public String toString() {
			return "Config Server " + this.remotePropertySource + ":"
					+ this.origin.toString();
		}
	}
}
