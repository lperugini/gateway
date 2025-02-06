package com.sap.periziafacile.pfgateway.filters.fallback;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class FallbackFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String fallbackResponse = "{ \"message\": \"Service is currently unavailable. Please try again later.\" }";

        return new ResponseUtil().writeResponse(exchange, HttpStatus.SERVICE_UNAVAILABLE, fallbackResponse);
    }

}
