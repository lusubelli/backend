package fr.usubelli.accounting.backend.adapter.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.usubelli.accounting.backend.dto.User;
import fr.usubelli.accounting.backend.exception.UserAlreadyExistsException;
import fr.usubelli.accounting.backend.port.UserGateway;

import java.io.IOException;

public class UserRestClient implements UserGateway {

    private static final String USER_CONTEXT_PATH = "/user";

    private final String url;
    private final String login;
    private final String password;
    private final ObjectMapper objectMapper;

    public UserRestClient(
            final String url,
            final String login,
            final String password) {
        this.url = url;
        this.login = login;
        this.password = password;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public User createUser(User user) throws UserAlreadyExistsException {

        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(url + USER_CONTEXT_PATH)
                    .basicAuth(this.login, this.password)
                    .post(userToJson(user))
                    .send();
        } catch (IOException e) {
            throw new RuntimeException("Impossible to execute request", e);
        }

        if (response.code() == 404 || response.payload() == null) {
            return null;
        }

        if (response.code() == 409) {
            throw new UserAlreadyExistsException();
        }

        return jsonToUser(response);

    }

    public User findUser(String email) {

        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(url + USER_CONTEXT_PATH + "/" + email)
                    .basicAuth(this.login, this.password)
                    .get()
                    .send();
        } catch (IOException e) {
            throw new RuntimeException("Impossible to execute request", e);
        }

        if (response.code() == 404 || response.payload() == null) {
            return null;
        }

        return jsonToUser(response);

    }

    public User updateUser(User user) {

        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(url + USER_CONTEXT_PATH)
                    .basicAuth(this.login, this.password)
                    .put(userToJson(user))
                    .send();
        } catch (IOException e) {
            throw new RuntimeException("Impossible to execute request", e);
        }

        if (response.code() == 404 || response.payload() == null) {
            return null;
        }

        return jsonToUser(response);

    }

    private String userToJson(User user) {
        final String json;
        try {
            json = objectMapper.writeValueAsString(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Impossible to serialize request %s", user), e);
        }
        return json;
    }

    private User jsonToUser(RestResponse response) {
        try {
            return objectMapper.readValue(response.payload(), User.class);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Impossible to deserialize response %s", response.payload()));
        }
    }

}
