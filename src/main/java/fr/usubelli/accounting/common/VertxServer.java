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
import io.vertx.ext.web.handler.BasicAuthHandler;

public class VertxServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertxServer.class);

    private final Vertx vertx;
    private final MicroServiceConfiguration microServiceConfiguration;
    private final HttpServerOptions httpServerOptions;
    private final Router router;
    private JWTAuth jwtAuth;

    VertxServer(Vertx vertx, MicroServiceConfiguration microServiceConfiguration) {
        this.vertx = vertx;
        this.microServiceConfiguration = microServiceConfiguration;
        this.httpServerOptions = new HttpServerOptions();
        this.router = Router.router(vertx);
        this.router.route().handler(ctx -> {
            ctx.response()
                    // do not allow proxies to cache the data
                    .putHeader("Cache-Control", "no-store, no-cache")
                    // prevents Internet Explorer from MIME - sniffing a
                    // response away from the declared content-type
                    .putHeader("X-Content-Type-Options", "nosniff")
                    // Strict HTTPS (for about ~6Months)
                    .putHeader("Strict-Transport-Security", "max-age=" + 15768000)
                    // IE8+ do not allow opening of attachments in the context of this resource
                    .putHeader("X-Download-Options", "noopen")
                    // enable XSS for IE
                    .putHeader("X-XSS-Protection", "1; mode=block")
                    // deny frames
                    .putHeader("X-FRAME-OPTIONS", "DENY");
            ctx.next();
        });
    }

    VertxServer htpasswd() {
        LOGGER.info(String.format("HTPASSWD : %s", microServiceConfiguration.hasBasicAuth()));
        if (this.microServiceConfiguration.hasBasicAuth()) {
            router.route().handler(BasicAuthHandler.create(
                    HtpasswdAuth.create(vertx, new HtpasswdAuthOptions().setHtpasswdFile(
                            this.microServiceConfiguration.basic().getPath())),
                    this.microServiceConfiguration.basic().getRealm()));
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
        return new VertxServer(vertx, microServiceConfiguration)
                .htpasswd()
                .ssl()
                .jwt();
    }

    public void start(VertxMicroService microService) {
        LOGGER.info(String.format("PORT : %s", microServiceConfiguration.getPort()));
        microService.route(router, jwtAuth);
        this.vertx.createHttpServer(this.httpServerOptions)
                .requestHandler(router)
                .listen(microServiceConfiguration.getPort());
    }
}
