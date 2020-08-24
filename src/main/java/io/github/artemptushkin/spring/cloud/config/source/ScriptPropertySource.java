package io.github.artemptushkin.spring.cloud.config.source;

import java.util.function.Consumer;

import lombok.Getter;

import org.springframework.context.support.GenericApplicationContext;

@Getter
public abstract class ScriptPropertySource<T> {
	private final String name;
	private final T source;

	public ScriptPropertySource(String name, T source) {
		this.name = name;
		this.source = source;
	}

	public abstract Consumer<GenericApplicationContext> updateContextFunction();
}
