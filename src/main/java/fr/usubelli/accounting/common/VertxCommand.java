package fr.usubelli.accounting.common;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import org.apache.commons.cli.*;

public class VertxCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxCommand.class);

    private final Options options;

    public VertxCommand() {
        this.options = this.initOptions();
    }

    private Options initOptions() {
        Option input = new Option("c", "config", true, "configuration file path");
        input.setRequired(false);

        Option portOption = new Option("p", "port", true, "port");
        portOption.setRequired(false);

        Option authHtpasswdPath = new Option("htp", "auth-htpasswd-path", true, "auth htpasswd path");
        authHtpasswdPath.setRequired(false);

        Option sslKeystoreOption = new Option("sslk", "ssl-keystore", true, "ssl keystore mandatory if ssl is activated");
        sslKeystoreOption.setRequired(false);

        Option sslPasswordOption = new Option("sslp", "ssl-password", true, "ssl password mandatory if ssl is activated");
        sslPasswordOption.setRequired(false);

        Options options = new Options();
        options.addOption(input);
        options.addOption(portOption);
        options.addOption(authHtpasswdPath);
        options.addOption(sslKeystoreOption);
        options.addOption(sslPasswordOption);
        return options;
    }

    public VertxConfiguration parse(String[] args) throws ParseException {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            LOGGER.info(e.getMessage());
            formatter.printHelp("User Micro Service", options);
            throw e;
        }
        return new VertxConfiguration(cmd);
    }
}
