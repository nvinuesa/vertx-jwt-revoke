package com.github.underscorenico.vertx.jwt.revoke.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

@DataObject
public class Instrument {

  public static final String DB_TABLE = "instruments";

  @JsonProperty("_id")
  private String id;
  private String instrumentId;
  private String name;
  private int octaves;

  public Instrument() {
  }

  public Instrument(String id, String instrumentId, String name, int octaves) {
    this.id = id;
    this.instrumentId = instrumentId;
    this.name = name;
    this.octaves = octaves;
  }

  public Instrument(JsonObject json) {
    this.id = json.getString("_id");
    this.instrumentId = json.getString("instrumentId");
    this.name = json.getString("name");
    this.octaves = json.getInteger("octaves");
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getInstrumentId() {
    return instrumentId;
  }

  public void setInstrumentId(String instrumentId) {
    this.instrumentId = instrumentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getOctaves() {
    return octaves;
  }

  public void setOctaves(int octaves) {
    this.octaves = octaves;
  }

  public JsonObject toJson() {
    return JsonObject.mapFrom(this);
  }

  @Override
  public String toString() {
    return Json.encodePrettily(this);
  }
}
