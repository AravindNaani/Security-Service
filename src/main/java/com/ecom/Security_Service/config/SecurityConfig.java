package com.ecom.Security_Service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since we use JWT
                .csrf(csrf -> csrf.disable())

                // Disable default login form
                .formLogin(form -> form.disable())

                // Disable HTTP Basic Auth
                .httpBasic(basic -> basic.disable())

                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll() // allow health checks
                        .requestMatchers("/validate-token").permitAll() // allow token validation
                        .requestMatchers("/hash-password").permitAll() // allow password hashing
                        .anyRequest().authenticated() // everything else requires authentication
                );

        return http.build();
    }
}
