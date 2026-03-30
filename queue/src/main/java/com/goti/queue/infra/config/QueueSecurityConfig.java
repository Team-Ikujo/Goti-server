package com.goti.queue.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.goti.security.MeshProperties;
import com.goti.security.MeshSecuritySupport;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.application.name", havingValue = "goti-queue-service")
public class QueueSecurityConfig {

	private final MeshProperties meshProperties;

	@Bean
	public SecurityFilterChain queueFilterChain(HttpSecurity http) throws Exception {
		MeshSecuritySupport.applyDefaults(http, meshProperties.enabled());

		http.authorizeHttpRequests(auth -> auth
			.requestMatchers(MeshSecuritySupport.PUBLIC_PATHS).permitAll()
			.requestMatchers("/actuator/**").permitAll()
			.anyRequest().authenticated()
		);

		return http.build();
	}
}
