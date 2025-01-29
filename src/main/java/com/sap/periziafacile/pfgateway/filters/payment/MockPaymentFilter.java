package com.sap.periziafacile.pfgateway.filters.payment;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sap.periziafacile.pfgateway.services.MockUserService;
import reactor.core.publisher.Mono;

@Component
public class MockPaymentFilter implements GatewayFilter {

        public MockPaymentFilter(MockUserService mockUserService) {
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return chain.filter(exchange);
        }

}
