package com.sap.periziafacile.pfgateway.filters.order;

import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.sap.periziafacile.pfgateway.messaging.OrderMessageConfig;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class OrderPublishFilter implements GatewayFilter {

    private final RabbitTemplate rabbitTemplate;
    private final ResponseUtil responseUtil = new ResponseUtil();

    public OrderPublishFilter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        return exchange
                .getRequest()
                .getBody()
                .collectList()
                .map(body -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    body.forEach(dataBuffer -> stringBuilder.append(dataBuffer.toString(StandardCharsets.UTF_8)));
                    return stringBuilder.toString();
                })
                .flatMap(body -> {
                    JSONObject orderJson = new JSONObject(body);
                    orderJson.put("timestamp", System.currentTimeMillis()); // Aggiunge un timestamp

                    // Pubblica l'ordine su RabbitMQ
                    rabbitTemplate.convertAndSend(
                            OrderMessageConfig.EXCHANGE_NAME,
                            OrderMessageConfig.ROUTING_KEY,
                            orderJson.toString());

                    return this.responseUtil.writeResponse(exchange,
                            HttpStatus.CREATED,
                            "");

                });
    }

}
