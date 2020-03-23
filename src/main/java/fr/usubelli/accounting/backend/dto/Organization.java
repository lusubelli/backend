package fr.usubelli.accounting.backend.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Organization {

    private final String name;
    private final String address;
    private final String zipcode;
    private final String town;
    private final String siren;
    private final String tva;
    private final List<Customer> customers;

    @JsonCreator
    public Organization(
            @JsonProperty("name") String name,
            @JsonProperty("address") String address,
            @JsonProperty("zipcode") String zipcode,
            @JsonProperty("town") String town,
            @JsonProperty("siren") String siren,
            @JsonProperty("tva") String tva,
            @JsonProperty("customers") List<Customer> customers) {
        this.name = name;
        this.address = address;
        this.zipcode = zipcode;
        this.town = town;
        this.siren = siren;
        this.tva = tva;
        this.customers = customers;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getTown() {
        return town;
    }

    public String getSiren() {
        return siren;
    }

    public String getTva() {
        return tva;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

}
