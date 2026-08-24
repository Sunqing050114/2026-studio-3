package com.csse3200.game.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.configs.EffectConfig;
import org.junit.jupiter.api.Test;

class CardConfigTest {

  /** Builds a card that passes validation, so tests can invalidate one field at a time. */
  private CardConfig validCard() {
    CardConfig card = new CardConfig();
    card.id = "strike";
    card.name = "Strike";
    card.description = "Deal 6 damage.";
    card.cost = 1;
    card.type = CardType.ATTACK;
    card.rarity = Rarity.COMMON;
    card.target = TargetType.SINGLE_ENEMY;
    card.effects = new EffectConfig[] {new EffectConfig(EffectType.DAMAGE, 6)};
    card.texturePath = "images/cards/strike.png";
    return card;
  }

  @Test
  void shouldAcceptAValidCard() {
    assertTrue(validCard().isValid());
  }

  @Test
  void shouldAcceptZeroCost() {
    CardConfig card = validCard();
    card.cost = 0;
    assertTrue(card.isValid());
  }

  @Test
  void shouldAcceptMultipleEffectsOnOneCard() {
    CardConfig card = validCard();
    card.effects =
        new EffectConfig[] {
          new EffectConfig(EffectType.DAMAGE, 4), new EffectConfig(EffectType.POISON, 3, 3)
        };
    assertTrue(card.isValid());
  }

  @Test
  void shouldAcceptStrengthWithoutDuration() {
    CardConfig card = validCard();
    card.type = CardType.POWER;
    card.target = TargetType.SELF;
    card.effects = new EffectConfig[] {new EffectConfig(EffectType.STRENGTH, 2)};
    assertTrue(card.isValid());
  }

  @Test
  void shouldRejectDefaultPlaceholderId() {
    CardConfig card = validCard();
    card.id = "unknown";
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectBlankId() {
    CardConfig card = validCard();
    card.id = "  ";
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectBlankName() {
    CardConfig card = validCard();
    card.name = "";
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectNegativeCost() {
    CardConfig card = validCard();
    card.cost = -1;
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectBlankTexturePath() {
    CardConfig card = validCard();
    card.texturePath = "";
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectCardWithNoEffects() {
    CardConfig card = validCard();
    card.effects = new EffectConfig[0];
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectNullEffectEntry() {
    CardConfig card = validCard();
    card.effects = new EffectConfig[] {null};
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectNonPositiveEffectValue() {
    CardConfig card = validCard();
    card.effects = new EffectConfig[] {new EffectConfig(EffectType.DAMAGE, 0)};
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectOngoingEffectWithoutDuration() {
    CardConfig card = validCard();
    card.effects = new EffectConfig[] {new EffectConfig(EffectType.POISON, 3, 0)};
    assertFalse(card.isValid());
  }

  @Test
  void shouldRejectInstantEffectWithDuration() {
    CardConfig card = validCard();
    card.effects = new EffectConfig[] {new EffectConfig(EffectType.DAMAGE, 6, 2)};
    assertFalse(card.isValid());
  }

  @Test
  void shouldReportEveryProblemAtOnce() {
    CardConfig card = validCard();
    card.id = "";
    card.name = "";
    card.cost = -5;
    assertEquals(3, card.validate().size());
  }
}
