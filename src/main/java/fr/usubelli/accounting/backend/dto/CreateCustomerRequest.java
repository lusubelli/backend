package fr.usubelli.accounting.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateCustomerRequest {

    private final String siren;
    private final Organization organization;
    private final String billingContactEmail;

    @JsonCreator
    public CreateCustomerRequest(
            @JsonProperty("siren") String siren,
            @JsonProperty("organization") Organization organization,
            @JsonProperty("billingContactEmail") String billingContactEmail) {
        this.siren = siren;
        this.organization = organization;
        this.billingContactEmail = billingContactEmail;
    }

    public String getSiren() {
        return siren;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getBillingContactEmail() {
        return billingContactEmail;
    }

}
