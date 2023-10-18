package com.back.mymontz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicaionConfig {

	@Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
	}
}
