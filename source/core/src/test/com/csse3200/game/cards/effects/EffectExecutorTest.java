package com.csse3200.game.cards.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.cards.config.EffectConfig;
import com.csse3200.game.cards.config.EffectType;
import com.csse3200.game.cards.config.TargetType;
import org.junit.jupiter.api.Test;

class EffectExecutorTest {
  private final EffectExecutor executor = new EffectExecutor();

  @Test
  void shouldExecuteAllSupportedEffects() {
    RecordingCharacterEffectGateway target = new RecordingCharacterEffectGateway();

    executor.execute(new EffectConfig(EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 6), target);
    executor.execute(new EffectConfig(EffectType.BLOCK, TargetType.SELF, 5), target);
    executor.execute(new EffectConfig(EffectType.HEAL, TargetType.SELF, 4), target);
    executor.execute(new EffectConfig(EffectType.POISON, TargetType.SINGLE_ENEMY, 3), target);
    executor.execute(new EffectConfig(EffectType.VULNERABLE, TargetType.SINGLE_ENEMY, 2), target);
    executor.execute(new EffectConfig(EffectType.STRENGTH, TargetType.SELF, 1), target);

    assertEquals(6, target.damage);
    assertEquals(5, target.block);
    assertEquals(4, target.healing);
    assertEquals(3, target.poison);
    assertEquals(2, target.vulnerable);
    assertEquals(1, target.strength);
  }
}
