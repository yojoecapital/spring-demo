package com.learning.sprintsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final ScopedAuthorizationManager booksAuthorizationManager = new ScopedAuthorizationManager("books");
    private final ScopedAuthorizationManager moviesAuthorizationManager = new ScopedAuthorizationManager("movies");

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(requestMatcher -> requestMatcher
            .requestMatchers("/hello").authenticated()
            .requestMatchers("/books/**").access(booksAuthorizationManager)
            .requestMatchers("/movies/**").access(moviesAuthorizationManager)
            .anyRequest().permitAll()
        ).oauth2ResourceServer(serverConfigurer -> serverConfigurer
            .jwt(Customizer.withDefaults())
        );
        return http.build();
    }
}
