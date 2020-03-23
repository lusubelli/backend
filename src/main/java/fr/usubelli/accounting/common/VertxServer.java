package fr.usubelli.accounting.common;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.htpasswd.HtpasswdAuth;
import io.vertx.ext.auth.htpasswd.HtpasswdAuthOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;

public class VertxServer {

    private final Vertx vertx;
    private final VertxRouterBuilder routerBuilder;
    private final HttpServerOptions httpServerOptions;

    VertxServer(Vertx vertx, VertxRouterBuilder routerBuilder) {
        this.vertx = vertx;
        this.routerBuilder = routerBuilder;
        this.httpServerOptions = new HttpServerOptions();
    }

    public VertxServer htpasswd(String realm, String path) {
        HtpasswdAuth authProvider = HtpasswdAuth.create(vertx, new HtpasswdAuthOptions().setHtpasswdFile(path));
        this.routerBuilder.htpasswd(authProvider, realm);
        return this;
    }

    public VertxServer ssl(String keystore, String password) {
        this.httpServerOptions.setSsl(true)
                .setKeyStoreOptions(new JksOptions()
                        .setPath(keystore)
                        .setPassword(password));
        return this;
    }

    public static VertxServer create() {
        final Vertx vertx = Vertx.vertx(new VertxOptions());
        final Router router = Router.router(vertx);
        return new VertxServer(vertx, VertxRouterBuilder
                .router(router)
                .protect());
    }

    public void start(VertxMicroService microService, int port) {
        final Router router = this.routerBuilder.build();
        microService.route(router);
        this.vertx.createHttpServer(this.httpServerOptions)
                .requestHandler(router)
                .listen(port);
    }

    public JWTAuth createJwtAuth() {
        return JWTAuth.create(vertx, new JWTAuthOptions()
                .addPubSecKey(new PubSecKeyOptions()
                        .setAlgorithm("HS256")
                        .setPublicKey("password")
                        .setSymmetric(true)));
    }
}
