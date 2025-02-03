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
                        System.out.println("all");
                        return chain.filter(exchange);
                }

                String uri = exchange
                                .getRequest()
                                .getURI()
                                .toString()
                                .replace("8080", "8083");

                System.out.println(uri);

                ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(exchange
                                                .getRequest()
                                                .mutate()
                                                .uri(URI.create(uri + "/for/" + loggedId))
                                                .build())
                                .build();

                System.out.println(mutatedExchange.getRequest().getURI());

                return chain.filter(mutatedExchange);

                /*
                 * String path = exchange.getRequest().getPath().toString();
                 * String id = path.substring(path.lastIndexOf("/") + 1);
                 * Long orderId = Long.valueOf(id);
                 * 
                 * return WebClient
                 * .builder()
                 * .build()
                 * .get()
                 * .uri("http://localhost:8083/orders/{id}", orderId)
                 * .retrieve()
                 * .bodyToMono(String.class)
                 * .flatMap(order -> {
                 * JSONObject jsonOrder = new JSONObject(order);
                 * 
                 * if (jsonOrder.has("user")) {
                 * Long userid = Long.valueOf(jsonOrder.get("user").toString());
                 * 
                 * if (Long.valueOf(loggedId).equals(userid)) {
                 * return chain.filter(exchange);
                 * }
                 * }
                 * 
                 * return this.responseUtil.writeResponse(exchange,
                 * HttpStatus.UNAUTHORIZED,
                 * "UNAUTHORIZED");
                 * });
                 */
        }

}
