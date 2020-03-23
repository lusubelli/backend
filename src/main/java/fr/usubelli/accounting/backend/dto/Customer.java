package fr.usubelli.accounting.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Customer {

    private final String siren;
    private final String billingContactEmail;

    @JsonCreator
    public Customer(
            @JsonProperty("siren") String siren,
            @JsonProperty("billingContactEmail") String billingContactEmail) {
        this.siren = siren;
        this.billingContactEmail = billingContactEmail;
    }

    public String getSiren() {
        return siren;
    }

    public String getBillingContactEmail() {
        return billingContactEmail;
    }

}
