package io.github.artemptushkin.spring.cloud.config.conversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

@Slf4j
public class BeanDefinitionHttpMessageConverter extends AbstractHttpMessageConverter<BeanDefinitionDsl> {

	public BeanDefinitionHttpMessageConverter() {
		super(MediaType.ALL);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return BeanDefinitionDsl.class.isAssignableFrom(clazz);
	}

	@Override
	protected BeanDefinitionDsl readInternal(Class<? extends BeanDefinitionDsl> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("kts");
		try {
			InputStream inputStream = inputMessage.getBody();
			return (BeanDefinitionDsl) engine.eval(new InputStreamReader(inputStream));
		} catch (Exception e) {
			log.error("Handled exception during file access", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void writeInternal(BeanDefinitionDsl beanDefinitionDsl, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {

	}
}
