package fr.usubelli.accounting.common;

public class MicroServiceConfiguration {

    private final int port;
    private BasicAuth basicAuth;
    private Ssl ssl;
    private Jwt jwt;

    public MicroServiceConfiguration(int port) {
        this.port = port;
    }

    public MicroServiceConfiguration basic(Configuration basicAuthConfiguration) {
        if (basicAuthConfiguration != null) {
            final String path = basicAuthConfiguration.getString("path");
            final String realm = basicAuthConfiguration.getString("realm");
            this.basicAuth = new BasicAuth(path, realm);
        }
        return this;
    }

    public MicroServiceConfiguration ssl(Configuration sslConfiguration) {
        if (sslConfiguration != null) {
            final String path = sslConfiguration.getString("path");
            final String password = sslConfiguration.getString("password");
            this.ssl = new Ssl(path, password);
        }
        return this;
    }

    public MicroServiceConfiguration jwt(Configuration sslConfiguration) {
        if (sslConfiguration != null) {
            final String algorithm = sslConfiguration.getString("algorithm");
            final String password = sslConfiguration.getString("password");
            this.jwt = new Jwt(algorithm, password);
        }
        return this;
    }

    boolean hasBasicAuth() {
        return basicAuth != null;
    }

    boolean hasSsl() {
        return ssl != null;
    }

    boolean hasJwt() {
        return jwt != null;
    }

    BasicAuth basic() {
        return basicAuth;
    }

    Ssl ssl() {
        return ssl;
    }

    Jwt jwt() {
        return jwt;
    }

    public int getPort() {
        return port;
    }

    public class Ssl {

        private final String path;
        private final String password;

        Ssl(String path, String password) {
            this.path = path;
            this.password = password;
        }

        String getPath() {
            return this.path;
        }

        String getPassword() {
            return this.password;
        }

    }

    public class Jwt {

        private final String algorithm;
        private final String password;

        Jwt(String algorithm, String password) {
            this.algorithm = algorithm;
            this.password = password;
        }

        String getAlgorithm() {
            return this.algorithm;
        }

        String getPassword() {
            return this.password;
        }

        boolean getSymetric() {
            return true;
        }

    }

    public class BasicAuth {

        private final String path;
        private final String realm;

        BasicAuth(String path, String realm) {
            this.path = path;
            this.realm = realm;
        }

        String getPath() {
            return path;
        }

        String getRealm() {
            return realm;
        }

    }

}
