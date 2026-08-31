package com.csse3200.game.cards.effects;

/**
 * Immutable snapshot of external combat values used while resolving one card.
 *
 * <p>The multipliers describe card damage before Team 1/Team 7 apply block, armor, health and death
 * rules. They are snapshots so a single card is resolved consistently even if another system
 * changes combat state at the same time.
 */
public record CardEffectResolutionContext(
    int strength, float outgoingDamageMultiplier, float incomingDamageMultiplier) {
  public CardEffectResolutionContext {
    validateMultiplier(outgoingDamageMultiplier, "Outgoing damage multiplier");
    validateMultiplier(incomingDamageMultiplier, "Incoming damage multiplier");
  }

  /** Creates a context using only Team 5's backwards-compatible calculation state. */
  public static CardEffectResolutionContext from(PlayerEffectState playerState) {
    if (playerState == null) {
      throw new IllegalArgumentException("Player effect state cannot be null");
    }
    return new CardEffectResolutionContext(playerState.getStrength(), 1.0f, 1.0f);
  }

  /** Applies the captured modifiers and clamps the final outgoing damage to zero. */
  public int resolveDamage(int baseDamage) {
    float modifiedDamage =
        (baseDamage + strength) * outgoingDamageMultiplier * incomingDamageMultiplier;
    return Math.max(0, Math.round(modifiedDamage));
  }

  private static void validateMultiplier(float multiplier, String label) {
    if (!Float.isFinite(multiplier) || multiplier < 0.0f) {
      throw new IllegalArgumentException(label + " must be finite and non-negative");
    }
  }
}
