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
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mongo.crud.services.Instrument;
import io.vertx.mongo.crud.services.InstrumentRepository;

public class ControllerVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerVerticle.class);

  private static final String CONFIG_HTTP_SERVER_PORT = "http.server.port";
  private WebClient webClient;
  private InstrumentRepository repository;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    repository = InstrumentRepository.createProxy(vertx, "instrument-service");

    webClient = WebClient.create(vertx, new WebClientOptions()
      .setSsl(true)
      .setUserAgent("vert-x3"));

    HttpServer server = vertx.createHttpServer();

    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
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
      }
    );
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
