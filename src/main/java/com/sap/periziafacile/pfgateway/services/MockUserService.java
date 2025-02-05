package com.sap.periziafacile.pfgateway.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MockUserService {

        private final List<JSONObject> users = new ArrayList<>();
        BCryptPasswordEncoder passwordEncoder;

        public MockUserService() {
                this.passwordEncoder = new BCryptPasswordEncoder();

                JSONObject jsonUser1 = new JSONObject()
                                .put("id", 1L)
                                .put("firstName", "Frodo")
                                .put("lastName", "Baggins")
                                .put("role", "user")
                                .put("e_mail", "fb@lotr.com")
                                .put("username", "fbaggins")
                                .put("password", passwordEncoder.encode("fbaggins"))
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("method", "GET")
                                                                .put("href", "http://localhost:8080/users/1"))
                                                .put("update", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/1")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/1")
                                                                .put("method", "DELETE")));

                JSONObject jsonUser2 = new JSONObject()
                                .put("id", 2L)
                                .put("firstName", "Bilbo")
                                .put("lastName", "Baggins")
                                .put("role", "user")
                                .put("e_mail", "bb@lotr.com")
                                .put("username", "bbaggins")
                                .put("password", passwordEncoder.encode("bbaggins"))
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("method", "GET")
                                                                .put("href", "http://localhost:8080/users/2"))
                                                .put("update", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/2")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/2")
                                                                .put("method", "DELETE")));

                JSONObject jsonUser3 = new JSONObject()
                                .put("id", 3L)
                                .put("firstName", "Leonardo")
                                .put("lastName", "Perugini")
                                .put("role", "admin")
                                .put("e_mail", "leonardo.perugini2@studio.unibo.it")
                                .put("username", "leonardo")
                                .put("password", passwordEncoder.encode("leonardo1"))
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("method", "GET")
                                                                .put("href", "http://localhost:8080/users/3"))
                                                .put("update", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/3")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/2")
                                                                .put("method", "DELETE")));

                JSONObject jsonUser4 = new JSONObject()
                                .put("id", 4L)
                                .put("firstName", "Alfonso")
                                .put("lastName", "Casagrande")
                                .put("role", "collaborator")
                                .put("e_mail", "alfonso@casagrande.com")
                                .put("username", "acasagrande")
                                .put("password", passwordEncoder.encode("casagrande"))
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("method", "GET")
                                                                .put("href", "http://localhost:8080/users/4"))
                                                .put("update", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/4")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/4")
                                                                .put("method", "DELETE")));

                JSONObject jsonUser5 = new JSONObject()
                                .put("id", 5L)
                                .put("firstName", "Lorenzo")
                                .put("lastName", "De Medici")
                                .put("role", "collaborator")
                                .put("e_mail", "lorenzo@demedici.com")
                                .put("username", "lmedici")
                                .put("password", passwordEncoder.encode("medici"))
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("method", "GET")
                                                                .put("href", "http://localhost:8080/users/5"))
                                                .put("update", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/5")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/5")
                                                                .put("method", "DELETE")));

                JSONObject jsonUser6 = new JSONObject()
                                .put("id", 6L)
                                .put("firstName", "Enzo")
                                .put("lastName", "Ferrari")
                                .put("role", "collaborator")
                                .put("e_mail", "enzo@ferrari.com")
                                .put("username", "eferrari")
                                .put("password", passwordEncoder.encode("ferrari"))
                                .put("_links", new JSONObject()
                                                .put("self", new JSONObject()
                                                                .put("method", "GET")
                                                                .put("href", "http://localhost:8080/users/6"))
                                                .put("update", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/6")
                                                                .put("method", "PUT"))
                                                .put("delete", new JSONObject()
                                                                .put("href", "http://localhost:8080/users/6")
                                                                .put("method", "DELETE")));

                users.add(jsonUser1);
                users.add(jsonUser2);
                users.add(jsonUser3);
                users.add(jsonUser4);
                users.add(jsonUser5);
                users.add(jsonUser6);

        }

        public List<JSONObject> getAllUsers() {
                return users;
        }

        public Optional<JSONObject> getById(Long id) {
                return users.stream().filter(user -> user.get("id").equals(id)).findFirst();
        }

        public Optional<JSONObject> getByUsername(String username) {
                return users.stream().filter(user -> user.get("username").equals(username)).findFirst();
        }

        public Optional<JSONObject> addUser(JSONObject newUser) {
                String password = newUser.getString("password");

                try {
                        String username = newUser.getString("username");

                        Long id = Long.sum(
                                        users
                                                        .stream()
                                                        .map(i -> i.getLong("id"))
                                                        .max(Comparator.naturalOrder())
                                                        .orElse(0L),
                                        1L);

                        JSONObject jsonUser = new JSONObject()
                                        .put("id", id)
                                        .put("username", username)
                                        .put("password", passwordEncoder.encode(password))
                                        .put("_links", new JSONObject()
                                                        .put("self", new JSONObject()
                                                                        .put("method", "GET")
                                                                        .put("href", "http://localhost:8080/users/"
                                                                                        + id.toString()))
                                                        .put("update", new JSONObject()
                                                                        .put("href", "http://localhost:8080/users/"
                                                                                        + id.toString())
                                                                        .put("method", "PUT"))
                                                        .put("delete", new JSONObject()
                                                                        .put("href", "http://localhost:8080/users/"
                                                                                        + id.toString())
                                                                        .put("method", "DELETE")));
                        users.add(jsonUser);
                        return Optional.of(jsonUser);
                } catch (Exception e) {
                        return Optional.empty();
                }
        }

        public Optional<JSONObject> updateUser(Long id, JSONObject updatedUser) {
                Optional<JSONObject> existingUser = getById(id);
                if (existingUser.isPresent()) {

                        String firstName = updatedUser.getString("firstName");
                        String lastName = updatedUser.getString("lastName");
                        String email = updatedUser.getString("email");

                        JSONObject jsonUser = new JSONObject()
                                        .put("id", id)
                                        .put("firstName", firstName)
                                        .put("lastName", lastName)
                                        .put("e_mail", email)
                                        .put("role", existingUser.get().getString("role"))
                                        .put("username", existingUser.get().getString("username"))
                                        .put("password", existingUser.get().getString("password"))
                                        .put("_links", new JSONObject()
                                                        .put("self", new JSONObject()
                                                                        .put("href", "http://localhost:8080/users/"
                                                                                        + id.toString()))
                                                        .put("update", new JSONObject()
                                                                        .put("href", "http://localhost:8080/users/"
                                                                                        + id.toString())
                                                                        .put("method", "PUT"))
                                                        .put("delete", new JSONObject()
                                                                        .put("href", "http://localhost:8080/users/"
                                                                                        + id.toString())
                                                                        .put("method", "DELETE")));

                        users.removeIf(user -> user.get("id").equals(id));
                        users.add(jsonUser);
                        return Optional.of(jsonUser);
                }
                return Optional.empty();
        }

        public boolean deleteUser(Long id) {
                return users.removeIf(user -> user.get("id").equals(id));
        }
}
