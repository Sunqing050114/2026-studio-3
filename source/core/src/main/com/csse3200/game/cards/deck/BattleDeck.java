package com.csse3200.game.cards.deck;

import java.util.ArrayList;
import java.util.List;

/**
 * Runtime deck state for a single combat encounter.
 *
 * <p>A battle deck is created from the player's long-term deck at battle start, then owns its own
 * draw pile, hand and discard pile. Mutating this class should not change the original {@link
 * PlayerDeck}.
 */
public class BattleDeck {
  private final List<String> drawPile = new ArrayList<>();
  private final List<String> hand = new ArrayList<>();
  private final List<String> discardPile = new ArrayList<>();

  /**
   * Creates battle deck state from a player deck.
   *
   * @param playerDeck source player deck
   */
  public BattleDeck(PlayerDeck playerDeck) {
    if (playerDeck == null) {
      throw new IllegalArgumentException("playerDeck must not be null");
    }
    drawPile.addAll(playerDeck.getCardIds());
  }
}
