package com.ecom.Security_Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll() // allow health checks
                        .requestMatchers("/api/v1/security/validate-token").permitAll() // allow token validation
                        .requestMatchers("/api/v1/security/hash-password").permitAll() // allow password hashing
                        .anyRequest().authenticated() // everything else requires authentication
                );

        return http.build();
    }
}
