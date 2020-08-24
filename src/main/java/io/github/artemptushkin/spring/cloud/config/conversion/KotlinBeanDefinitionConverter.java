package io.github.artemptushkin.spring.cloud.config.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.core.io.InputStreamResource;

@Slf4j
public class KotlinBeanDefinitionConverter {

	public BeanDefinitionDsl convert(InputStreamResource resource) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("kts");
		try {
			InputStream inputStream = resource.getInputStream();
			return (BeanDefinitionDsl) engine.eval(new InputStreamReader(inputStream));
		} catch (IOException e) {
			log.error("Handled exception during file access", e);
			throw new IllegalStateException(e);
		} catch(ScriptException e) {
			log.error("Handled exception file serialization", e);
			throw new IllegalStateException(e);
		}
	}
}
