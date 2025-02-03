package com.sap.periziafacile.pfgateway.filters.user;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class MockUserFilter implements GatewayFilter {

        private final MockUserService mockUserService;
        private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
        private final ResponseUtil responseUtil = new ResponseUtil();

        public MockUserFilter(MockUserService mockUserService) {
                this.mockUserService = mockUserService;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                if (exchange.getRequest().getMethod() == HttpMethod.POST) {
                        return addUser(exchange);
                }
                return returnAll(exchange);

        }

        private Mono<Void> addUser(ServerWebExchange exchange) {
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
                                                Map<String, String> user = objectMapper.readValue(body,
                                                                new TypeReference<>() {
                                                                });

                                                Optional<JSONObject> optionalUser = mockUserService
                                                                .addUser(new JSONObject(user));

                                                if (optionalUser.isEmpty()) {
                                                        return this.responseUtil.writeErrorResponse(exchange,
                                                                        HttpStatus.NOT_FOUND,
                                                                        "Error.");
                                                }

                                                return this.responseUtil.writeResponse(exchange, HttpStatus.OK,
                                                                optionalUser.get().toString());

                                        } catch (Exception e) {
                                                // In caso di errore nella conversione, restituisci una risposta di
                                                // errore
                                                return this.responseUtil.writeErrorResponse(exchange,
                                                                HttpStatus.BAD_REQUEST,
                                                                "Invalid JSON body");
                                        }
                                });
        }

        private Mono<Void> returnAll(ServerWebExchange exchange) {
                // Crea una risposta mock in formato JSON
                List<JSONObject> users = mockUserService.getAllUsers();

                JSONArray userList = new JSONArray();

                userList.putAll(users);

                JSONObject jsonResponse = new JSONObject().put("_embedded",
                                new JSONObject()
                                                .put("userList", userList));

                return this.responseUtil.writeResponse(exchange,
                                HttpStatus.OK,
                                jsonResponse.toString());
        }

}
