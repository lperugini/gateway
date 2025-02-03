package com.sap.periziafacile.pfgateway.filters.auth;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class MockRegisterFilter implements GatewayFilter {

    private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
    private final ResponseUtil responseUtil = new ResponseUtil();

    private final MockUserService mockUserService;

    public MockRegisterFilter(MockUserService mockUserService) {
        this.mockUserService = mockUserService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Legge il body della richiesta
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    // Converti il DataBuffer in una stringa
                    byte[] bodyBytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bodyBytes);
                    DataBufferUtils.release(dataBuffer); // Rilascia il buffer
                    String body = new String(bodyBytes, StandardCharsets.UTF_8);

                    try {
                        // Converti il JSON in una mappa
                        Map<String, String> user = objectMapper.readValue(body, new TypeReference<>() {
                        });

                        String username = user.get("username");
                        String password = user.get("password");

                        Optional<JSONObject> optionalUser = mockUserService.getByUsername(username);
                        if (optionalUser.isEmpty()) {
                            Optional<JSONObject> newUser = mockUserService
                                    .addUser(new JSONObject()
                                            .put("username", username)
                                            .put("password", password));
                            return this.responseUtil.writeResponse(exchange, HttpStatus.CREATED,
                                    newUser.get().toString());
                        }
                        return this.responseUtil.writeErrorResponse(exchange, HttpStatus.BAD_REQUEST,
                                "Username not valid.");
                    } catch (Exception e) {
                        // In caso di errore nella conversione, restituisci una risposta di errore
                        return this.responseUtil.writeErrorResponse(exchange, HttpStatus.BAD_REQUEST,
                                "Invalid JSON body");
                    }
                });
    }

}
