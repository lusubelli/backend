package fr.usubelli.accounting.common;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class Configuration {

    private final Config config;

    public Configuration(Config config) {
        this.config = config;
    }

    public String getString(String key) {
        String value = null;
        try {
            value = this.config.getString(key);
        } catch (ConfigException.Missing e) {}
        return value;
    }

    public String getString(final String key, final String defaultValue) {
        String value;
        try {
            value = this.config.getString(key);
        } catch (ConfigException.Missing e) {
            value = defaultValue;
        }
        return value;
    }

    public int getInt(final String key, final int defaultValue) {
        int value;
        try {
            value = this.config.getInt(key);
        } catch (ConfigException.Missing e) {
            value = defaultValue;
        }
        return value;
    }

    public Configuration getConfiguration(final String key, final Configuration defaultValue) {
        Configuration value;
        try {
            value = new Configuration(this.config.getConfig(key));
        } catch (ConfigException.Missing e) {
            value = defaultValue;
        }
        return value;
    }

}
