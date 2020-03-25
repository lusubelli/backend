package fr.usubelli.accounting.backend.adapter.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.usubelli.accounting.backend.RestConfiguration;
import fr.usubelli.accounting.backend.dto.Organization;
import fr.usubelli.accounting.backend.exception.OrganisationAlreadyExistsException;
import fr.usubelli.accounting.backend.port.OrganizationGateway;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.IOException;

public class OrganizationRestClient implements OrganizationGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationRestClient.class);
    private static final String ORGANIZATION_CONTEXT_PATH = "/organization";

    private final ObjectMapper objectMapper;
    private final RestConfiguration restConfiguration;

    public OrganizationRestClient(RestConfiguration restConfiguration, ObjectMapper objectMapper) {
        this.restConfiguration = restConfiguration;
        this.objectMapper = objectMapper;
        LOGGER.info(String.format("\tURL : %s", restConfiguration.url()));
        LOGGER.info(String.format("\tHTPASSWD : %s", restConfiguration.hasBasicAuth()));
    }

    @Override
    public Organization createOrganisation(Organization organization) throws OrganisationAlreadyExistsException {
        RestResponse response;
        try {
            response = OkHttpRestClient
                    .url(this.restConfiguration.url() + ORGANIZATION_CONTEXT_PATH)
                    .basicAuth(this.restConfiguration.basic())
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
                    .url(this.restConfiguration.url() + ORGANIZATION_CONTEXT_PATH + "/" + siren)
                    .basicAuth(this.restConfiguration.basic())
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
                    .url(this.restConfiguration.url() + ORGANIZATION_CONTEXT_PATH)
                    .basicAuth(this.restConfiguration.basic())
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
