package com.sap.periziafacile.pfgateway;

import static org.assertj.core.api.Assertions.assertThat;
import org.json.JSONArray;
import org.json.JSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.sap.periziafacile.pfgateway.utils.JwtUtil;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ExtendWith(MockitoExtension.class) // per usare Mockito
public class APIGatewayTest {

    @Autowired
    private WebTestClient webTestClient; // Simula il client HTTP

    @Test
    void testItemsService() throws Exception {

        String responseBody = webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        JSONArray items = new JSONObject(responseBody).getJSONObject("_embedded").getJSONArray("itemList");

        assertThat(items).hasSize(3);
    }

    @Test
    void testUserAuthorization() throws Exception {

        assertThat(webTestClient
                .get()
                .uri("/users")
                .exchange()
                .expectStatus()
                .isUnauthorized());
    }

    @Test
    void testOrdersAuthorization() throws Exception {

        assertThat(webTestClient
                .get()
                .uri("/orders")
                .exchange()
                .expectStatus()
                .isUnauthorized());
    }

    @Test
    void testLogin() throws Exception {

        String responseBody = webTestClient.post()
                .uri("/auth/login")
                .bodyValue(new JSONObject()
                        .put("username", "leonardo")
                        .put("password", "leonardo1")
                        .toString())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String token = new JSONObject(responseBody).getString("token");

        assertThat(new JwtUtil().validateToken(token));
    }

    @Test
    void testRetrievePersonalInformation() throws Exception {

        String responseBody = webTestClient.post()
                .uri("/auth/login")
                .bodyValue(new JSONObject()
                        .put("username", "leonardo")
                        .put("password", "leonardo1")
                        .toString())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        String token = new JSONObject(responseBody).getString("token");
        assertThat(new JwtUtil().validateToken(token));

        responseBody = webTestClient.get()
                .uri("/users/me")
                .header("Authorization", "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        JSONObject user = new JSONObject(responseBody);

        assertEquals(user.getInt("id"), 3);
        assertEquals(user.getString("e_mail"), "leonardo.perugini2@studio.unibo.it");
    }

}
