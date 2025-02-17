package com.sap.periziafacile.pfgateway.config;

import java.util.List;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.sap.periziafacile.pfgateway.filters.auth.AuthFilter;
import com.sap.periziafacile.pfgateway.filters.auth.LoginFilter;
import com.sap.periziafacile.pfgateway.filters.auth.MockRegisterFilter;
import com.sap.periziafacile.pfgateway.filters.composition.OrderCollectionCompositionFilter;
import com.sap.periziafacile.pfgateway.filters.composition.SingleOrderCompositionFilter;
import com.sap.periziafacile.pfgateway.filters.fallback.FallbackFilter;
import com.sap.periziafacile.pfgateway.filters.item.MockItemFilter;
import com.sap.periziafacile.pfgateway.filters.item.MockSingleItemFilter;
import com.sap.periziafacile.pfgateway.filters.order.OrderPublishFilter;
import com.sap.periziafacile.pfgateway.filters.order.PersonalOrderFilter;
import com.sap.periziafacile.pfgateway.filters.order.UserOrdersFilter;
import com.sap.periziafacile.pfgateway.filters.user.MockPersonalInfoFilter;
import com.sap.periziafacile.pfgateway.filters.user.MockSingleUserFilter;
import com.sap.periziafacile.pfgateway.filters.user.MockUserFilter;

import reactor.core.publisher.Mono;

@Configuration
public class GatewayRoutingConfig {

        private final String GATEWAYURL = "http://localhost:8080";
        private final String ORDERSERVICEURL = "http://localhost:8083";

        @Bean
        RouteLocator customRouteLocator(
                        RouteLocatorBuilder builder,
                        FallbackFilter fallbackFilter,
                        SingleOrderCompositionFilter singleOrderCompositionFilter,
                        OrderCollectionCompositionFilter orderCollectionCompositionFilter,
                        PersonalOrderFilter personalOrderFilter,
                        UserOrdersFilter userOrdersFilter,
                        OrderPublishFilter orderPublishFilter,
                        MockRegisterFilter mockRegisterFilter,
                        LoginFilter loginFilter,
                        MockPersonalInfoFilter mockPersonalInfoFilter,
                        AuthFilter mockAuthFilter,
                        MockSingleUserFilter mockSingleUserFilter,
                        MockSingleItemFilter mockSingleItemFilter,
                        MockUserFilter mockUserFilter,
                        MockItemFilter mockItemFilter) {

                return builder
                                .routes()
                                .route("register", r -> r // no-auth needed
                                                .path("/auth/register")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(mockRegisterFilter))
                                                .uri("no://op"))
                                .route("login", r -> r // no-auth needed
                                                .path("/auth/login")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(loginFilter))
                                                .uri("no://op"))
                                /* ITEMS */
                                .route("getitem", r -> r // no-auth needed
                                                .path("/items/{id}")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f
                                                                .filter(mockSingleItemFilter))
                                                .uri("no://op"))
                                .route("getitems", r -> r // no-auth needed
                                                .path("/items")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f
                                                                .filter(mockItemFilter))
                                                .uri("no://op"))
                                .route("postitem", r -> r
                                                .path("/items")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockItemFilter))
                                                .uri("no://op"))
                                .route("putitem", r -> r
                                                .path("/items/{id}")
                                                .and()
                                                .method(HttpMethod.PUT)
                                                .filters(f -> f
                                                                .filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockSingleItemFilter))
                                                .uri("no://op"))
                                .route("deleteitem", r -> r
                                                .path("/items/{id}")
                                                .and()
                                                .method(HttpMethod.DELETE)
                                                .filters(f -> f.filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockSingleItemFilter))
                                                .uri("no://op"))
                                /* USERS */
                                .route("me", r -> r
                                                .path("/users/me")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(new AuthFilter(
                                                                List.of("user", "admin")))
                                                                .filter(mockPersonalInfoFilter))
                                                .uri("no://op"))
                                .route("getusers", r -> r
                                                .path("/users")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockUserFilter))
                                                .uri("no://op"))
                                .route("user", r -> r
                                                .path("/users/{id}")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockSingleUserFilter))
                                                .uri("no://op"))
                                .route("postuser", r -> r
                                                .path("/users")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockUserFilter))
                                                .uri("no://op"))
                                .route("putuser", r -> r
                                                .path("/users/{id}")
                                                .and()
                                                .method(HttpMethod.PUT)
                                                .filters(f -> f
                                                                .filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockSingleUserFilter))
                                                .uri("no://op"))
                                .route("deleteuser", r -> r
                                                .path("/users/{id}")
                                                .and()
                                                .method(HttpMethod.DELETE)
                                                .filters(f -> f.filter(new AuthFilter(List.of("admin")))
                                                                .filter(mockSingleUserFilter))
                                                .uri("no://op"))
                                /* ORDERS */
                                .route("getorders", r -> r
                                                .path("/orders")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f
                                                                .circuitBreaker(config -> config
                                                                                .setName("ordersCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback"))
                                                                .filter(new AuthFilter(List.of("user", "admin")))
                                                                .filter(userOrdersFilter)
                                                                .filter(orderCollectionCompositionFilter)
                                                                .modifyResponseBody(String.class, String.class,
                                                                                (exchange, originalBody) -> {
                                                                                        return Mono.just(originalBody
                                                                                                        .replace(ORDERSERVICEURL,
                                                                                                                        GATEWAYURL));
                                                                                }))
                                                .uri(ORDERSERVICEURL))
                                .route("order", r -> r
                                                .path("/orders/{id}")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f
                                                                .circuitBreaker(config -> config
                                                                                .setName("ordersCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback"))
                                                                .filter(new AuthFilter(List.of("user", "admin")))
                                                                .filter(personalOrderFilter)
                                                                .filter(singleOrderCompositionFilter)
                                                                .modifyResponseBody(String.class, String.class,
                                                                                (exchange, originalBody) -> {
                                                                                        return Mono.just(originalBody
                                                                                                        .replace(ORDERSERVICEURL,
                                                                                                                        GATEWAYURL));
                                                                                }))
                                                .uri(ORDERSERVICEURL))
                                .route("postorder", r -> r
                                                .path("/orders")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f
                                                                .circuitBreaker(config -> config
                                                                                .setName("ordersCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback"))
                                                                .filter(new AuthFilter(List.of("user")))
                                                                .filter(orderPublishFilter)
                                                                .modifyResponseBody(String.class, String.class,
                                                                                (exchange, originalBody) -> {
                                                                                        return Mono.just(originalBody
                                                                                                        .replace(ORDERSERVICEURL,
                                                                                                                        GATEWAYURL));
                                                                                }))
                                                .uri(ORDERSERVICEURL))
                                .route("putorder", r -> r
                                                .path("/orders/{id}")
                                                .and()
                                                .method(HttpMethod.PUT)
                                                .filters(f -> f
                                                                .circuitBreaker(config -> config
                                                                                .setName("ordersCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback"))
                                                                .filter(new AuthFilter(List.of(
                                                                                "user",
                                                                                "admin")))
                                                                .modifyResponseBody(String.class, String.class,
                                                                                (exchange, originalBody) -> {
                                                                                        return Mono.just(originalBody
                                                                                                        .replace(ORDERSERVICEURL,
                                                                                                                        GATEWAYURL));
                                                                                }))
                                                .uri(ORDERSERVICEURL))
                                .route("deleteorder", r -> r
                                                .path("/orders/{id}")
                                                .and()
                                                .method(HttpMethod.DELETE)
                                                .filters(f -> f
                                                                .circuitBreaker(config -> config
                                                                                .setName("ordersCircuitBreaker")
                                                                                .setFallbackUri("forward:/fallback"))
                                                                .filter(new AuthFilter(List.of("user", "admin")))
                                                                .modifyResponseBody(String.class, String.class,
                                                                                (exchange, originalBody) -> {
                                                                                        return Mono.just(originalBody
                                                                                                        .replace(ORDERSERVICEURL,
                                                                                                                        GATEWAYURL));
                                                                                }))
                                                .uri(ORDERSERVICEURL))
                                .route("fallback", r -> r
                                                .path("/fallback")
                                                .filters(f -> f.filter(fallbackFilter))
                                                .uri("no://op"))
                                .build();
        }

        @Bean
        RestTemplate restTemplate() {
                return new RestTemplate();
        }
}
