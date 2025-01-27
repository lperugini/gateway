package com.sap.periziafacile.pfgateway.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MockItemService {

        private final List<JSONObject> items = new ArrayList<>();
        BCryptPasswordEncoder passwordEncoder;

        public MockItemService() {
                this.passwordEncoder = new BCryptPasswordEncoder();

                JSONObject jsonItem1 = new JSONObject()
                                .put("id", 1L)
                                .put("code", "PFVEICOLI")
                                .put("description", "Perizia su veicoli effettuata da nostro specialista.")
                                .put("name", "Perizia veicoli")
                                .put("price", 50)
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("href", "/items/1"))
                                                .put("update", new JSONObject()
                                                                .put("href", "/items/1")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "/items/1")
                                                                .put("method", "DELETE")));

                JSONObject jsonItem2 = new JSONObject()
                                .put("id", 2L)
                                .put("code", "PFIMMOBILI")
                                .put("description", "Perizia su immobili effettuata da nostro specialista.")
                                .put("name", "Perizia immobili")
                                .put("price", 50)
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("href", "/items/2"))
                                                .put("update", new JSONObject()
                                                                .put("href", "/items/2")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "/items/2")
                                                                .put("method", "DELETE")));

                JSONObject jsonItem3 = new JSONObject()
                                .put("id", 3L)
                                .put("code", "PFMEDICA")
                                .put("description", "Perizia medica effettuata da nostro specialista.")
                                .put("name", "Perizia medica")
                                .put("price", 50)
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("href", "/items/3"))
                                                .put("update", new JSONObject()
                                                                .put("href", "/items/3")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "/items/3")
                                                                .put("method", "DELETE")));

                items.add(jsonItem1);
                items.add(jsonItem2);
                items.add(jsonItem3);

        }

        public List<JSONObject> getAllItems() {
                return items;
        }

        public Optional<JSONObject> getItemById(Long id) {
                return items.stream().filter(item -> item.get("id").equals(id)).findFirst();
        }

        public Optional<JSONObject> addItem(JSONObject newItem) {
                try {
                        String code = newItem.getString("code");
                        String description = newItem.getString("description");
                        String name = newItem.getString("name");
                        Integer price = newItem.getInt("price");

                        Long id = Long.sum(
                                        items
                                                        .stream()
                                                        .map(i -> i.getLong("id"))
                                                        .max(Comparator.naturalOrder())
                                                        .orElse(0L),
                                        1L);

                        JSONObject jsonItem = new JSONObject()
                                        .put("id", id)
                                        .put("code", code)
                                        .put("description", description)
                                        .put("name", name)
                                        .put("price", price)
                                        .put("_links", new JSONObject()
                                                        .put("self", new JSONObject()
                                                                        .put("href", "/items/" + id.toString()))
                                                        .put("update", new JSONObject()
                                                                        .put("href", "/items/" + id.toString())
                                                                        .put("method", "PUT"))
                                                        .put("delete", new JSONObject()
                                                                        .put("href", "/items/" + id.toString())
                                                                        .put("method", "DELETE")));
                        items.add(jsonItem);
                        return Optional.of(jsonItem);
                } catch (Exception e) {
                        return Optional.empty();
                }
        }

        public Optional<JSONObject> updateItem(Long id, JSONObject updatedItem) {
                Optional<JSONObject> existingItem = getItemById(id);
                if (existingItem.isPresent()) {

                        String code = updatedItem.getString("code");
                        String description = updatedItem.getString("description");
                        String name = updatedItem.getString("name");
                        Integer price = updatedItem.getInt("price");

                        JSONObject jsonItem = new JSONObject()
                                        .put("id", id)
                                        .put("code", code)
                                        .put("description", description)
                                        .put("name", name)
                                        .put("price", price)
                                        .put("_links", new JSONObject()
                                                        .put("self", new JSONObject()
                                                                        .put("href", "/items/" + id.toString()))
                                                        .put("update", new JSONObject()
                                                                        .put("href", "/items/" + id.toString())
                                                                        .put("method", "PUT"))
                                                        .put("delete", new JSONObject()
                                                                        .put("href", "/items/" + id.toString())
                                                                        .put("method", "DELETE")));

                        items.removeIf(item -> item.get("id").equals(id));
                        items.add(jsonItem);
                        return Optional.of(jsonItem);
                }
                return Optional.empty();
        }

        public boolean deleteItem(Long id) {
                return items.removeIf(item -> item.get("id").equals(id));
        }
}
