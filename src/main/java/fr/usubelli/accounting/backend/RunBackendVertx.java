package fr.usubelli.accounting.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.usubelli.accounting.backend.adapter.rest.OrganizationRestClient;
import fr.usubelli.accounting.backend.adapter.rest.UserRestClient;
import fr.usubelli.accounting.common.*;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.cli.ParseException;

public class RunBackendVertx {

    private static final String DEFAULT_APPLICATION_CONF = "src/main/resources/application.conf";
    private static final Logger LOGGER = LoggerFactory.getLogger(RunBackendVertx.class);

    public static void main(String[] args) {

        Configuration configuration;
        try {
            configuration = MicroServiceCommand.parse(args, DEFAULT_APPLICATION_CONF);
        } catch (ParseException e) {
            System.exit(1);
            return;
        }

        if (configuration == null) {
            System.exit(1);
            return;
        }

        ObjectMapper serverObjectMapper = new ObjectMapper();
        serverObjectMapper.registerModule(new JavaTimeModule());
        serverObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        ObjectMapper clientObjectMapper = new ObjectMapper();
        clientObjectMapper.registerModule(new JavaTimeModule());
        clientObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final UserRestClient userGateway = new UserRestClient(
                new RestConfiguration(configuration.getString("user-api.url", "http://localhost:9000"))
                        .basic(configuration.getConfiguration("user-api.basic", null)), clientObjectMapper);
        final OrganizationRestClient organisationGateway = new OrganizationRestClient(
                new RestConfiguration(configuration.getString("organization-api.url", "http://localhost:9001"))
                        .basic(configuration.getConfiguration("organization-api.basic", null)), clientObjectMapper);

        final VertxMicroService microService = new BackendVertx(serverObjectMapper,
                userGateway, organisationGateway);

        VertxServer.create(
                new MicroServiceConfiguration(configuration.getInt("http.port", 8080))
                        .basic(configuration.getConfiguration("http.basic", null))
                        .ssl(configuration.getConfiguration("http.ssl", null))
                        .jwt(configuration.getConfiguration("http.jwt", null)))
                .start(microService);

    }

}
