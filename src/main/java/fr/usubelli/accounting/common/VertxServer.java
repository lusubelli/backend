package fr.usubelli.accounting.common;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;

public class VertxServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);

    private final Vertx vertx;
    private final VertxRouterBuilder routerBuilder;
    private final MicroServiceConfiguration microServiceConfiguration;
    private final HttpServerOptions httpServerOptions;
    private JWTAuth jwtAuth;

    VertxServer(Vertx vertx, VertxRouterBuilder routerBuilder, MicroServiceConfiguration microServiceConfiguration) {
        this.vertx = vertx;
        this.routerBuilder = routerBuilder;
        this.microServiceConfiguration = microServiceConfiguration;
        this.httpServerOptions = new HttpServerOptions();
    }

    VertxServer htpasswd() {
        LOGGER.info(String.format("HTPASSWD : %s", microServiceConfiguration.hasBasicAuth()));
        if (this.microServiceConfiguration.hasBasicAuth()) {
            this.routerBuilder.htpasswd(
                    HtpasswdAuth.create(vertx, new HtpasswdAuthOptions().setHtpasswdFile(
                            this.microServiceConfiguration.basic().getPath())),
                    this.microServiceConfiguration.basic().getRealm());
        }
        return this;
    }

    VertxServer ssl() {
        LOGGER.info(String.format("HTTPS : %s", microServiceConfiguration.hasSsl()));
        if (this.microServiceConfiguration.hasSsl()) {
            this.httpServerOptions.setSsl(true)
                    .setKeyStoreOptions(new JksOptions()
                            .setPath(this.microServiceConfiguration.ssl().getPath())
                            .setPassword(this.microServiceConfiguration.ssl().getPassword()));
        }
        return this;
    }

    VertxServer jwt() {
        LOGGER.info(String.format("JWT : %s", microServiceConfiguration.hasJwt()));
        if (this.microServiceConfiguration.hasJwt()) {
            jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
                    .addPubSecKey(new PubSecKeyOptions()
                            .setAlgorithm(this.microServiceConfiguration.jwt().getAlgorithm())
                            .setPublicKey(this.microServiceConfiguration.jwt().getPassword())
                            .setSymmetric(this.microServiceConfiguration.jwt().getSymetric())));
        }
        return this;
    }

    public static VertxServer create(MicroServiceConfiguration microServiceConfiguration) {
        final Vertx vertx = Vertx.vertx(new VertxOptions());
        final Router router = Router.router(vertx);
        return new VertxServer(vertx, VertxRouterBuilder.router(router).protect(), microServiceConfiguration)
                .htpasswd()
                .ssl()
                .jwt();
    }

    public void start(VertxMicroService microService) {
        LOGGER.info(String.format("Starting service on port : %s", microServiceConfiguration.getPort()));
        final Router router = this.routerBuilder.build();
        microService.route(router, jwtAuth);
        this.vertx.createHttpServer(this.httpServerOptions)
                .requestHandler(router)
                .listen(microServiceConfiguration.getPort());
    }
}
