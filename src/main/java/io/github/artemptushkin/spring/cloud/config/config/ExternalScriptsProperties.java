package io.github.artemptushkin.spring.cloud.config.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.cloud.config.scripts")
public class ExternalScriptsProperties {

	private String[] files;
}
