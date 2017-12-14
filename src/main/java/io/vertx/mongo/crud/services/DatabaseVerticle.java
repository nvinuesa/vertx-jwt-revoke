package io.vertx.mongo.crud.services;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.mongo.MongoClient;

public class DatabaseVerticle extends AbstractVerticle {

  private MongoClient client;

  @Override
  public void start() throws Exception {

    client = MongoClient.createShared(vertx, config());
  }
}
