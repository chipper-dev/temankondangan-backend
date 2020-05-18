package com.mitrais.chipper.temankondangan.backendapps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import com.mitrais.chipper.temankondangan.backendapps.config.AppProperties;
import com.mitrais.chipper.temankondangan.backendapps.model.common.AuditorAwareImpl;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableConfigurationProperties(AppProperties.class)
public class BackendAppsApplication {

	@Bean
	public AuditorAware<String> auditorAware() {
		return new AuditorAwareImpl();
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendAppsApplication.class, args);
	}
}
