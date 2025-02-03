package com.sap.periziafacile.pfgateway.filters.auth;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sap.periziafacile.pfgateway.utils.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter {

        private final JwtUtil jwtUtil;
        private final List<String> acceptedRoles;

        public AuthFilter(List<String> acceptedRoles) {
                this.jwtUtil = new JwtUtil();
                this.acceptedRoles = acceptedRoles;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                HttpHeaders headers = exchange.getRequest().getHeaders();

                if (!headers.containsKey("Authorization"))
                        return returnUnauthorized(exchange);

                String auth = headers.get("Authorization").stream().findFirst().orElse("");

                if (!(auth.startsWith("Bearer ")))
                        return returnUnauthorized(exchange);

                String token = auth.substring(7);

                if (!this.jwtUtil.validateToken(token))
                        return returnUnauthorized(exchange);

                String role = this.jwtUtil.getRoleFromToken(token);

                if (!this.acceptedRoles.contains(role))
                        return returnUnauthorized(exchange);

                exchange.getAttributes().put("logged_username", this.jwtUtil.getUsernameFromToken(token));
                exchange.getAttributes().put("logged_id", this.jwtUtil.getIdFromToken(token));

                return chain.filter(exchange);
        }

        private Mono<Void> returnUnauthorized(ServerWebExchange exchange) {
                // any other case is unauthorized
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
        }

}
