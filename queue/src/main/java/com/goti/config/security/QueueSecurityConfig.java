package com.goti.config.security;

import static com.goti.security.MeshSecuritySupport.PUBLIC_PATHS;

import com.goti.security.MeshProperties;
import com.goti.security.MeshSecuritySupport;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class QueueSecurityConfig {

	private final MeshProperties meshProperties;

	@Bean
	public SecurityFilterChain queueFilterChain(HttpSecurity http) throws Exception {
		MeshSecuritySupport.applyDefaults(http, meshProperties.enabled())
			.authorizeHttpRequests(auth -> {
				auth.requestMatchers(PUBLIC_PATHS).permitAll();
				if (meshProperties.enabled()) {
					auth.anyRequest().authenticated();
				} else {
					auth.anyRequest().permitAll();
				}
			});

		return http.build();
	}
}
