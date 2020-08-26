package io.github.artemptushkin.spring.cloud.config.factory;

import java.io.IOException;

import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

/* an option with PropertySource */
public class GroovyScriptPropertyFactory implements PropertySourceFactory {
	@Override
	public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
		resource.getInputStream();//todo groovy impl
		return null;
	}
}
