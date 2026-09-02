package com.csse3200.game.cards.effects;

import com.csse3200.game.cards.CardPlayRequest;
import com.csse3200.game.cards.CardService;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.deck.BattleDeck;
import com.csse3200.game.components.player.EnergyComponent;
import java.util.Optional;

/**
 * Team 3's local stand-in for Team 5's single card-play entry point.
 *
 * <p>Given a {@link CardPlayRequest} it reads the card config from Team 6's library, checks the
 * player's energy, resolves the card's effects through {@link CardEffectResolver}, then commits the
 * deck and energy changes and returns a single {@link CardPlayResult}. When Team 5 ships their real
 * entry point, this class is the only thing Team 3 needs to swap out.
 */
public class CardResolutionService {
  private final CardService cardService;
  private final CardEffectResolver effectResolver;
  private final PlayerEffectState playerState;

  /**
   * @param cardService Team 6 card library used to look up card configs
   */
  public CardResolutionService(CardService cardService) {
    this(cardService, new CardEffectResolver(), new PlayerEffectState());
  }

  public CardResolutionService(
      CardService cardService, CardEffectResolver effectResolver, PlayerEffectState playerState) {
    if (cardService == null) {
      throw new IllegalArgumentException("cardService cannot be null");
    }
    if (effectResolver == null) {
      throw new IllegalArgumentException("effectResolver cannot be null");
    }
    if (playerState == null) {
      throw new IllegalArgumentException("playerState cannot be null");
    }
    this.cardService = cardService;
    this.effectResolver = effectResolver;
    this.playerState = playerState;
  }

  /**
   * Attempts to play a card.
   *
   * @param request the card and target the player chose
   * @param deck the current battle deck, mutated on success
   * @param energy the player's energy component, may be {@code null} when energy is not tracked
   * @return the outcome of the attempt
   */
  public CardPlayResult play(CardPlayRequest request, BattleDeck deck, EnergyComponent energy) {
    if (request == null) {
      throw new IllegalArgumentException("request cannot be null");
    }
    if (deck == null) {
      throw new IllegalArgumentException("deck cannot be null");
    }

    Optional<CardConfig> maybeCard = cardService.getCard(request.cardID());
    if (maybeCard.isEmpty()) {
      return CardPlayResult.failure(
          "Unknown card: " + request.cardID(), request.cardID(), request.targetID(), deck);
    }
    CardConfig card = maybeCard.get();

    if (!deck.getHand().contains(card.id)) {
      return CardPlayResult.failure("Card not in hand", card.id, request.targetID(), deck);
    }
    if (energy != null && !energy.canAfford(card.cost)) {
      return CardPlayResult.failure("Not enough energy", card.id, request.targetID(), deck);
    }

    CardEffectResolution resolution = effectResolver.resolve(card, playerState);

    // Commit: energy first (already checked affordable), then move the card to the discard pile.
    if (energy != null) {
      energy.spendEnergy(card.cost);
    }
    deck.playCard(card.id);

    return CardPlayResult.success(
        card.id,
        request.targetID(),
        resolution.enemyEffects(),
        resolution.playerEffects(),
        deck,
        card.cost);
  }
}
