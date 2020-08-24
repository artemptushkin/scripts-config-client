package io.github.artemptushkin.spring.cloud.config.source;

import java.util.function.Consumer;

import lombok.Getter;

import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.context.support.GenericApplicationContext;

@Getter
public class KotlinScriptPropertySource extends ScriptPropertySource<BeanDefinitionDsl> {

	private final String profile;
	private final String label;

	public KotlinScriptPropertySource(BeanDefinitionDsl source, String name, String profile, String label) {
		super(name, source);
		this.profile = profile;
		this.label = label;
	}

	@Override
	public Consumer<GenericApplicationContext> updateContextFunction() {
		return genericApplicationContext -> getSource().initialize(genericApplicationContext);
	}
}
