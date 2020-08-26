package io.github.artemptushkin.spring.cloud.config.source;

import java.util.function.Consumer;

import lombok.Getter;

import org.springframework.context.support.GenericApplicationContext;

@Getter
public abstract class ScriptPropertySource<T> {
	private final String name;
	private final String profile;
	private final String label;
	private final T source;

	public ScriptPropertySource(String name, String profile, String label, T source) {
		this.name = name;
		this.profile = profile;
		this.label = label;
		this.source = source;
	}

	public abstract Consumer<GenericApplicationContext> updateContextFunction();
}
