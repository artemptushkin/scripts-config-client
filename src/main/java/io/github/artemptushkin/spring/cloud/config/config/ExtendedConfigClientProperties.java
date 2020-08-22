package io.github.artemptushkin.spring.cloud.config.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.core.env.Environment;

@ConfigurationProperties("spring.cloud.config")
public class ExtendedConfigClientProperties extends ConfigClientProperties {

	private String[] scriptFilesNames;

	public ExtendedConfigClientProperties(Environment environment) {
		super(environment);
	}
}
