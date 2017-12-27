package com.github.underscorenico.vertx.jwt.revoke.services;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;

public class DatabaseVerticle extends AbstractVerticle {

  private MongoClient client;

  @Override
  public void start() throws Exception {

    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", "application.json"));
    ConfigRetriever retriever = ConfigRetriever
      .create(vertx, new ConfigRetrieverOptions()
        .addStore(fileStore));
    retriever.getConfig(conf -> {
      JsonObject datasourceConfig = conf.result().getJsonObject("datasource");
      final MongoClient client = MongoClient.createShared(vertx, datasourceConfig);
      final InstrumentRepository service = new InstrumentRepositoryImpl(client);
      new ServiceBinder(vertx)
        .setAddress("instrument-service")
        .register(InstrumentRepository.class, service);
    });
  }
}
