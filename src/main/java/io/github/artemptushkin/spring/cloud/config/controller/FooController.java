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

	@Autowired
	private FooInterface fooInterface;

	@GetMapping("/get-value")
	public String getValue() {
		return value;
	}

	@GetMapping("/say-hi-from-script")
	public String get() {
		return fooInterface.hi();
	}
}
