package com.csse3200.game.cards.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.csse3200.game.cards.config.CardConfig;
import com.csse3200.game.cards.config.EffectConfig;
import com.csse3200.game.cards.config.EffectType;
import com.csse3200.game.cards.config.TargetType;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardEffectResolverTest {
  private final CardEffectResolver resolver = new CardEffectResolver();

  @Test
  void shouldResolveSelfEffects() {
    RecordingCharacterEffectGateway self = new RecordingCharacterEffectGateway();
    RecordingCharacterEffectGateway enemy = new RecordingCharacterEffectGateway();
    CardConfig card = card(new EffectConfig(EffectType.BLOCK, TargetType.SELF, 5));

    resolver.resolve(card, self, enemy, List.of(enemy));

    assertEquals(5, self.block);
    assertEquals(0, enemy.block);
  }

  @Test
  void shouldResolveSingleEnemyEffects() {
    RecordingCharacterEffectGateway self = new RecordingCharacterEffectGateway();
    RecordingCharacterEffectGateway selectedEnemy = new RecordingCharacterEffectGateway();
    RecordingCharacterEffectGateway otherEnemy = new RecordingCharacterEffectGateway();
    CardConfig card = card(new EffectConfig(EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 6));

    resolver.resolve(card, self, selectedEnemy, List.of(selectedEnemy, otherEnemy));

    assertEquals(6, selectedEnemy.damage);
    assertEquals(0, otherEnemy.damage);
    assertEquals(0, self.damage);
  }

  @Test
  void shouldResolveAllEnemyEffects() {
    RecordingCharacterEffectGateway self = new RecordingCharacterEffectGateway();
    RecordingCharacterEffectGateway firstEnemy = new RecordingCharacterEffectGateway();
    RecordingCharacterEffectGateway secondEnemy = new RecordingCharacterEffectGateway();
    CardConfig card = card(new EffectConfig(EffectType.POISON, TargetType.ALL_ENEMIES, 3));

    resolver.resolve(card, self, firstEnemy, List.of(firstEnemy, secondEnemy));

    assertEquals(3, firstEnemy.poison);
    assertEquals(3, secondEnemy.poison);
    assertEquals(0, self.poison);
  }

  @Test
  void shouldResolveMultipleEffectsInOrder() {
    RecordingCharacterEffectGateway self = new RecordingCharacterEffectGateway();
    RecordingCharacterEffectGateway enemy = new RecordingCharacterEffectGateway();
    CardConfig card =
        card(
            new EffectConfig(EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 8),
            new EffectConfig(EffectType.VULNERABLE, TargetType.SINGLE_ENEMY, 2),
            new EffectConfig(EffectType.STRENGTH, TargetType.SELF, 1));

    resolver.resolve(card, self, enemy, List.of(enemy));

    assertEquals(8, enemy.damage);
    assertEquals(2, enemy.vulnerable);
    assertEquals(1, self.strength);
  }

  @Test
  void shouldRequireSelectedEnemyForSingleEnemyEffects() {
    RecordingCharacterEffectGateway self = new RecordingCharacterEffectGateway();
    CardConfig card = card(new EffectConfig(EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 6));

    assertThrows(
        IllegalArgumentException.class, () -> resolver.resolve(card, self, null, List.of()));
  }

  private CardConfig card(EffectConfig... effects) {
    return new CardConfig("test", "Test", "Test card", 1, effects);
  }
}
