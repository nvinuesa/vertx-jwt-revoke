package io.vertx.mongo.crud.services;

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
//      JsonObject o = new JsonObject();
//      o.put("host", datasourceConfig.getString("host"));
//      o.put("port", datasourceConfig.getInteger("port"));
//      o.put("db_name", datasourceConfig.getString("db_name"));
      final MongoClient client = MongoClient.createShared(vertx, datasourceConfig);
      final InstrumentRepository service = new InstrumentRepositoryImpl(client);
      new ServiceBinder(vertx)
        .setAddress("instrument-service")
        .register(InstrumentRepository.class, service);
    });
  }
}
