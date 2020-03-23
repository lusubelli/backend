package fr.usubelli.accounting.common;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class Configuration {

    private final Config config;

    public Configuration(Config config) {
        this.config = config;
    }

    protected String getString(final String key, final String defaultValue) {
        String value;
        try {
            value = this.config.getString(key);
        } catch (ConfigException.Missing e) {
            value = defaultValue;
        }
        return value;
    }

}
