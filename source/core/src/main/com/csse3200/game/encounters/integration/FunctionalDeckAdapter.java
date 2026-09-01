package com.csse3200.game.encounters.integration;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Adapts Team 5's final PlayerDeck method shapes to Team 2's deck boundary.
 *
 * <p>The add operation is a {@link Consumer} because PlayerDeck.addCard(String) returns void. The
 * remove operation is a {@link Predicate} because PlayerDeck.removeCard(String) returns boolean.
 */
public final class FunctionalDeckAdapter implements DeckGateway {
  private final Consumer<String> addCard;
  private final Predicate<String> removeCard;

  public FunctionalDeckAdapter(Consumer<String> addCard, Predicate<String> removeCard) {
    this.addCard = Objects.requireNonNull(addCard, "addCard cannot be null");
    this.removeCard = Objects.requireNonNull(removeCard, "removeCard cannot be null");
  }

  @Override
  public boolean addCard(String cardId) {
    if (cardId == null || cardId.isBlank()) {
      return false;
    }
    addCard.accept(cardId);
    return true;
  }

  @Override
  public boolean removeCard(String cardId) {
    return cardId != null && !cardId.isBlank() && removeCard.test(cardId);
  }
}
