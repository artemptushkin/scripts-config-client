package io.github.artemptushkin.spring.cloud.config.controller;

import io.github.artemptushkin.spring.cloud.config.beans.FooInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FooController {

	@Value("${foo.baz}")
	private String value;

	@Value("${foo.custom}")
	private String customValue;

	@Autowired
	private FooInterface fooInterface;

	@GetMapping("/get-value")
	public String getValue() {
		return value;
	}

	@GetMapping("/get-value-from-custom")
	public String getCustomValue() {
		return customValue;
	}

	@GetMapping("/say-hi-from-script")
	public String get() {
		return fooInterface.hi();
	}
}
