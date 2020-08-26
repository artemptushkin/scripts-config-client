package io.github.artemptushkin.spring.cloud.config.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.support.BeanDefinitionDsl;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties("spring.cloud.config.scripts")
public class ConfigScriptsProperties {

	private GroovyScriptLanguageProperties groovy;
	private KotlinScriptLanguageProperties kotlin;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
	public static class GroovyScriptLanguageProperties extends ScriptLanguageProperties<String> {
		private boolean enabled = true;
		private String[] scriptFiles;
		private String fileExtension = ".groovy";
		private Class<String> scriptClass = String.class;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
	public static class KotlinScriptLanguageProperties extends ScriptLanguageProperties<BeanDefinitionDsl> {
		private boolean enabled = true;
		private String[] scriptFiles;
		private String fileExtension = ".kts";
		private Class<BeanDefinitionDsl> scriptClass = BeanDefinitionDsl.class;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static abstract class ScriptLanguageProperties<T> {
		private boolean enabled = true;
		private String[] scriptFiles;

		@NotNull
		public abstract String getFileExtension();
		@NotNull
		public abstract Class<T> getScriptClass();
	}
}
