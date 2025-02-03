package com.sap.periziafacile.pfgateway.filters.user;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class MockSingleUserFilter implements GatewayFilter {

        private final MockUserService mockUserService;
        private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
        private final ResponseUtil responseUtil = new ResponseUtil();

        public MockSingleUserFilter(MockUserService mockUserService) {
                this.mockUserService = mockUserService;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                if (exchange.getRequest().getMethod() == HttpMethod.PUT) {
                        return editUser(exchange);
                }
                if (exchange.getRequest().getMethod() == HttpMethod.DELETE) {
                        return deleteUser(exchange);
                }
                return returnSingle(exchange);
        }

        private Mono<Void> deleteUser(ServerWebExchange exchange) {
                String path = exchange.getRequest().getPath().toString();
                String id = path.substring(path.lastIndexOf("/") + 1);
                Long itemId = Long.valueOf(id);

                if (mockUserService.deleteUser(itemId)) {
                        return this.responseUtil.writeResponse(exchange, HttpStatus.OK, itemId.toString());
                }
                return this.responseUtil.writeResponse(exchange, HttpStatus.BAD_REQUEST, "Invalid JSON body");
        }

        private Mono<Void> returnSingle(ServerWebExchange exchange) {
                String path = exchange.getRequest().getPath().toString();
                String id = path.substring(path.lastIndexOf("/") + 1);
                Long itemId = Long.valueOf(id);
                Optional<JSONObject> optional = mockUserService.getById(itemId);

                JSONObject jsonResponse;
                if (optional.isPresent()) {
                        jsonResponse = optional.get();
                } else {
                        jsonResponse = new JSONObject()
                                        .put("error", "Item not found")
                                        .put("_links", new JSONObject()
                                                        .put("allItems", new JSONObject()
                                                                        .put("href", "/items")));
                }
                return this.responseUtil.writeResponse(exchange, HttpStatus.OK, jsonResponse.toString());
        }

        private Mono<Void> editUser(ServerWebExchange exchange) {
                String path = exchange.getRequest().getPath().toString();
                String id = path.substring(path.lastIndexOf("/") + 1);
                Long itemId = Long.valueOf(id);

                return DataBufferUtils
                                .join(exchange.getRequest().getBody())
                                .flatMap(dataBuffer -> {
                                        // Converti il DataBuffer in una stringa
                                        byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(bodyBytes);
                                        DataBufferUtils.release(dataBuffer); // Rilascia il buffer
                                        String body = new String(bodyBytes, StandardCharsets.UTF_8);

                                        try {
                                                // Converti il JSON in una mappa
                                                Map<String, String> item = objectMapper.readValue(body,
                                                                new TypeReference<>() {
                                                                });

                                                Optional<JSONObject> optionalItem = mockUserService
                                                                .updateUser(itemId, new JSONObject(item));

                                                if (optionalItem.isEmpty()) {
                                                        return this.responseUtil.writeResponse(exchange,
                                                                        HttpStatus.NOT_FOUND,
                                                                        "Error.");
                                                }

                                                return this.responseUtil.writeResponse(exchange,
                                                                HttpStatus.OK,
                                                                optionalItem.get().toString());

                                        } catch (Exception e) {
                                                // In caso di errore nella conversione, restituisci una risposta di
                                                // errore
                                                return this.responseUtil.writeResponse(exchange,
                                                                HttpStatus.BAD_REQUEST,
                                                                "Invalid JSON body");
                                        }
                                });
        }

}
