package com.sap.periziafacile.pfgateway.filters;

import java.util.concurrent.CompletableFuture;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class AggregationFilter implements GatewayFilter {

    private final RestTemplate restTemplate;

    public AggregationFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //System.out.println(exchange.getRequest().getPath());

        CompletableFuture<String> customerService = CompletableFuture
                .supplyAsync(() -> restTemplate.getForObject("http://localhost:8082/customers", String.class));

        CompletableFuture<String> orderService = CompletableFuture
                .supplyAsync(() -> restTemplate.getForObject("http://localhost:8083/orders", String.class));

        return Mono.fromFuture(
                customerService.thenCombine(orderService, (a, b) -> "[" + a + ", " + b + "]"))
                .flatMap(combinedResponse -> {
                    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    byte[] bytes = combinedResponse.getBytes();
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                    return exchange.getResponse().writeWith(Mono.just(buffer));
                })
                .onErrorResume(e -> {
                    exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                    return exchange.getResponse().setComplete();
                });
    }
}
