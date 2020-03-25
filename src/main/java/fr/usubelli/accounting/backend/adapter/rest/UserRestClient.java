package fr.usubelli.accounting.backend.adapter.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.usubelli.accounting.backend.RestConfiguration;
import fr.usubelli.accounting.backend.dto.User;
import fr.usubelli.accounting.backend.exception.UserAlreadyExistsException;
import fr.usubelli.accounting.backend.port.UserGateway;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;

public class UserRestClient implements UserGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestClient.class);
    private static final String USER_CONTEXT_PATH = "/user";

    private final RestConfiguration restConfiguration;
    private final ObjectMapper objectMapper;

    public UserRestClient(RestConfiguration restConfiguration, ObjectMapper objectMapper) {
        this.restConfiguration = restConfiguration;
        this.objectMapper = objectMapper;

        LOGGER.info(String.format("\tURL : %s", restConfiguration.url()));
        LOGGER.info(String.format("\tHTPASSWD : %s", restConfiguration.hasBasicAuth()));
    }

    public User createUser(User user) throws UserAlreadyExistsException {

        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(this.restConfiguration.url() + USER_CONTEXT_PATH)
                    .basicAuth(this.restConfiguration.basic())
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
                    .url(this.restConfiguration.url() + USER_CONTEXT_PATH + "/" + email)
                    .basicAuth(this.restConfiguration.basic())
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
                    .url(this.restConfiguration.url() + USER_CONTEXT_PATH)
                    .basicAuth(this.restConfiguration.basic())
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
