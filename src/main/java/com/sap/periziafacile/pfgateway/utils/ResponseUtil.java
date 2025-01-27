package com.sap.periziafacile.pfgateway.utils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ResponseUtil {

    private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON

    public Mono<Void> writeResponse(ServerWebExchange exchange, Map<String, String> content) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.OK);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        try {
            // Serializza la mappa in JSON e scrivila nella risposta
            byte[] responseBody = objectMapper.writeValueAsBytes(content);
            DataBuffer buffer = response.bufferFactory().wrap(responseBody);
            return response.writeWith(Flux.just(buffer));
        } catch (Exception e) {
            return writeErrorResponse(exchange, "Failed to write response");
        }
    }

    public Mono<Void> writeResponse(ServerWebExchange exchange, String content) {
        try {
            byte[] responseBytes = content.getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(responseBytes);

            // Imposta lo stato HTTP e restituisci la risposta mock
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (Exception e) {
            return writeErrorResponse(exchange, "Failed to write response");
        }
    }

    public Mono<Void> writeErrorResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.BAD_REQUEST);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        String errorResponse = "{\"error\": \"" + message + "\"}";
        byte[] responseBody = errorResponse.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(responseBody);

        return response.writeWith(Flux.just(buffer));
    }

}
