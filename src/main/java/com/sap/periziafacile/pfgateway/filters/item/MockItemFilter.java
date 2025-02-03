package com.sap.periziafacile.pfgateway.filters.item;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONArray;
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
import com.sap.periziafacile.pfgateway.services.MockItemService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class MockItemFilter implements GatewayFilter {

        private final ObjectMapper objectMapper = new ObjectMapper(); // Per deserializzare JSON
        private final MockItemService mockItemService;
        private final ResponseUtil responseUtil = new ResponseUtil();

        public MockItemFilter(MockItemService mockItemService) {
                this.mockItemService = mockItemService;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                if (exchange.getRequest().getMethod() == HttpMethod.POST) {
                        return addItem(exchange);
                }
                return returnAll(exchange);
        }

        private Mono<Void> addItem(ServerWebExchange exchange) {
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

                                                Optional<JSONObject> optionalItem = mockItemService
                                                                .addItem(new JSONObject(item));

                                                if (optionalItem.isEmpty()) {
                                                        return this.responseUtil.writeErrorResponse(exchange,
                                                                        HttpStatus.NOT_FOUND,
                                                                        "Error.");
                                                }

                                                return this.responseUtil.writeResponse(exchange,
                                                                HttpStatus.CREATED,
                                                                optionalItem.get().toString());

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
                List<JSONObject> items = mockItemService.getAllItems();

                JSONArray itemList = new JSONArray();

                itemList.putAll(items);

                JSONObject jsonResponse = new JSONObject().put("_embedded",
                                new JSONObject()
                                                .put("itemList", itemList));

                return this.responseUtil.writeResponse(exchange,
                                HttpStatus.OK,
                                jsonResponse.toString());
        }

}
