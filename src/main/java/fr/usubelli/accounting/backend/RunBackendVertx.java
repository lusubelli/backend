package fr.usubelli.accounting.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import fr.usubelli.accounting.backend.adapter.rest.OrganizationRestClient;
import fr.usubelli.accounting.backend.adapter.rest.UserRestClient;
import fr.usubelli.accounting.common.VertxCommand;
import fr.usubelli.accounting.common.VertxConfiguration;
import fr.usubelli.accounting.common.VertxServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.cli.ParseException;

import java.io.File;

public class RunBackendVertx {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunBackendVertx.class);

    public static void main(String[] args) {

        VertxConfiguration vertxConfiguration;
        try {
            vertxConfiguration = new VertxCommand().parse(args);
        } catch (ParseException e) {
            System.exit(1);
            return;
        }

        LOGGER.info(String.format("Loading config from %s", vertxConfiguration.getConfigPath()));
        LOGGER.info(String.format("HTTPS : %s", vertxConfiguration.getSslKeystorePath() != null));
        LOGGER.info(String.format("HTPASSWD : %s", vertxConfiguration.getAuthHtpasswdPath() != null));

        Config configuration = ConfigFactory.empty();
        final File configFile = new File(vertxConfiguration.getConfigPath());
        if (configFile.exists()) {
            configuration = ConfigFactory.parseFile(configFile).resolve();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        final VertxServer vertxServer = VertxServer.create();
        vertxServer.htpasswd(vertxConfiguration.getAuthHtpasswdRealm(), vertxConfiguration.getAuthHtpasswdPath());
        if (vertxConfiguration.getSslKeystorePath() != null) {
            vertxServer.ssl(vertxConfiguration.getSslKeystorePath(), vertxConfiguration.getSslPassword());
        }
        vertxServer.start(new BackendVertx(vertxServer.createJwtAuth(), objectMapper,
                new UserRestClient("http://localhost:8585", "login", "password"),
                new OrganizationRestClient("http://localhost:8686", "login", "password")), vertxConfiguration.getPort());

    }


}
