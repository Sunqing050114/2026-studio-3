package com.csse3200.game.cards;

import com.csse3200.game.cards.configs.CardConfig;
import java.util.List;
import java.util.Optional;

/**
 * Provides access to the card configurations available to the game.
 *
 * <p>Cards can be retrieved individually using their unique identifier or enumerated as a
 * collection.
 */
public interface CardService {
  /**
   * Retrieves the configuration of the card with the given identifier.
   *
   * @param cardId the unique identifier of the card to retrieve
   * @return the card configuration, or an empty {@link Optional} if no card has the given
   *     identifier
   */
  Optional<CardConfig> getCard(String cardId);

  /**
   * Retrieves all card configurations currently available to the game.
   *
   * @return an unmodifiable snapshot of all registered card configurations
   */
  List<CardConfig> getAllCards();
}
