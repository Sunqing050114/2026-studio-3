package com.csse3200.game.cards.effects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.cards.CardLibrary;
import com.csse3200.game.cards.CardPlayRequest;
import com.csse3200.game.cards.CardType;
import com.csse3200.game.cards.EffectType;
import com.csse3200.game.cards.TargetType;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.configs.EffectConfig;
import com.csse3200.game.cards.deck.BattleDeck;
import com.csse3200.game.cards.deck.PlayerDeck;
import com.csse3200.game.components.player.EnergyComponent;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardResolutionServiceTest {
  private CardResolutionService service;
  private BattleDeck deck;

  @BeforeEach
  void setUp() {
    CardLibrary library =
        new CardLibrary(
            List.of(
                card(
                    "strike",
                    CardType.ATTACK,
                    TargetType.SINGLE_ENEMY,
                    1,
                    new EffectConfig(EffectType.DAMAGE, 6)),
                card(
                    "bandage",
                    CardType.SKILL,
                    TargetType.SELF,
                    1,
                    new EffectConfig(EffectType.HEAL, 6))));
    service = new CardResolutionService(library);

    // Hand of 2 (strike, bandage) with spare cards in the draw pile so playing a card can draw a
    // replacement.
    deck = new BattleDeck(new PlayerDeck(List.of("strike", "bandage", "bandage", "bandage")));
    deck.drawCards(2);
  }

  private static CardConfig card(
      String id, CardType type, TargetType target, int cost, EffectConfig effect) {
    CardConfig config = new CardConfig();
    config.id = id;
    config.name = id;
    config.cost = cost;
    config.type = type;
    config.target = target;
    config.effects = new EffectConfig[] {effect};
    config.texturePath = "images/cards/" + id + ".png";
    return config;
  }

  @Test
  void playsAttackCardAndMovesItToDiscard() {
    EnergyComponent energy = new EnergyComponent(3);

    CardPlayResult result = service.play(new CardPlayRequest("strike", "enemy"), deck, energy);

    assertTrue(result.success());
    assertEquals(1, result.enemyEffects().size());
    assertEquals(EffectType.DAMAGE, result.enemyEffects().get(0).type());
    assertEquals(6, result.enemyEffects().get(0).value());
    assertTrue(result.playerEffects().isEmpty());
    assertEquals(1, result.energyCost());
    assertFalse(result.updatedHand().contains("strike"));
    assertTrue(result.updatedDiscardPile().contains("strike"));
    assertEquals(2, result.updatedHand().size()); // played card replaced by a draw
    assertEquals(2, energy.getCurrentEnergy());
  }

  @Test
  void failsWhenNotEnoughEnergyAndKeepsCardInHand() {
    EnergyComponent energy = new EnergyComponent(1);
    energy.spendEnergy(1); // now at 0

    CardPlayResult result = service.play(new CardPlayRequest("strike", "enemy"), deck, energy);

    assertFalse(result.success());
    assertEquals("Not enough energy", result.failureReason());
    assertTrue(result.updatedHand().contains("strike"));
    assertTrue(result.enemyEffects().isEmpty());
    assertTrue(deck.getHand().contains("strike"));
  }

  @Test
  void failsForUnknownCard() {
    CardPlayResult result =
        service.play(new CardPlayRequest("mystery", "enemy"), deck, new EnergyComponent(3));

    assertFalse(result.success());
    assertTrue(result.failureReason().contains("Unknown card"));
  }

  @Test
  void failsWhenCardNotInHand() {
    deck.playCard("strike"); // move it to discard first

    CardPlayResult result =
        service.play(new CardPlayRequest("strike", "enemy"), deck, new EnergyComponent(3));

    assertFalse(result.success());
    assertEquals("Card not in hand", result.failureReason());
  }

  @Test
  void worksWithoutAnEnergyComponent() {
    CardPlayResult result = service.play(new CardPlayRequest("bandage", "player"), deck, null);

    assertTrue(result.success());
    assertEquals(1, result.playerEffects().size());
    assertEquals(EffectType.HEAL, result.playerEffects().get(0).type());
  }
}
