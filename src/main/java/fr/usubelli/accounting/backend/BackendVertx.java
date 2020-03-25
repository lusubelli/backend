package fr.usubelli.accounting.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.usubelli.accounting.backend.dto.SigninRequest;
import fr.usubelli.accounting.backend.dto.User;
import fr.usubelli.accounting.backend.port.OrganizationGateway;
import fr.usubelli.accounting.backend.port.UserGateway;
import fr.usubelli.accounting.backend.usecase.Signin;
import fr.usubelli.accounting.common.VertxMicroService;
import io.vertx.core.Handler;
import io.vertx.core.http.Cookie;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.UUID;

public class BackendVertx implements VertxMicroService {

    private static final String APPLICATION_JSON = "application/json";

    private final ObjectMapper objectMapper;
    private final UserGateway userGateway;
    private final OrganizationGateway organisationGateway;

    public BackendVertx(ObjectMapper objectMapper, UserGateway userGateway,
            OrganizationGateway organisationGateway) {
        this.objectMapper = objectMapper;
        this.userGateway = userGateway;
        this.organisationGateway = organisationGateway;
    }

    public void route(Router router, JWTAuth authProvider) {
        router.route().handler(CorsHandler.create(".*.")
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowCredentials(true)
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Content-Type"));
        router.route().handler(BodyHandler.create());
        router.route("/backend/api/v1/secured/*")
                .produces(APPLICATION_JSON)
                .handler(authenticationFilter(authProvider));
        router.post("/backend/api/v1/signin")
                .produces(APPLICATION_JSON)
                .handler(signin(authProvider));
        router.get("/backend/api/v1/secured/signout")
                .produces(APPLICATION_JSON)
                .handler(signout());
    }

    private Handler<RoutingContext> signin(JWTAuth authProvider) {
        return rc -> {
            try {
                final SigninRequest signinRequest = objectMapper.readValue(rc.getBodyAsString(), SigninRequest.class);

                final User user = new Signin(userGateway).signin(signinRequest);
                if (user != null) {
                    final String xsrfToken = UUID.randomUUID().toString();
                    String token = createJsonWebToken(authProvider, user, xsrfToken);
                    // now for any request to protected resources you should pass this string in the HTTP header Authorization as:
                    // Authorization: Bearer <token>
                    rc.addCookie(createCookie(token));
                    rc.response().setStatusCode(200).end("{ \"access_token\": \"" + xsrfToken + "\" }");
                } else {
                    rc.response().setStatusCode(403).end();
                }
            } catch (Exception e) {
                e.printStackTrace();
                rc.response().setStatusCode(500).end();
            }
        };
    }

    private Handler<RoutingContext> authenticationFilter(JWTAuth authProvider) {
        return rc -> {
            final String jwt = rc.getCookie("SESSIONID").getValue();
            authProvider.authenticate(new JsonObject().put("jwt", jwt), res -> {
                if (res.succeeded()) {
                    final String headerXsrfToken = rc.request().getHeader("x-xsrf-token");
                    final String jwtXsrfToken = res.result().principal().getString("xsrfToken");
                    if (!headerXsrfToken.equals(jwtXsrfToken)) {
                        rc.response().setStatusCode(403).end();
                    } else {
                        final String email = res.result().principal().getString("sub");
                        rc.put("email", email);
                        rc.next();
                    }
                } else {
                    rc.response().setStatusCode(403).end();
                }
            });
        };
    }

    private Handler<RoutingContext> signout() {
        return rc -> {
            try {
                rc.addCookie(removeCookie());
                rc.response().setStatusCode(200).end();
            } catch (Exception e) {
                e.printStackTrace();
                rc.response().setStatusCode(500).end();
            }
        };
    }


    private Cookie removeCookie() {
        Cookie cookie = Cookie.cookie("SESSIONID", "");
        String path = "/";
        cookie.setPath(path);
        //cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        return cookie;
    }

    private String createJsonWebToken(JWTAuth authProvider, User user, String xsrfToken) {
        final JsonObject claims = new JsonObject()
                .put("sub", user.getEmail())
                .put("xsrfToken", xsrfToken)
                .put("scopes", new JsonArray().add("admin"));
        return authProvider.generateToken(claims, new JWTOptions());
    }

    private Cookie createCookie(String token) {
        Cookie cookie = Cookie.cookie("SESSIONID", token);
        String path = "/";
        cookie.setPath(path);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(10000000);
        return cookie;
    }

}
