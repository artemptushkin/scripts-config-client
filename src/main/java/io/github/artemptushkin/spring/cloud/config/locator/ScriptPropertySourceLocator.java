package io.github.artemptushkin.spring.cloud.config.locator;

import java.util.Collection;

import io.github.artemptushkin.spring.cloud.config.source.ScriptPropertySource;

import org.springframework.core.env.Environment;

public interface ScriptPropertySourceLocator {
	/**
	 * @param environment The current Environment.
	 * @return A PropertySource, or null if there is none.
	 * @throws IllegalStateException if there is a fail-fast condition.
	 */
	Collection<ScriptPropertySource<?>> locate(Environment environment);
}
