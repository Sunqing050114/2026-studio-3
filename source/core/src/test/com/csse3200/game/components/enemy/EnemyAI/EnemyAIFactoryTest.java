package com.csse3200.game.components.enemy.EnemyAI;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class EnemyAIFactoryTest {
  @Test
  void shouldCreateCycleAttackDefendAI() {
    EnemyAI ai = EnemyAIFactory.create(EnemyAIFactory.LESSER_SHADE);

    assertInstanceOf(CycleAttackDefendAI.class, ai);
  }

  @Test
  void shouldCreateSeparateInstances() {
    EnemyAI first = EnemyAIFactory.create(EnemyAIFactory.LESSER_SHADE);

    EnemyAI second = EnemyAIFactory.create(EnemyAIFactory.LESSER_SHADE);

    assertNotSame(first, second);
  }

  @Test
  void shouldRejectUnknownBehaviour() {
    assertThrows(IllegalArgumentException.class, () -> EnemyAIFactory.create("unknown_ai"));
  }

  @Test
  void shouldRejectNullBehaviour() {
    assertThrows(IllegalArgumentException.class, () -> EnemyAIFactory.create(null));
  }
}
