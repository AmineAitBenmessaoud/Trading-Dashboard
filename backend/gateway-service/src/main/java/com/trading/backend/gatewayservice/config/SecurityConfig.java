package com.trading.backend.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/api/*/health").permitAll()  // Allow health endpoints from all services
                .pathMatchers("/api/market/top-movers").permitAll()  // Allow public access to top-movers
                .pathMatchers("/api/market/trending").permitAll()  // Allow public access to trending
                .pathMatchers("/api/market/search").permitAll()  // Allow public access to market search
                .pathMatchers("/api/market/data/**").permitAll()  // Allow public access to market data by symbol
                .pathMatchers("/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
