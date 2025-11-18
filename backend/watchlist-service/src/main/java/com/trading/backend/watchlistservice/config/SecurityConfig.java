package com.trading.backend.watchlistservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/watchlist/**", "/api/watchlist/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().permitAll()  // Allow all other requests - watchlist controller handles JWT validation
                )
                .csrf(csrf -> csrf.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
