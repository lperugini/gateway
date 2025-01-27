package com.sap.periziafacile.pfgateway.config;

import java.util.List;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.sap.periziafacile.pfgateway.filters.auth.MockAuthFilter;
import com.sap.periziafacile.pfgateway.filters.auth.MockLoginFilter;
import com.sap.periziafacile.pfgateway.filters.auth.MockRegisterFilter;
import com.sap.periziafacile.pfgateway.filters.item.MockItemFilter;
import com.sap.periziafacile.pfgateway.filters.item.MockSingleItemFilter;
import com.sap.periziafacile.pfgateway.filters.user.MockPeronalInfoFilter;
import com.sap.periziafacile.pfgateway.filters.user.MockSingleUserFilter;
import com.sap.periziafacile.pfgateway.filters.user.MockUserFilter;

@Configuration
public class Config {

        @Bean
        RouteLocator customRouteLocator(
                        RouteLocatorBuilder builder,
                        MockRegisterFilter mockRegisterFilter,
                        MockLoginFilter mockLoginFilter,
                        MockPeronalInfoFilter mockPersonalInfoFilter,
                        MockAuthFilter mockAuthFilter,
                        MockSingleUserFilter mockSingleUserFilter,
                        MockSingleItemFilter mockSingleItemFilter,
                        MockUserFilter mockUserFilter,
                        MockItemFilter mockItemFilter) {

                return builder
                                .routes()
                                .route("register", r -> r // no-auth needed
                                                .path("/register")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(mockRegisterFilter))
                                                .uri("no://op"))
                                .route("login", r -> r // no-auth needed
                                                .path("/login")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(mockLoginFilter))
                                                .uri("no://op"))
                                /* ITEMS */
                                .route("getitem", r -> r // no-auth needed
                                                .path("/items/{id}")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(mockSingleItemFilter))
                                                .uri("no://op"))
                                .route("getitems", r -> r // no-auth needed
                                                .path("/items")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(mockItemFilter))
                                                .uri("no://op"))
                                .route("postitem", r -> r
                                                .path("/items")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockItemFilter))
                                                .uri("no://op"))
                                .route("putitem", r -> r
                                                .path("/items/{id}")
                                                .and()
                                                .method(HttpMethod.PUT)
                                                .filters(f -> f
                                                                .filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockSingleItemFilter))
                                                .uri("no://op"))
                                .route("deleteitem", r -> r
                                                .path("/items/{id}")
                                                .and()
                                                .method(HttpMethod.DELETE)
                                                .filters(f -> f.filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockSingleItemFilter))
                                                .uri("no://op"))
                                /* USERS */
                                .route("me", r -> r
                                                .path("/me")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(new MockAuthFilter(
                                                                List.of("user", "admin", "collaborator")))
                                                                .filter(mockPersonalInfoFilter))
                                                .uri("no://op"))
                                .route("getusers", r -> r
                                                .path("/users")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockUserFilter))
                                                .uri("no://op"))
                                .route("user", r -> r
                                                .path("/users/{id}")
                                                .and()
                                                .method(HttpMethod.GET)
                                                .filters(f -> f.filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockSingleUserFilter))
                                                .uri("no://op"))
                                .route("postuser", r -> r
                                                .path("/users")
                                                .and()
                                                .method(HttpMethod.POST)
                                                .filters(f -> f.filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockUserFilter))
                                                .uri("no://op"))
                                .route("putuser", r -> r
                                                .path("/users/{id}")
                                                .and()
                                                .method(HttpMethod.PUT)
                                                .filters(f -> f
                                                                .filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockSingleUserFilter))
                                                .uri("no://op"))
                                .route("deleteuser", r -> r
                                                .path("/users/{id}")
                                                .and()
                                                .method(HttpMethod.DELETE)
                                                .filters(f -> f.filter(new MockAuthFilter(List.of("admin")))
                                                                .filter(mockSingleUserFilter))
                                                .uri("no://op"))
                                /* FINIRE */

                                /* ORDERS */

                                /* PAYMENTS */

                                .build();
        }

        @Bean
        RestTemplate restTemplate() {
                return new RestTemplate();
        }
}
