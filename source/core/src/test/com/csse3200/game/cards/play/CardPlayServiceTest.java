package com.csse3200.game.cards.play;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.cards.CardLibrary;
import com.csse3200.game.cards.CardType;
import com.csse3200.game.cards.EffectType;
import com.csse3200.game.cards.Rarity;
import com.csse3200.game.cards.TargetType;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.configs.EffectConfig;
import com.csse3200.game.cards.deck.BattleDeck;
import com.csse3200.game.cards.deck.PlayerDeck;
import com.csse3200.game.cards.effects.CardEffectResolution;
import com.csse3200.game.cards.effects.CardEffectResolutionService;
import com.csse3200.game.cards.effects.ResolvedCardEffect;
import com.csse3200.game.components.player.EnergyComponent;
import java.util.List;
import org.junit.jupiter.api.Test;

class CardPlayServiceTest {
  @Test
  void shouldSpendEnergyResolveEffectsAndDiscardPlayedCard() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);
    CardPlayService playService =
        new CardPlayService(cardLibrary, resolutionService, battleDeck, energyComponent);

    CardPlayResult result = playService.playCard("strike");

    assertTrue(result.successful());
    assertEquals(CardPlayFailureReason.NONE, result.failureReason());
    assertEquals(1, result.energyCost());
    assertEquals(2, energyComponent.getCurrentEnergy());
    assertTrue(battleDeck.getHand().isEmpty());
    assertIterableEquals(List.of("strike"), battleDeck.getDiscardPile());
    assertIterableEquals(
        List.of(
            new ResolvedCardEffect("strike", EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 6, 0, 0)),
        result.enemyEffects());
    assertTrue(result.playerEffects().isEmpty());
    assertIterableEquals(List.of(result.resolution()), resolutionService.getResolutions());
  }

  @Test
  void shouldNotResolveOrDiscardWhenEnergyIsInsufficient() {
    CardConfig innerFocus =
        card("inner_focus", 2, TargetType.SELF, new EffectConfig(EffectType.STRENGTH, 2));
    CardLibrary cardLibrary = new CardLibrary(List.of(innerFocus));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("inner_focus")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    energyComponent.spendEnergy(2);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);
    CardPlayService playService =
        new CardPlayService(cardLibrary, resolutionService, battleDeck, energyComponent);

    CardPlayResult result = playService.playCard("inner_focus");

    assertFalse(result.successful());
    assertEquals(CardPlayFailureReason.INSUFFICIENT_ENERGY, result.failureReason());
    assertEquals(1, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("inner_focus"), battleDeck.getHand());
    assertTrue(battleDeck.getDiscardPile().isEmpty());
    assertTrue(result.enemyEffects().isEmpty());
    assertTrue(result.playerEffects().isEmpty());
    assertTrue(resolutionService.getResolutions().isEmpty());
  }

  @Test
  void shouldNotSpendEnergyWhenCardIsNotInHand() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);
    CardPlayService playService =
        new CardPlayService(cardLibrary, resolutionService, battleDeck, energyComponent);

    CardPlayResult result = playService.playCard("strike");

    assertFalse(result.successful());
    assertEquals(CardPlayFailureReason.CARD_NOT_IN_HAND, result.failureReason());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("strike"), battleDeck.getDrawPile());
    assertTrue(battleDeck.getHand().isEmpty());
    assertTrue(battleDeck.getDiscardPile().isEmpty());
    assertTrue(resolutionService.getResolutions().isEmpty());
  }

  @Test
  void shouldPreviewCanPlayWithoutSpendingEnergy() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardPlayService playService = new CardPlayService(cardLibrary, battleDeck, energyComponent);

    assertTrue(playService.canPlay("strike"));
    assertEquals(3, energyComponent.getCurrentEnergy());
  }

  @Test
  void shouldRejectMissingDependencies() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);

    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayService(null, battleDeck, energyComponent));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayService(cardLibrary, null, battleDeck, energyComponent));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayService(cardLibrary, resolutionService, null, energyComponent));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayService(cardLibrary, resolutionService, battleDeck, null));
  }

  @Test
  void shouldRejectInvalidCardPlayResults() {
    CardEffectResolution resolution =
        new CardEffectResolution(
            "strike",
            List.of(
                new ResolvedCardEffect(
                    "strike", EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 6, 0, 0)));

    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayResult("strike", true, 1, null, CardPlayFailureReason.NONE));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CardPlayResult(
                "strike", true, 1, resolution, CardPlayFailureReason.INSUFFICIENT_ENERGY));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayResult("strike", false, 1, null, CardPlayFailureReason.NONE));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CardPlayResult(
                "strike", false, 1, resolution, CardPlayFailureReason.CARD_NOT_IN_HAND));
  }

  private static CardConfig card(String id, int cost, TargetType target, EffectConfig... effects) {
    CardConfig card = new CardConfig();
    card.id = id;
    card.name = id;
    card.description = "Test card";
    card.cost = cost;
    card.type = CardType.SKILL;
    card.rarity = Rarity.COMMON;
    card.target = target;
    card.effects = effects;
    card.texturePath = "images/cards/" + id + ".png";
    return card;
  }
}
