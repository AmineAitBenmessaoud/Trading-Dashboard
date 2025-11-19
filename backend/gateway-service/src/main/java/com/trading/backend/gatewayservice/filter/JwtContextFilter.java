package com.trading.backend.gatewayservice.filter;

import com.trading.backend.gatewayservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import jakarta.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtContextFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        log.info("JwtContextFilter initialized with order: {}", Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        log.info("JwtContextFilter processing request for path: {} with authHeader: {}", 
            exchange.getRequest().getPath(), authHeader != null ? "Bearer ***" : "null");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("No Bearer token in Authorization header for path: {}", exchange.getRequest().getPath());
            return (Mono<Void>) (Object) chain.filter(exchange);
        }
        
        String token = authHeader.substring(7);
        
        try {
            if (!jwtUtil.validateToken(token)) {
                log.info("JWT token validation failed for path: {}", exchange.getRequest().getPath());
                return (Mono<Void>) (Object) chain.filter(exchange);
            }
            
            String username = jwtUtil.extractUsername(token);
            log.info("JWT token valid for user: {} on path: {}", username, exchange.getRequest().getPath());
            
            Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            
            SecurityContext securityContext = new SecurityContextImpl(auth);
            
            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
        } catch (Exception e) {
            log.warn("Exception during JWT processing: {}", e.getMessage());
            return (Mono<Void>) (Object) chain.filter(exchange);
        }
    }
}
