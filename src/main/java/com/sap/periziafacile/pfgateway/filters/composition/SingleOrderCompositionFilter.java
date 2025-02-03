package com.sap.periziafacile.pfgateway.filters.composition;

import java.util.Optional;

import org.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

import com.sap.periziafacile.pfgateway.services.MockItemService;
import com.sap.periziafacile.pfgateway.services.MockUserService;
import com.sap.periziafacile.pfgateway.utils.ResponseUtil;

import reactor.core.publisher.Mono;

@Component
public class SingleOrderCompositionFilter implements GatewayFilter {

    private final MockItemService mockItemService;
    private final MockUserService mockUserService;
    private final ResponseUtil responseUtil = new ResponseUtil();

    public SingleOrderCompositionFilter(MockItemService mockItemService, MockUserService mockUserService) {
        this.mockItemService = mockItemService;
        this.mockUserService = mockUserService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
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
                    JSONObject jsonResponse = new JSONObject().put("order", jsonOrder);

                    if (jsonOrder.has("item") && jsonOrder.has("user")) {
                        Long itemid = Long.valueOf(jsonOrder.get("item").toString());
                        Long userid = Long.valueOf(jsonOrder.get("user").toString());

                        Optional<JSONObject> optionalUser = mockUserService.getById(userid);
                        Optional<JSONObject> optionalItem = mockItemService.getItemById(itemid);
                        if (optionalUser.isPresent() && optionalItem.isPresent()) {
                            jsonResponse
                                    .put("user", optionalUser.get())
                                    .put("item", optionalItem.get());
                        }
                    }

                    return this.responseUtil.writeResponse(exchange,
                            HttpStatus.OK,
                            jsonResponse.toString());
                });
    }

}
