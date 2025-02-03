package com.sap.periziafacile.pfgateway.filters.order;

import java.net.URI;
import java.util.List;

import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class UserOrdersFilter implements GatewayFilter {

        private final MockUserService mockUserService;
        private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
        private final ResponseUtil responseUtil = new ResponseUtil();

        public UserOrdersFilter(MockUserService mockUserService) {
                this.mockUserService = mockUserService;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String role = (String) exchange.getAttributes().get("logged_role");
                Integer loggedId = (Integer) exchange.getAttributes().get("logged_id");

                if (List.of("admin").contains(role)) {
                        System.out.println("admin");
                        return chain.filter(exchange);
                }

                String uri = exchange
                                .getRequest()
                                .getURI()
                                .toString()
                                .replace("8080", "8083");

                ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(exchange
                                                .getRequest()
                                                .mutate()
                                                .uri(URI.create(uri + "/for/" + loggedId))
                                                .build())
                                .build();

                return chain.filter(mutatedExchange);
        }

}
