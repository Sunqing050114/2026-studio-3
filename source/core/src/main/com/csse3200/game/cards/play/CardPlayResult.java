package com.csse3200.game.cards.play;

import com.csse3200.game.cards.effects.CardEffectResolution;
import com.csse3200.game.cards.effects.ResolvedCardEffect;
import java.util.List;

/** Immutable result of attempting to play one card from the player's current hand. */
public record CardPlayResult(
    String cardId,
    boolean successful,
    int energyCost,
    CardEffectResolution resolution,
    CardPlayFailureReason failureReason) {
  public CardPlayResult {
    if (cardId == null || cardId.isBlank()) {
      throw new IllegalArgumentException("Card ID cannot be null or blank");
    }
    if (energyCost < 0) {
      throw new IllegalArgumentException("Energy cost cannot be negative");
    }
    if (failureReason == null) {
      throw new IllegalArgumentException("Card play failure reason cannot be null");
    }
    if (successful) {
      if (failureReason != CardPlayFailureReason.NONE) {
        throw new IllegalArgumentException("Successful card play cannot have a failure reason");
      }
      if (resolution == null) {
        throw new IllegalArgumentException("Successful card play must include a resolution");
      }
    } else {
      if (failureReason == CardPlayFailureReason.NONE) {
        throw new IllegalArgumentException("Failed card play must include a failure reason");
      }
      if (resolution != null) {
        throw new IllegalArgumentException("Failed card play cannot include a resolution");
      }
    }
  }

  /**
   * Creates a successful play result.
   *
   * @param cardId played card ID
   * @param energyCost energy spent through Team 7
   * @param resolution resolved card effects from Team 5
   * @return successful card play result
   */
  public static CardPlayResult success(
      String cardId, int energyCost, CardEffectResolution resolution) {
    return new CardPlayResult(cardId, true, energyCost, resolution, CardPlayFailureReason.NONE);
  }

  /**
   * Creates a failed play result.
   *
   * @param cardId requested card ID
   * @param energyCost energy cost that would have been paid
   * @param failureReason reason the card was not played
   * @return failed card play result
   */
  public static CardPlayResult failure(
      String cardId, int energyCost, CardPlayFailureReason failureReason) {
    return new CardPlayResult(cardId, false, energyCost, null, failureReason);
  }

  /**
   * @return resolved enemy-targeting effects, or an empty list when the card was not played
   */
  public List<ResolvedCardEffect> enemyEffects() {
    return resolution == null ? List.of() : resolution.enemyEffects();
  }

  /**
   * @return resolved player-targeting effects, or an empty list when the card was not played
   */
  public List<ResolvedCardEffect> playerEffects() {
    return resolution == null ? List.of() : resolution.playerEffects();
  }
}
