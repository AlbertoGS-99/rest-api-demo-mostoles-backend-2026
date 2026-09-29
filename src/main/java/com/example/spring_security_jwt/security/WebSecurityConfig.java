package com.example.spring_security_jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.example.spring_security_jwt.security.jwt.AuthEntryPointJwt;
import com.example.spring_security_jwt.security.jwt.AuthTokenFilter;
import com.example.spring_security_jwt.security.jwt.JwtUtils;
import com.example.spring_security_jwt.security.service.UserDetailsServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
/**
 * La anotacion anterior permite securedEnable = true, 
 * jsr250Enabled = true y la mas importante prePostEnabled = true
 * 
 * Que se resume a poder asegurar (securizar) directamente los metodos
 * de los controladores, es decir, donde se delegan las peticiones,
 * concretamente los endpoints
 * 
 */
@RequiredArgsConstructor
public class WebSecurityConfig {
	
	private final UserDetailsServiceImpl userDetailsService;
	private final AuthEntryPointJwt unauthorizeHandle;
	private final JwtUtils jwtUtils;
	
	@Bean
	AuthTokenFilter authenticationJwtTokenFilter() {
		
		return new AuthTokenFilter(jwtUtils, userDetailsService);
	}
}









