package fr.usubelli.accounting.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

    public enum UserState {
        CREATED
    }

    private final String email;
    private final String password;
    private final OrganisationRights rights;
    private final UserState state;

    @JsonCreator
    public User(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("rights") OrganisationRights rights,
            @JsonProperty("state") UserState state) {
        this.email = email;
        this.password = password;
        this.rights = rights;
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public OrganisationRights getRights() {
        return rights;
    }

    public UserState getState() {
        return state;
    }

}
