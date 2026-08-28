package com.csse3200.game.entities.configs;

/** Applies progression-based stat scaling to enemy configurations. */
public final class EnemyScaling {

  public static EnemyConfig scale(EnemyConfig base, int progression) {
    int safeProgression = Math.max(0, progression);

    double healthRate;
    double attackRate;

    if (base.tier == EnemyTier.ELITE) {
      healthRate = 0.10;
      attackRate = 0.07;
    } else {
      healthRate = 0.08;
      attackRate = 0.05;
    }

    EnemyConfig scaled = new EnemyConfig();

    scaled.id = base.id;
    scaled.name = base.name;
    scaled.health = (int) Math.round(base.health * (1.0 + healthRate * safeProgression));
    scaled.baseAttack = (int) Math.round(base.baseAttack * (1.0 + attackRate * safeProgression));
    scaled.armour = base.armour;
    scaled.tier = base.tier;
    scaled.behaviour = base.behaviour;

    return scaled;
  }

  private EnemyScaling() {
    throw new IllegalStateException("Instantiating utility class");
  }
}
