package io.github.artemptushkin.spring.cloud.config.controller;

import io.github.artemptushkin.spring.cloud.config.beans.FooInterface;
import io.github.artemptushkin.spring.cloud.config.beans.MyScriptBeans;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooController {

	@Value("${foo.baz}")
	private String value;

	@Autowired
	private FooInterface fooInterface;

	@Autowired
	ApplicationContext applicationContext;

	@Autowired
	private MyScriptBeans groovyBean;

	@GetMapping("/get-value")
	public String getValue() {
		return value;
	}

	@GetMapping("/say-hi-from-kotlin-script")
	public String get() {
		return fooInterface.hi();
	}

	@GetMapping("/say-hi-from-groovy-script")
	public String hello() {
		return groovyBean.getBazInterface().hello();
	}
}
