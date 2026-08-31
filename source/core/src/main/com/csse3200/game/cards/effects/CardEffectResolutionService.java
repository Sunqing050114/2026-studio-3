package com.csse3200.game.cards.effects;

import com.csse3200.game.cards.CardService;
import com.csse3200.game.cards.EffectType;
import com.csse3200.game.cards.configs.CardConfig;
import java.util.List;

/**
 * Public Team 5 entry point for resolving cards and querying the current turn's results.
 *
 * <p>The service owns only Team 5's calculation state and result history. It does not apply the
 * returned effects to real player or enemy statistics; consuming systems remain responsible for
 * those entity updates.
 */
public final class CardEffectResolutionService {
  private final CardEffectResolver resolver;
  private final PlayerEffectState playerEffectState;
  private final CardEffectResultStore resultStore;

  /** Creates a combat-scoped service backed by Team 6's public card retrieval API. */
  public CardEffectResolutionService(CardService cardService) {
    this(
        new CardEffectResolver(requireCardService(cardService)),
        new PlayerEffectState(),
        new TurnEffectStore());
  }

  /** Creates a service with explicit dependencies for integration and testing. */
  public CardEffectResolutionService(
      CardEffectResolver resolver,
      PlayerEffectState playerEffectState,
      CardEffectResultStore resultStore) {
    if (resolver == null) {
      throw new IllegalArgumentException("Card effect resolver cannot be null");
    }
    if (playerEffectState == null) {
      throw new IllegalArgumentException("Player effect state cannot be null");
    }
    if (resultStore == null) {
      throw new IllegalArgumentException("Card effect result store cannot be null");
    }
    this.resolver = resolver;
    this.playerEffectState = playerEffectState;
    this.resultStore = resultStore;
  }

  /** Resolves a card by ID and records the successful result for current-turn consumers. */
  public CardEffectResolution resolve(String cardId) {
    CardEffectResolution resolution = resolver.resolve(cardId, playerEffectState);
    resultStore.record(resolution);
    return resolution;
  }

  /** Resolves an already retrieved Team 6 card config and records the successful result. */
  public CardEffectResolution resolve(CardConfig card) {
    CardEffectResolution resolution = resolver.resolve(card, playerEffectState);
    resultStore.record(resolution);
    return resolution;
  }

  /** Prepares a resolution from Team 5's internal state without recording or mutating it. */
  public CardEffectResolution resolveUnrecorded(CardConfig card) {
    return resolver.resolveWithoutStateUpdate(
        card, CardEffectResolutionContext.from(playerEffectState));
  }

  /** Prepares a resolution from external state without recording or mutating Team 5 state. */
  public CardEffectResolution resolveUnrecorded(
      CardConfig card, CardEffectResolutionContext context) {
    return resolver.resolveWithoutStateUpdate(card, context);
  }

  /**
   * Commits a resolution after the complete card-play transaction succeeds.
   *
   * @param resolution prepared immutable resolution
   * @param updateCalculationState whether Team 5's backwards-compatible Strength state should be
   *     updated; false when Team 7 is the source of truth through a PlayerStateView
   */
  public void recordSuccessful(CardEffectResolution resolution, boolean updateCalculationState) {
    resultStore.record(resolution);
    if (updateCalculationState) {
      CardEffectResolver.updateCalculationState(resolution, playerEffectState);
    }
  }

  /**
   * @return recorded card resolutions in play order
   */
  public List<CardEffectResolution> getResolutions() {
    return resultStore.getResolutions();
  }

  /**
   * @return current-turn enemy effects for Team 1 or the battle system
   */
  public List<ResolvedCardEffect> getEnemyEffects() {
    return resultStore.getEnemyEffects();
  }

  /**
   * @return current-turn player effects for Team 7 or the battle system
   */
  public List<ResolvedCardEffect> getPlayerEffects() {
    return resultStore.getPlayerEffects();
  }

  /**
   * @return current-turn effects matching the requested type
   */
  public List<ResolvedCardEffect> getEffectsOfType(EffectType type) {
    return resultStore.getEffectsOfType(type);
  }

  /**
   * Clears queryable turn results while preserving combat-long calculation state such as strength.
   */
  public void clearTurnResults() {
    resultStore.clear();
  }

  private static CardService requireCardService(CardService cardService) {
    if (cardService == null) {
      throw new IllegalArgumentException("Card service cannot be null");
    }
    return cardService;
  }
}
