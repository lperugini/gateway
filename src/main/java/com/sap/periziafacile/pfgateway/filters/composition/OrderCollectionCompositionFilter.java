package com.sap.periziafacile.pfgateway.filters.composition;

import java.util.Optional;

import org.json.JSONArray;
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
public class OrderCollectionCompositionFilter implements GatewayFilter {

    private final MockItemService mockItemService;
    private final MockUserService mockUserService;
    private final ResponseUtil responseUtil = new ResponseUtil();

    public OrderCollectionCompositionFilter(MockItemService mockItemService, MockUserService mockUserService) {
        this.mockItemService = mockItemService;
        this.mockUserService = mockUserService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return WebClient
                .builder()
                .build()
                .get()
                .uri("http://localhost:8083/orders")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(orders -> {
                    JSONObject jsonContent = new JSONObject(orders);

                    JSONArray jsonResponse = new JSONArray();

                    JSONArray orderArray = jsonContent.getJSONObject("_embedded").getJSONArray("orderList");
                    orderArray.forEach(order -> {
                        JSONObject jOrder = new JSONObject(order.toString());

                        if (jOrder.has("item") && jOrder.has("user")) {
                            Long itemid = Long.valueOf(jOrder.get("item").toString());
                            Long userid = Long.valueOf(jOrder.get("user").toString());

                            Optional<JSONObject> optionalUser = mockUserService.getById(userid);
                            Optional<JSONObject> optionalItem = mockItemService.getItemById(itemid);
                            if (optionalUser.isPresent() && optionalItem.isPresent()) {
                                jOrder.put("userInfo", optionalUser.get()).put("itemInfo", optionalItem.get());
                                jsonResponse.put(jOrder);
                            }
                        }
                    });
                    jsonContent.getJSONObject("_embedded").remove("orderList");
                    jsonContent.getJSONObject("_embedded").put("orderList", jsonResponse);

                    return this.responseUtil.writeResponse(exchange,
                            HttpStatus.OK,
                            jsonContent.toString());
                });
    }
}
