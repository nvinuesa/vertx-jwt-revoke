package io.vertx.mongo.crud.models;

public class Instrument {

  private int id;
  private String name;
  private int octaves;

  public Instrument(int id, String name, int octaves) {
    this.id = id;
    this.name = name;
    this.octaves = octaves;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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
}
