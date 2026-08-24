package com.csse3200.game.cards.deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class BattleDeckTest {
  @Test
  void shouldCreateDrawPileFromPlayerDeck() {
    PlayerDeck playerDeck = new PlayerDeck(List.of("strike", "defend", "bandage"));
    BattleDeck battleDeck = new BattleDeck(playerDeck);

    assertIterableEquals(List.of("strike", "defend", "bandage"), battleDeck.getDrawPile());
    assertTrue(battleDeck.getHand().isEmpty());
    assertTrue(battleDeck.getDiscardPile().isEmpty());
    assertEquals(3, battleDeck.getDrawPileSize());
    assertEquals(0, battleDeck.getHandSize());
    assertEquals(0, battleDeck.getDiscardPileSize());
  }

  @Test
  void shouldNotMutateOriginalPlayerDeck() {
    PlayerDeck playerDeck = new PlayerDeck(List.of("strike", "defend"));
    BattleDeck battleDeck = new BattleDeck(playerDeck);

    battleDeck.drawOne();

    assertIterableEquals(List.of("strike", "defend"), playerDeck.getCardIds());
    assertIterableEquals(List.of("defend"), battleDeck.getDrawPile());
  }

  @Test
  void shouldRejectNullPlayerDeck() {
    assertThrows(IllegalArgumentException.class, () -> new BattleDeck(null));
  }

  @Test
  void shouldDrawOneCardIntoHand() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike", "defend")));

    String drawnCard = battleDeck.drawOne();

    assertEquals("strike", drawnCard);
    assertIterableEquals(List.of("defend"), battleDeck.getDrawPile());
    assertIterableEquals(List.of("strike"), battleDeck.getHand());
    assertEquals(1, battleDeck.getDrawPileSize());
    assertEquals(1, battleDeck.getHandSize());
  }

  @Test
  void shouldReturnNullWhenDrawingFromEmptyDrawPile() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck());

    assertNull(battleDeck.drawOne());
    assertTrue(battleDeck.getDrawPile().isEmpty());
    assertTrue(battleDeck.getHand().isEmpty());
  }

  @Test
  void shouldDrawMultipleCardsIntoHand() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike", "defend", "bandage")));

    List<String> drawnCards = battleDeck.drawCards(2);

    assertIterableEquals(List.of("strike", "defend"), drawnCards);
    assertIterableEquals(List.of("bandage"), battleDeck.getDrawPile());
    assertIterableEquals(List.of("strike", "defend"), battleDeck.getHand());
  }

  @Test
  void shouldDrawOnlyAvailableCards() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike", "defend")));

    List<String> drawnCards = battleDeck.drawCards(5);

    assertIterableEquals(List.of("strike", "defend"), drawnCards);
    assertTrue(battleDeck.getDrawPile().isEmpty());
    assertIterableEquals(List.of("strike", "defend"), battleDeck.getHand());
  }

  @Test
  void shouldDrawNoCardsWhenCountIsZero() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike", "defend")));

    List<String> drawnCards = battleDeck.drawCards(0);

    assertTrue(drawnCards.isEmpty());
    assertIterableEquals(List.of("strike", "defend"), battleDeck.getDrawPile());
    assertTrue(battleDeck.getHand().isEmpty());
  }

  @Test
  void shouldRejectNegativeDrawCount() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike")));

    assertThrows(IllegalArgumentException.class, () -> battleDeck.drawCards(-1));
  }

  @Test
  void shouldReturnImmutableSnapshots() {
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(List.of("strike", "defend")));

    assertThrows(
        UnsupportedOperationException.class, () -> battleDeck.getDrawPile().add("bandage"));
    assertThrows(UnsupportedOperationException.class, () -> battleDeck.getHand().add("bandage"));
    assertThrows(
        UnsupportedOperationException.class, () -> battleDeck.getDiscardPile().add("bandage"));

    assertIterableEquals(List.of("strike", "defend"), battleDeck.getDrawPile());
    assertTrue(battleDeck.getHand().isEmpty());
    assertTrue(battleDeck.getDiscardPile().isEmpty());
  }

  @Test
  void shouldShuffleWithoutChangingCards() {
    List<String> startingCards =
        List.of("strike", "defend", "poison_dagger", "expose", "bandage", "inner_focus");
    BattleDeck battleDeck = new BattleDeck(new PlayerDeck(startingCards));

    battleDeck.shuffleDrawPile();

    List<String> expectedCards = new ArrayList<>(startingCards);
    List<String> actualCards = new ArrayList<>(battleDeck.getDrawPile());
    Collections.sort(expectedCards);
    Collections.sort(actualCards);
    assertIterableEquals(expectedCards, actualCards);
    assertEquals(startingCards.size(), battleDeck.getDrawPileSize());
    assertTrue(battleDeck.getHand().isEmpty());
  }
}
