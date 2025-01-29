package com.sap.periziafacile.pfgateway.filters.order;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class PeronalOrderFilter implements GatewayFilter {

        private final MockUserService mockUserService;
        private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
        private final ResponseUtil responseUtil = new ResponseUtil();

        public PeronalOrderFilter(MockUserService mockUserService) {
                this.mockUserService = mockUserService;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                String loggedId = (String) exchange.getAttributes().get("logged_id");

                String path = exchange.getRequest().getPath().toString();
                String id = path.substring(path.lastIndexOf("/") + 1);
                Long orderId = Long.valueOf(id);

                return WebClient
                                .builder()
                                .build()
                                .get()
                                .uri("http://localhost:8083/orders/{id}", orderId)
                                .retrieve()
                                .bodyToMono(String.class)
                                .flatMap(order -> {
                                        JSONObject jsonOrder = new JSONObject(order);

                                        if (jsonOrder.has("user")) {
                                                Long userid = Long.valueOf(jsonOrder.get("user").toString());

                                                if (userid == Long.valueOf(loggedId)) {
                                                        return chain.filter(exchange);
                                                }
                                        }

                                        return this.responseUtil.writeErrorResponse(exchange, "");
                                });
        }

}
