package io.github.artemptushkin.spring.cloud.config.locator;

import io.github.artemptushkin.spring.cloud.config.config.ExtendedConfigClientProperties;

import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;

public class ScriptFilesLocator extends ConfigServicePropertySourceLocator {

	public ScriptFilesLocator(ExtendedConfigClientProperties defaultProperties) {
		super(defaultProperties);
	}
}
