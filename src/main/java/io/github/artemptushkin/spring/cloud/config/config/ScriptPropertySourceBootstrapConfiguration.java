package io.github.artemptushkin.spring.cloud.config.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import io.github.artemptushkin.spring.cloud.config.locator.ScriptPropertySourceLocator;
import io.github.artemptushkin.spring.cloud.config.source.ScriptPropertySource;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import static java.text.MessageFormat.format;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ConfigScriptsProperties.class)
public class ScriptPropertySourceBootstrapConfiguration implements
		ApplicationContextInitializer<GenericApplicationContext>, Ordered {

	private final int order = Ordered.HIGHEST_PRECEDENCE + 1;

	private static final Log LOGGER = LogFactory.getLog(ScriptPropertySourceBootstrapConfiguration.class);

	@Setter
	@Autowired(required = false)
	private List<ScriptPropertySourceLocator> propertySourceLocators = new ArrayList<>();

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public void initialize(GenericApplicationContext applicationContext) {
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		List<ScriptPropertySource<?>> result = propertySourceLocators
				.stream()
				.map(it -> it.locate(environment))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
		if (!result.isEmpty()) {
			LOGGER.debug("Script property sources has been found, trying to update context");
			insertPropertySources(applicationContext, result);
		}
	}

	private void insertPropertySources(GenericApplicationContext applicationContext, List<ScriptPropertySource<?>> result) {
		result
				.stream()
				.peek(scriptPropertySource -> LOGGER.info(format("Updating context by the script {0}", scriptPropertySource.getName())))
				.forEach(scriptPropertySource -> scriptPropertySource.updateContextFunction().accept(applicationContext));
	}
}
