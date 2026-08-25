package com.csse3200.game.cards.config;

/** Serializable configuration for one playable card. */
public class CardConfig {
  public String id = "";
  public String name = "";
  public String description = "";
  public int cost = 0;
  public EffectConfig[] effects = new EffectConfig[0];

  public CardConfig() {}

  public CardConfig(String id, String name, String description, int cost, EffectConfig[] effects) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.cost = cost;
    this.effects = effects == null ? new EffectConfig[0] : effects;
  }
}
