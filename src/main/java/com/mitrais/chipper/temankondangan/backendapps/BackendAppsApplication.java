package com.mitrais.chipper.temankondangan.backendapps;

import com.mitrais.chipper.temankondangan.backendapps.config.AppProperties;
import com.mitrais.chipper.temankondangan.backendapps.model.common.AuditorAwareImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//@EnableEurekaClient
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableHystrix
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
