package io.github.artemptushkin.spring.cloud.config.source;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class KotlinScriptContextInitializer implements ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext applicationContext) {
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		BeanDefinitionDsl beanDefinitionDsl = environment.getProperty("script-beans", BeanDefinitionDsl.class);
		if (beanDefinitionDsl != null) {
			log.info("Registering kotlin script bean definitions");
			beanDefinitionDsl.initialize(applicationContext);
		}
	}
}
