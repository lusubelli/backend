package fr.usubelli.accounting.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAccountWithOrganisationRequest {

    private final Account account;
    private final Organization organization;

    @JsonCreator
    public CreateAccountWithOrganisationRequest(
            @JsonProperty("account") Account account,
            @JsonProperty("organization") Organization organization) {
        this.account = account;
        this.organization = organization;
    }

    public Account getAccount() {
        return account;
    }

    public Organization getOrganization() {
        return organization;
    }

}
