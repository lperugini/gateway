package com.sap.periziafacile.pfgateway.filters.user;

import java.util.Optional;

import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class MockPersonalInfoFilter implements GatewayFilter {

        private final MockUserService mockUserService;
        private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
        private final ResponseUtil responseUtil = new ResponseUtil();

        public MockPersonalInfoFilter(MockUserService mockUserService) {
                this.mockUserService = mockUserService;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return returnMe(exchange);
        }

        private Mono<Void> returnMe(ServerWebExchange exchange) {

                String loggedUsername = (String) exchange.getAttributes().get("logged_username");

                // Crea una risposta mock in formato JSON
                Optional<JSONObject> user = mockUserService.getByUsername(loggedUsername);

                JSONObject jsonResponse;
                if (user.isPresent()) {
                        jsonResponse = user.get();
                } else {
                        jsonResponse = new JSONObject()
                                        .put("error", "User not found")
                                        .put("_links", new JSONObject()
                                                        .put("allUsers", new JSONObject()
                                                                        .put("href", "/users")));
                }
                return this.responseUtil.writeResponse(exchange,
                                HttpStatus.OK,
                                jsonResponse.toString());
        }

}
