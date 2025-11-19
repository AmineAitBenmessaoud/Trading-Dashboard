package com.trading.backend.gatewayservice.filter;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Skip for auth endpoints - they're handled by the gateway
        if (path.startsWith("/api/auth") || path.startsWith("/actuator")) {
            return chain.filter(exchange);
        }

        // Extract username from security context (already validated by JwtContextFilter)
        return ReactiveSecurityContextHolder.getContext()
            .mapNotNull(securityContext -> securityContext.getAuthentication().getPrincipal())
            .map(principal -> {
                String username = principal.toString();
                // Add username as a header for downstream services
                ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r.headers(h -> h.add("X-User-Name", username)))
                    .build();
                return modifiedExchange;
            })
            .defaultIfEmpty(exchange)
            .flatMap(chain::filter);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
