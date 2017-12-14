package io.vertx.mongo.crud;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.mongo.crud.services.DatabaseVerticle;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Future<Void> startFuture) {

    Future<String> dbVerticleDeployment = Future.future();
    vertx.deployVerticle(
      new DatabaseVerticle(),
      dbVerticleDeployment.completer());

    dbVerticleDeployment.compose(id -> {

      Future<String> httpVerticleDeployment = Future.future();
      vertx.deployVerticle(
        // If we need to create more than one instance, then we need to pass the verticle class name instead of the instance (with new):
        "io.vertx.mongo.crud.controller.ControllerVerticle",
        new DeploymentOptions().setInstances(2),
        httpVerticleDeployment.completer());

      return httpVerticleDeployment;

    }).setHandler(ar -> {
      if (ar.succeeded()) {
        startFuture.complete();
      } else {
        startFuture.fail(ar.cause());
      }
    });
  }

}
