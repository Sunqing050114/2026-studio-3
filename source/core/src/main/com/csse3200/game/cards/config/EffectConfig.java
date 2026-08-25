package com.csse3200.game.cards.config;

/** Serializable configuration for a single effect on a card. */
public class EffectConfig {
  public EffectType type;
  public TargetType target;
  public int amount = 0;

  public EffectConfig() {}

  public EffectConfig(EffectType type, TargetType target, int amount) {
    this.type = type;
    this.target = target;
    this.amount = amount;
  }
}
