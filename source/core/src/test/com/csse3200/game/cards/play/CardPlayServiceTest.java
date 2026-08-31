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
    assertTrue(result.updatedHand().isEmpty());
    assertTrue(result.updatedDrawPile().isEmpty());
    assertIterableEquals(List.of("strike"), result.updatedDiscardPile());
    assertIterableEquals(
        List.of(
            new ResolvedCardEffect("strike", EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 6, 0, 0)),
        result.enemyEffects());
    assertTrue(result.playerEffects().isEmpty());
    assertIterableEquals(List.of(result.resolution()), resolutionService.getResolutions());
  }

  @Test
  void shouldReturnOneCompleteResultForUnifiedCardPlayRequest() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike", "defend")));
    battleDeck.drawCards(2);
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardPlayService playService = new CardPlayService(cardLibrary, battleDeck, energyComponent);
    CardPlayRequest request = CardPlayRequest.singleEnemy("strike", "enemy-1");

    CardPlayResult result = playService.playCard(request);

    assertTrue(result.success());
    assertTrue(result.successful());
    assertEquals(request.target(), result.target());
    assertEquals(result.effectResolution(), result.resolution());
    assertEquals(1, result.energyCost());
    assertEquals(2, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("defend"), result.updatedHand());
    assertTrue(result.updatedDrawPile().isEmpty());
    assertIterableEquals(List.of("strike"), result.updatedDiscardPile());
    assertFalse(playService.canPlay(CardPlayRequest.singleEnemy("defend", "enemy-1")));
  }

  @Test
  void shouldRejectMismatchedTargetWithoutChangingEnergyOrDeck() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);
    CardPlayService playService =
        new CardPlayService(cardLibrary, resolutionService, battleDeck, energyComponent);

    CardPlayResult result = playService.playCard(CardPlayRequest.self("strike"));

    assertFalse(result.success());
    assertEquals(CardPlayFailureReason.INVALID_TARGET, result.failureReason());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("strike"), result.updatedHand());
    assertTrue(result.updatedDiscardPile().isEmpty());
    assertTrue(result.enemyEffects().isEmpty());
    assertTrue(resolutionService.getResolutions().isEmpty());
  }

  @Test
  void shouldReturnUnknownCardFailureFromUnifiedEntryPoint() {
    CardLibrary cardLibrary = new CardLibrary();
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("missing")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardPlayService playService = new CardPlayService(cardLibrary, battleDeck, energyComponent);

    CardPlayResult result = playService.playCard(CardPlayRequest.singleEnemy("missing", "enemy-1"));

    assertFalse(result.success());
    assertEquals(CardPlayFailureReason.UNKNOWN_CARD, result.failureReason());
    assertEquals(0, result.energyCost());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("missing"), result.updatedHand());
  }

  @Test
  void shouldReturnInvalidCardConfigFailureWithoutSpendingEnergy() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    strike.effects = new EffectConfig[0];
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardPlayService playService = new CardPlayService(cardLibrary, battleDeck, energyComponent);

    CardPlayResult result = playService.playCard(CardPlayRequest.singleEnemy("strike", "enemy-1"));

    assertFalse(result.success());
    assertEquals(CardPlayFailureReason.INVALID_CARD_CONFIG, result.failureReason());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("strike"), result.updatedHand());
    assertTrue(result.enemyEffects().isEmpty());
  }

  @Test
  void shouldReturnFailureAndLeaveNoEffectsWhenDeckCommitFails() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck failingDeck =
        new BattleDeck(new PlayerDeck(List.of("strike"))) {
          @Override
          public List<String> getHand() {
            return List.of("strike");
          }

          @Override
          public boolean playCard(String cardId) {
            return false;
          }
        };
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);
    CardPlayService playService =
        new CardPlayService(cardLibrary, resolutionService, failingDeck, energyComponent);

    CardPlayResult result = playService.playCard(CardPlayRequest.singleEnemy("strike", "enemy-1"));

    assertFalse(result.success());
    assertEquals(CardPlayFailureReason.RESOLUTION_FAILED, result.failureReason());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertTrue(resolutionService.getResolutions().isEmpty());
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
    assertEquals(CardPlayFailureReason.NOT_ENOUGH_ENERGY, result.failureReason());
    assertEquals(1, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("inner_focus"), battleDeck.getHand());
    assertTrue(battleDeck.getDiscardPile().isEmpty());
    assertTrue(result.enemyEffects().isEmpty());
    assertTrue(result.playerEffects().isEmpty());
    assertTrue(resolutionService.getResolutions().isEmpty());
  }

  @Test
  void shouldResolveDamageFromPlayerAndEnemyStateViews() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    PlayerStateView playerState =
        new PlayerStateView() {
          @Override
          public int currentEnergy() {
            return energyComponent.getCurrentEnergy();
          }

          @Override
          public int statusValue(EffectType type) {
            return switch (type) {
              case STRENGTH -> 2;
              case FEEBLE -> 1;
              default -> 0;
            };
          }
        };
    EnemyStateView enemyState =
        new EnemyStateView() {
          @Override
          public boolean isTargetAvailable(String targetId) {
            return "enemy-1".equals(targetId);
          }

          @Override
          public int statusValue(String targetId, EffectType type) {
            return type == EffectType.VULNERABLE ? 2 : 0;
          }
        };
    CardPlayService playService =
        new CardPlayService(cardLibrary, battleDeck, energyComponent, playerState, enemyState);

    CardPlayResult result = playService.playCard(CardPlayRequest.singleEnemy("strike", "enemy-1"));

    assertTrue(result.success());
    assertEquals(9, result.enemyEffects().get(0).value());
    assertEquals(2, energyComponent.getCurrentEnergy());
  }

  @Test
  void shouldRejectUnavailableEnemyBeforeSpendingEnergy() {
    CardConfig strike =
        card("strike", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.DAMAGE, 6));
    CardLibrary cardLibrary = new CardLibrary(List.of(strike));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    PlayerStateView playerState = stateView(energyComponent);
    EnemyStateView unavailableEnemy =
        new EnemyStateView() {
          @Override
          public boolean isTargetAvailable(String targetId) {
            return false;
          }

          @Override
          public int statusValue(String targetId, EffectType type) {
            return 0;
          }
        };
    CardPlayService playService =
        new CardPlayService(
            cardLibrary, battleDeck, energyComponent, playerState, unavailableEnemy);

    CardPlayResult result =
        playService.playCard(CardPlayRequest.singleEnemy("strike", "missing-enemy"));

    assertFalse(result.success());
    assertEquals(CardPlayFailureReason.INVALID_TARGET, result.failureReason());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("strike"), result.updatedHand());
  }

  @Test
  void shouldRollbackEnergyAndReturnFailureWhenResolutionFails() {
    CardConfig invalidCombination =
        card("enemy_heal", 1, TargetType.SINGLE_ENEMY, new EffectConfig(EffectType.HEAL, 4));
    CardLibrary cardLibrary = new CardLibrary(List.of(invalidCombination));
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("enemy_heal")));
    battleDeck.drawOne();
    EnergyComponent energyComponent = new EnergyComponent(3);
    CardEffectResolutionService resolutionService = new CardEffectResolutionService(cardLibrary);
    CardPlayService playService =
        new CardPlayService(cardLibrary, resolutionService, battleDeck, energyComponent);

    CardPlayResult result =
        playService.playCard(CardPlayRequest.singleEnemy("enemy_heal", "enemy-1"));

    assertFalse(result.success());
    assertEquals(CardPlayFailureReason.RESOLUTION_FAILED, result.failureReason());
    assertEquals(3, energyComponent.getCurrentEnergy());
    assertIterableEquals(List.of("enemy_heal"), result.updatedHand());
    assertTrue(result.updatedDiscardPile().isEmpty());
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
                "strike", true, 1, resolution, CardPlayFailureReason.NOT_ENOUGH_ENERGY));
    assertThrows(
        IllegalArgumentException.class,
        () -> new CardPlayResult("strike", false, 1, null, CardPlayFailureReason.NONE));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new CardPlayResult(
                "strike", false, 1, resolution, CardPlayFailureReason.CARD_NOT_IN_HAND));
  }

  @Test
  void shouldValidateCardPlayRequestsAndTargets() {
    assertThrows(IllegalArgumentException.class, () -> CardPlayRequest.self(" "));
    assertThrows(IllegalArgumentException.class, () -> CardPlayRequest.self(" strike "));
    assertThrows(IllegalArgumentException.class, () -> new CardPlayRequest("strike", null));
    assertThrows(IllegalArgumentException.class, () -> CardPlayTarget.singleEnemy(" "));
    assertThrows(IllegalArgumentException.class, () -> CardPlayTarget.singleEnemy(" enemy-1 "));
    assertThrows(
        IllegalArgumentException.class, () -> new CardPlayTarget(TargetType.SELF, "enemy-1"));
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

  private static PlayerStateView stateView(EnergyComponent energyComponent) {
    return new PlayerStateView() {
      @Override
      public int currentEnergy() {
        return energyComponent.getCurrentEnergy();
      }

      @Override
      public int statusValue(EffectType type) {
        return 0;
      }
    };
  }
}
