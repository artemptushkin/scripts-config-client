package io.github.artemptushkin.spring.cloud.config.source;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.AbstractResource;

@RequiredArgsConstructor
public class GroovyResource extends AbstractResource {
	private final String fileName;
	private final String scriptBody;

	@Override
	public String getDescription() {
		return fileName;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(
				scriptBody.getBytes(StandardCharsets.UTF_8)
		);
	}
}
