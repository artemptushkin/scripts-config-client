package io.github.artemptushkin.spring.cloud.config.source;

import java.util.function.Consumer;

import lombok.Getter;

import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;

@Getter
public class GroovyScriptPropertySource extends ScriptPropertySource<String> {

	public GroovyScriptPropertySource(String name, String profile, String label, String source) {
		super(name, profile, label, source);
	}

	@Override
	public Consumer<GenericApplicationContext> updateContextFunction() {
		return genericApplicationContext -> {
			GroovyBeanDefinitionReader beanDefinitionReader = new GroovyBeanDefinitionReader(genericApplicationContext);
			beanDefinitionReader.loadBeanDefinitions(new GroovyResource(getName(), getSource()));
		};
	}
}
