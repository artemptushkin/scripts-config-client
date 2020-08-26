package io.github.artemptushkin.spring.cloud.config.source;

import java.util.function.Consumer;

import lombok.Getter;

import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.context.support.GenericApplicationContext;

@Getter
public class KotlinScriptPropertySource extends ScriptPropertySource<BeanDefinitionDsl> {

	public KotlinScriptPropertySource(String name, String profile, String label, BeanDefinitionDsl source) {
		super(name, profile, label, source);
	}

	@Override
	public Consumer<GenericApplicationContext> updateContextFunction() {
		return genericApplicationContext -> getSource().initialize(genericApplicationContext);
	}
}
