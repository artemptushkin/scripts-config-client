package io.github.artemptushkin.spring.cloud.config;

import io.github.artemptushkin.spring.cloud.config.config.ExtendedConfigClientProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ExtendedConfigClientProperties.class)
public class ScriptsConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScriptsConfigClientApplication.class, args);
	}

}
