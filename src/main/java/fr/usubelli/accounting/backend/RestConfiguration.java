package fr.usubelli.accounting.backend;

import fr.usubelli.accounting.common.Configuration;

public class RestConfiguration {
    
    private final String url;
    private RestConfiguration.BasicAuth basicAuth;

    RestConfiguration(final String url) {
        this.url = url;
    }

    RestConfiguration basic(Configuration basicAuthConfiguration) {
        if (basicAuthConfiguration != null) {
            final String username = basicAuthConfiguration.getString("username");
            final String password = basicAuthConfiguration.getString("password");
            this.basicAuth = new RestConfiguration.BasicAuth(username, password);
        }
        return this;
    }

    public RestConfiguration.BasicAuth basic() {
        return this.basicAuth;
    }

    public boolean hasBasicAuth() {
        return this.basicAuth != null;
    }

    public String url() {
        return this.url;
    }

    public static class BasicAuth {

        private final String username;
        private final String password;

        BasicAuth(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String username() {
            return username;
        }

        public String password() {
            return password;
        }

    }

}
