package fr.usubelli.accounting.backend.adapter.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.usubelli.accounting.backend.dto.Organization;
import fr.usubelli.accounting.backend.exception.OrganisationAlreadyExistsException;
import fr.usubelli.accounting.backend.port.OrganizationGateway;

import java.io.IOException;

public class OrganizationRestClient implements OrganizationGateway {

    private static final String ORGANIZATION_CONTEXT_PATH = "/organization";

    private final String url;
    private final String login;
    private final String password;
    private final ObjectMapper objectMapper;

    public OrganizationRestClient(
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

    @Override
    public Organization createOrganisation(Organization organization) throws OrganisationAlreadyExistsException {
        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(url + ORGANIZATION_CONTEXT_PATH)
                    .basicAuth(this.login, this.password)
                    .post(organizationToJson(organization))
                    .send();
        } catch (IOException e) {
            throw new RuntimeException("Impossible to execute request", e);
        }

        if (response.code() == 404 || response.payload() == null) {
            return null;
        }

        if (response.code() == 409) {
            throw new OrganisationAlreadyExistsException();
        }

        return jsonToOrganization(response);
    }

    @Override
    public Organization findOrganisation(String siren) {

        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(url + ORGANIZATION_CONTEXT_PATH + "/" + siren)
                    .basicAuth(this.login, this.password)
                    .get()
                    .send();
        } catch (IOException e) {
            throw new RuntimeException("Impossible to execute request", e);
        }

        if (response.code() == 404 || response.payload() == null) {
            return null;
        }

        return jsonToOrganization(response);
    }

    @Override
    public Organization updateOrganisation(Organization organization) {

        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(url + ORGANIZATION_CONTEXT_PATH)
                    .basicAuth(this.login, this.password)
                    .put(organizationToJson(organization))
                    .send();
        } catch (IOException e) {
            throw new RuntimeException("Impossible to execute request", e);
        }

        if (response.code() == 404 || response.payload() == null) {
            return null;
        }

        return jsonToOrganization(response);
    }


    private String organizationToJson(Organization organization) {
        final String json;
        try {
            json = objectMapper.writeValueAsString(organization);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(String.format("Impossible to serialize request %s", organization), e);
        }
        return json;
    }

    private Organization jsonToOrganization(RestResponse response) {
        try {
            return objectMapper.readValue(response.payload(), Organization.class);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Impossible to deserialize response %s", response.payload()));
        }
    }

}
