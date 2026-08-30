package com.csse3200.game.entities.configs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EnemyScalingTest {

  @Test
  void shouldNotScaleAtZeroProgression() {
    EnemyConfig base = new EnemyConfig();
    base.health = 100;
    base.baseAttack = 20;
    base.tier = EnemyTier.NORMAL;

    EnemyConfig scaled = EnemyScaling.scale(base, 0);

    assertEquals(100, scaled.health);
    assertEquals(20, scaled.baseAttack);
  }

  @Test
  void shouldScaleNormalEnemy() {
    EnemyConfig base = new EnemyConfig();
    base.health = 100;
    base.baseAttack = 20;
    base.tier = EnemyTier.NORMAL;

    EnemyConfig scaled = EnemyScaling.scale(base, 3);

    assertEquals(124, scaled.health);
    assertEquals(23, scaled.baseAttack);
  }

  @Test
  void shouldScaleEliteEnemy() {
    EnemyConfig base = new EnemyConfig();
    base.health = 100;
    base.baseAttack = 100;
    base.tier = EnemyTier.ELITE;

    EnemyConfig scaled = EnemyScaling.scale(base, 3);

    assertEquals(130, scaled.health);
    assertEquals(121, scaled.baseAttack);
  }

  @Test
  void shouldTreatNegativeProgressionAsZero() {
    EnemyConfig base = new EnemyConfig();
    base.health = 100;
    base.baseAttack = 20;
    base.tier = EnemyTier.NORMAL;

    EnemyConfig scaled = EnemyScaling.scale(base, -3);

    assertEquals(100, scaled.health);
    assertEquals(20, scaled.baseAttack);
  }
}
