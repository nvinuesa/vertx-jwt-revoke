package com.github.underscorenico.vertx.jwt.revoke.services;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;
import com.github.underscorenico.vertx.jwt.revoke.controller.ControllerVerticle;

import java.util.List;
import java.util.stream.Collectors;

public class InstrumentRepositoryImpl implements InstrumentRepository {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerVerticle.class);

  private MongoClient client;

  public InstrumentRepositoryImpl(final MongoClient client) {
    this.client = client;
  }

  @Override
  public InstrumentRepository save(Instrument instrument, Handler<AsyncResult<Instrument>> resultHandler) {
    JsonObject json = JsonObject.mapFrom(instrument);
    // Remove the id before saving
    json.remove("_id");
    client.save(Instrument.DB_TABLE, json, res -> {
      if (res.succeeded()) {
        LOGGER.info("Instrument created: " + res.result());
        instrument.setId(res.result());
        resultHandler.handle(Future.succeededFuture(instrument));
      } else {
        LOGGER.error("Instrument not created", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public InstrumentRepository findAll(Handler<AsyncResult<List<Instrument>>> resultHandler) {
    JsonObject query = new JsonObject();
    client.find(Instrument.DB_TABLE, query, res -> {
      if (res.succeeded()) {
        LOGGER.info("Retrieving all (" + res.result().size() + ") instruments");
        resultHandler.handle(Future.succeededFuture(
          res.result().stream()
            .map(Instrument::new)
            .collect(Collectors.toList())
        ));
      } else {
        LOGGER.error("Error retrieving all instruments", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public InstrumentRepository findById(String id, Handler<AsyncResult<Instrument>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", id);
    client.findOne(Instrument.DB_TABLE, query, new JsonObject(), res -> {
      if (res.succeeded()) {
        if (res.result() == null) {
          LOGGER.error("Instrument with id " + id + " not found!");
          resultHandler.handle(Future.failedFuture(new Throwable("Instrument not found!")));
        } else {
          LOGGER.info("Retrieving instrument with id" + id);
          resultHandler.handle(Future.succeededFuture(
            new Instrument(res.result())
          ));
        }
      } else {
        LOGGER.error("Error retrieving instrument", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public InstrumentRepository updateById(String id, Instrument body, Handler<AsyncResult<Instrument>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", id);
    JsonObject json = body.toJson();
    // Remove the id before update
    json.remove("_id");
    JsonObject update = new JsonObject().put("$set", json);

    client.findOneAndUpdate(Instrument.DB_TABLE, query, update, res -> {
      if (res.succeeded()) {
        if (res.result() == null) {
          LOGGER.error("Instrument with id " + id + " not found!");
          resultHandler.handle(Future.failedFuture(new Throwable("Instrument not found!")));
        } else {
          LOGGER.info("Updating instrument with id" + id);
          res.result().remove("_id");
          Instrument updated = new Instrument(res.result());
          updated.setId(id);
          resultHandler.handle(Future.succeededFuture(updated));
        }
      } else {
        LOGGER.error("Error updating instrument", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }

  @Override
  public InstrumentRepository remove(String id, Handler<AsyncResult<Void>> resultHandler) {
    JsonObject query = new JsonObject().put("_id", id);
    client.removeDocument(Instrument.DB_TABLE, query, res -> {
      if (res.succeeded()) {
        LOGGER.info("Deleted instrument with id" + id);
        resultHandler.handle(Future.succeededFuture());
      } else {
        LOGGER.error("Error deleting instrument", res.cause());
        resultHandler.handle(Future.failedFuture(res.cause()));
      }
    });
    return this;
  }
}
