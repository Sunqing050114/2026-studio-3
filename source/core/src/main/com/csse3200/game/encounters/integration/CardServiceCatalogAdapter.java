package com.csse3200.game.encounters.integration;

import com.csse3200.game.cards.CardService;
import java.util.Objects;

/** Adapts Team 6's public CardService API to Team 2's card-catalog boundary. */
public final class CardServiceCatalogAdapter implements CardCatalogGateway {
  private final CardService cardService;

  public CardServiceCatalogAdapter(CardService cardService) {
    this.cardService = Objects.requireNonNull(cardService, "cardService cannot be null");
  }

  @Override
  public boolean containsCard(String cardId) {
    return cardId != null && !cardId.isBlank() && cardService.getCard(cardId).isPresent();
  }
}
