package io.vertx.mongo.crud.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.auth.jwt.JWTOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.mongo.crud.services.Instrument;
import io.vertx.mongo.crud.services.InstrumentRepository;

public class ControllerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerVerticle.class);

  private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  private WebClient webClient;
  private InstrumentRepository repository;
  private JWTAuth provider;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    repository = InstrumentRepository.createProxy(vertx, "instrument-service");

    JWTAuthOptions jwtConfig = new JWTAuthOptions()
      .setKeyStore(new KeyStoreOptions()
        .setPath("keystore.jceks")
        .setPassword("secret"));

    provider = JWTAuth.create(vertx, jwtConfig);

    webClient = WebClient.create(vertx, new WebClientOptions()
      .setSsl(true)
      .setUserAgent("vert-x3"));

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    // Session routes
    router.post("/login").handler(this::login);
    router.post("/logout").handler(this::logout);
    // API routes
    // Protect the API routes with handler
    router.route("/api/*").handler(JWTAuthHandler.create(provider, "/login"));
    router.route("/api/*").handler(this::isRevoked);


    router.post("/api/instruments").handler(this::createInstrument);
    router.get("/api/instruments").handler(this::getAllInstruments);
    router.get("/api/instruments/:id").handler(this::getInstrument);
    router.put("/api/instruments/:id").handler(this::updateInstrument);
    router.delete("/api/instruments/:id").handler(this::removeInstrument);


    int portNumber = config().getInteger(CONFIG_HTTP_SERVER_PORT, 8080);
    server
      .requestHandler(router::accept)
      .listen(portNumber, ar -> {
        if (ar.succeeded()) {
          LOGGER.info("HTTP server running on port " + portNumber);
          startFuture.complete();
        } else {
          LOGGER.error("Could not start a HTTP server", ar.cause());
          startFuture.fail(ar.cause());
        }
      });
  }

  /**
   * This method checks whether the token has been revoked (logged-out) or not.
   *
   * @param routingContext
   */
  private void isRevoked(RoutingContext routingContext) {
    // Check if token is in Redis
    routingContext.next();
  }

  // Session methods

  /**
   * This method authenticates a user (hard-coded check to avoid database boilerplate) and returns a JWT.
   *
   * @param routingContext
   */
  private void login(RoutingContext routingContext) {
    // Get username from body
    String username = routingContext.getBodyAsJson().getString("username");
    // Get password from body
    String password = routingContext.getBodyAsJson().getString("password");
    // Hard-coded user / pass checking
    if (!username.equals("userTest") || !password.equals("passTest")) {
      routingContext.response()
        .setStatusCode(404)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily("Bad username or password!"));
    } else {
      String token = provider.generateToken(new JsonObject().put("username", "userTest"), new JWTOptions());
      routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(new JsonObject().put("token", token)));
    }
  }

  private void logout(RoutingContext routingContext) {

  }

  /**
   * API methods
   */

  private void createInstrument(RoutingContext routingContext) {
    Instrument instrument = Json.decodeValue(routingContext.getBodyAsString(), Instrument.class);
    repository.save(instrument, res -> {

      Instrument saved = res.result();
      routingContext.response()
        .putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(saved));
    });
  }

  private void getAllInstruments(RoutingContext routingContext) {
    repository.findAll(res -> routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(res.result()))
    );
  }

  private void getInstrument(RoutingContext routingContext) {
    String id = routingContext.pathParam("id");
    repository.findById(id, res -> {
      if (!res.succeeded()) {
        routingContext.response()
          .setStatusCode(404)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(res.cause().getMessage()));
      } else {
        routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(res.result()));
      }
    });
  }

  private void updateInstrument(RoutingContext routingContext) {
    String id = routingContext.pathParam("id");
    Instrument instrument = Json.decodeValue(routingContext.getBodyAsString(), Instrument.class);
    repository.updateById(id, instrument, res -> {
      if (!res.succeeded()) {
        routingContext.response()
          .setStatusCode(404)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(res.cause().getMessage()));
      } else {
        routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(res.result()));
      }
    });
  }

  private void removeInstrument(RoutingContext routingContext) {
    String id = routingContext.pathParam("id");
    repository.remove(id, res -> {
      if (!res.succeeded()) {
        routingContext.response()
          .setStatusCode(404)
          .putHeader("content-type", "application/json; charset=utf-8")
          .end(Json.encodePrettily(res.cause().getMessage()));
      } else {
        routingContext.response()
          .putHeader("content-type", "application/json; charset=utf-8")
          .end();
      }
    });
  }
}
