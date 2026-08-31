package com.csse3200.game.cards.effects;

import com.csse3200.game.cards.CardService;
import com.csse3200.game.cards.CardValidator;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.configs.EffectConfig;
import java.util.ArrayList;
import java.util.List;

/** Resolves Team 6 card configs into Team 5 card effect results. */
public class CardEffectResolver {
  private final CardService cardService;
  private final EffectExecutor effectExecutor;

  public CardEffectResolver() {
    this(null, new EffectExecutor());
  }

  public CardEffectResolver(CardService cardService) {
    this(cardService, new EffectExecutor());
  }

  public CardEffectResolver(EffectExecutor effectExecutor) {
    this(null, effectExecutor);
  }

  public CardEffectResolver(CardService cardService, EffectExecutor effectExecutor) {
    if (effectExecutor == null) {
      throw new IllegalArgumentException("Effect executor cannot be null");
    }
    this.cardService = cardService;
    this.effectExecutor = effectExecutor;
  }

  /** Resolves a card that has already been retrieved from Team 6's card library. */
  public CardEffectResolution resolve(CardConfig card, PlayerEffectState playerState) {
    CardEffectResolution resolution =
        resolveWithoutStateUpdate(card, CardEffectResolutionContext.from(playerState));
    updateCalculationState(resolution, playerState);
    return resolution;
  }

  /**
   * Resolves a card without mutating Team 5 calculation state.
   *
   * <p>This preview/prepare operation is used by {@code CardPlayService} so failed plays cannot
   * leave Strength or result-history side effects behind.
   */
  public CardEffectResolution resolveWithoutStateUpdate(
      CardConfig card, CardEffectResolutionContext context) {
    validate(card, context);

    List<ResolvedCardEffect> results = new ArrayList<>();
    for (int i = 0; i < card.effects.length; i++) {
      results.add(effectExecutor.resolve(card.id, card.effects[i], card.target, i, context));
    }
    return new CardEffectResolution(card.id, results);
  }

  /** Resolves a card by ID through the Team 6 card service supplied to this resolver. */
  public CardEffectResolution resolve(String cardId, PlayerEffectState playerState) {
    if (cardService == null) {
      throw new IllegalStateException("Card service is required to resolve by card ID");
    }
    if (cardId == null || cardId.isBlank()) {
      throw new IllegalArgumentException("Card ID cannot be null or blank");
    }
    CardConfig card =
        cardService
            .getCard(cardId)
            .orElseThrow(() -> new IllegalArgumentException("Unknown card ID: " + cardId));
    return resolve(card, playerState);
  }

  private void validate(CardConfig card, CardEffectResolutionContext context) {
    if (context == null) {
      throw new IllegalArgumentException("Card effect resolution context cannot be null");
    }
    List<String> errors = CardValidator.validate(card);
    if (!errors.isEmpty()) {
      throw new IllegalArgumentException("Invalid card config: " + String.join("; ", errors));
    }
    for (EffectConfig effect : card.effects) {
      if (effect == null) {
        throw new IllegalArgumentException("Card effects cannot contain null");
      }
    }
  }

  static void updateCalculationState(
      CardEffectResolution resolution, PlayerEffectState playerState) {
    if (resolution == null || playerState == null) {
      throw new IllegalArgumentException("Resolution and player effect state cannot be null");
    }
    resolution.effects().stream()
        .filter(effect -> effect.type() == com.csse3200.game.cards.EffectType.STRENGTH)
        .forEach(effect -> playerState.addStrength(effect.value()));
  }
}
