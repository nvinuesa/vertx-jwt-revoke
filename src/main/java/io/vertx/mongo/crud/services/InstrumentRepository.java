package io.vertx.mongo.crud.services;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

@ProxyGen
public interface InstrumentRepository {

  @Fluent
  InstrumentRepository save(Instrument instrument, Handler<AsyncResult<Instrument>> resultHandler);

  @Fluent
  InstrumentRepository findAll(Handler<AsyncResult<List<Instrument>>> resultHandler);

  @Fluent
  InstrumentRepository findById(String id, Handler<AsyncResult<Instrument>> resultHandler);

  @Fluent
  InstrumentRepository updateById(String id, Instrument body, Handler<AsyncResult<Instrument>> resultHandler);

  @Fluent
  InstrumentRepository remove(String id, Handler<AsyncResult<Void>> resultHandler);

  static InstrumentRepository createProxy(Vertx vertx, String address) {
    return new InstrumentRepositoryVertxEBProxy(vertx, address);
  }

  static InstrumentRepository create(MongoClient client) {
    return new InstrumentRepositoryImpl(client);
  }
}
