package io.vertx.mongo.crud.controller;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

public class ControllerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerVerticle.class);

  private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  private WebClient webClient;

  @Override
  public void start(Future<Void> startFuture) throws Exception {

    webClient = WebClient.create(vertx, new WebClientOptions()
      .setSsl(true)
      .setUserAgent("vert-x3"));

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.post("/api/instruments").handler(this::createInstrument);
    router.get("/api/instruments").handler(this::getAllInstruments);
    router.get("/api/instruments/:id").handler(this::getInstrument);
    router.put("/api/instruments/:id").handler(this::updateInstrument);
    router.delete("/api/instruments/:id").handler(this::deleteInstrument);

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

  private void createInstrument(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(""));
  }

  private void getAllInstruments(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(""));
  }

  private void getInstrument(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(""));
  }

  private void updateInstrument(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(""));
  }

  private void deleteInstrument(RoutingContext routingContext) {
    routingContext.response()
      .putHeader("content-type", "application/json; charset=utf-8")
      .end(Json.encodePrettily(""));
  }

}
