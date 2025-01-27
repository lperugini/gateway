package com.sap.periziafacile.pfgateway.services;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class MockAuthService {

        private final List<JSONObject> tokens = new ArrayList<>();

        public MockAuthService() {

                JSONObject token = new JSONObject()
                                .put("userid", 1L)
                                .put("role", "user")
                                .put("valid", Timestamp.from(Instant.now().plus(Duration.ofDays(1))));

                tokens.add(token);
        }

        public Optional<JSONObject> getTokenByUserId(Long id) {
                return tokens.stream().filter(item -> item.get("userid").equals(id)).findFirst();
        }

        public Optional<JSONObject> getUserByToken(String token) {
                return tokens.stream().filter(item -> item.get("token").equals(token)).findFirst();
        }

        public void addToken(String username, String role) {
                JSONObject token = new JSONObject()
                                .put("userid", 1L)
                                .put("role", "user")
                                .put("valid", Timestamp.from(Instant.now().plus(Duration.ofDays(1))));
                tokens.add(token);
        }

        public boolean deleteByUserId(Long id) {
                return tokens.removeIf(item -> item.get("userid").equals(id));
        }
}
