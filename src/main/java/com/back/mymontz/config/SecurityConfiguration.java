package com.back.mymontz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        	.csrf(csrf -> csrf.disable())
        	.authorizeHttpRequests(auth -> auth
	            .requestMatchers("/**").permitAll()
	            .anyRequest().authenticated());
        
        return http.build();
    }
}
