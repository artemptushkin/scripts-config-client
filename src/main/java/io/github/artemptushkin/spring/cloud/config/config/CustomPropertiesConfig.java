package io.github.artemptushkin.spring.cloud.config.config;

import io.github.artemptushkin.spring.cloud.config.factory.GroovyScriptPropertyFactory;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

//@Configuration
@PropertySource(value = "http://localhost:8888/scripts-app/dev/groovy/foo-script.groovy", factory = GroovyScriptPropertyFactory.class)
public class CustomPropertiesConfig {
}
