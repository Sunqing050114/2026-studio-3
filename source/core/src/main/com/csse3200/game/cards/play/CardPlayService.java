package com.csse3200.game.cards.play;

import com.csse3200.game.cards.CardService;
import com.csse3200.game.cards.CardValidator;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.deck.BattleDeck;
import com.csse3200.game.cards.effects.CardEffectResolution;
import com.csse3200.game.cards.effects.CardEffectResolutionService;
import com.csse3200.game.components.player.EnergyComponent;
import java.util.List;

/**
 * Coordinates Team 5 card play with Team 7 energy state.
 *
 * <p>This class does not own player energy. It asks Team 7's {@link EnergyComponent} to spend the
 * card cost, then resolves Team 5 card effects and moves the card from hand to discard.
 */
public final class CardPlayService {
  private final CardService cardService;
  private final CardEffectResolutionService resolutionService;
  private final BattleDeck battleDeck;
  private final EnergyComponent energyComponent;

  /**
   * Creates a play service using Team 6 card retrieval, Team 5 effect resolution and Team 7 energy.
   *
   * @param cardService source of card configs
   * @param battleDeck current combat deck state
   * @param energyComponent Team 7 player energy component
   */
  public CardPlayService(
      CardService cardService, BattleDeck battleDeck, EnergyComponent energyComponent) {
    this(
        cardService,
        new CardEffectResolutionService(requireCardService(cardService)),
        battleDeck,
        energyComponent);
  }

  /** Creates a play service with explicit dependencies for integration and testing. */
  public CardPlayService(
      CardService cardService,
      CardEffectResolutionService resolutionService,
      BattleDeck battleDeck,
      EnergyComponent energyComponent) {
    this.cardService = requireCardService(cardService);
    if (resolutionService == null) {
      throw new IllegalArgumentException("Card effect resolution service cannot be null");
    }
    if (battleDeck == null) {
      throw new IllegalArgumentException("Battle deck cannot be null");
    }
    if (energyComponent == null) {
      throw new IllegalArgumentException("Energy component cannot be null");
    }
    this.resolutionService = resolutionService;
    this.battleDeck = battleDeck;
    this.energyComponent = energyComponent;
  }

  /**
   * Read-only check for whether a card is currently playable.
   *
   * <p>This is intended for UI previews. The authoritative play path remains {@link #playCard},
   * which calls Team 7's {@link EnergyComponent#spendEnergy(int)}.
   *
   * @param cardId card ID to check
   * @return true if the card is in hand and Team 7 says the player can afford it
   */
  public boolean canPlay(String cardId) {
    CardConfig card = getPlayableCardConfig(cardId);
    return battleDeck.getHand().contains(card.id) && energyComponent.canAfford(card.cost);
  }

  /**
   * Attempts to play a card from the current hand.
   *
   * <p>When successful, Team 7 energy is spent first, then Team 5 resolves effects, then the battle
   * deck moves the card from hand to discard. When energy is insufficient, no card effects are
   * resolved and the hand remains unchanged.
   *
   * @param cardId card ID to play
   * @return structured play result with either resolved effects or a failure reason
   */
  public CardPlayResult playCard(String cardId) {
    CardConfig card = getPlayableCardConfig(cardId);
    if (!battleDeck.getHand().contains(card.id)) {
      return CardPlayResult.failure(card.id, card.cost, CardPlayFailureReason.CARD_NOT_IN_HAND);
    }
    if (!energyComponent.spendEnergy(card.cost)) {
      return CardPlayResult.failure(card.id, card.cost, CardPlayFailureReason.INSUFFICIENT_ENERGY);
    }

    CardEffectResolution resolution = resolutionService.resolve(card);
    if (!battleDeck.playCard(card.id)) {
      throw new IllegalStateException(
          "Card was no longer in hand after energy was spent: " + card.id);
    }
    return CardPlayResult.success(card.id, card.cost, resolution);
  }

  private CardConfig getPlayableCardConfig(String cardId) {
    if (cardId == null || cardId.isBlank()) {
      throw new IllegalArgumentException("Card ID cannot be null or blank");
    }
    CardConfig card =
        cardService
            .getCard(cardId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown card ID: " + cardId));
    List<String> errors = CardValidator.validate(card);
    if (!errors.isEmpty()) {
      throw new IllegalArgumentException("Invalid card config: " + String.join("; ", errors));
    }
    return card;
  }

  private static CardService requireCardService(CardService cardService) {
    if (cardService == null) {
      throw new IllegalArgumentException("Card service cannot be null");
    }
    return cardService;
  }
}
