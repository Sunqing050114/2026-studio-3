package com.csse3200.game.cards.play;

import com.csse3200.game.cards.EffectType;

/**
 * Read-only Team 5 boundary for player values that can affect card resolution.
 *
 * <p>The concrete adapter to Team 7 remains outside the card resolver so Team 7 stays the source of
 * truth for player combat state.
 */
public interface PlayerStateView {
  /** Team 7's currently implemented fixed Feeble outgoing-damage multiplier. */
  float FEEBLE_DAMAGE_MULTIPLIER = 0.75f;

  /**
   * @return the player's current energy
   */
  int currentEnergy();

  /**
   * Returns the current value/stacks for a card-related status, or zero when it is not active.
   *
   * @param type status type, such as STRENGTH or FEEBLE
   * @return current status value/stacks
   */
  int statusValue(EffectType type);

  /**
   * Returns the multiplier Team 5 should apply to outgoing card damage.
   *
   * <p>The default matches Team 7's current StatusEffectCalculator: Feeble is a fixed 25% damage
   * reduction while active and does not scale with its stored value. A concrete adapter can
   * override this method if the shared design changes later.
   */
  default float outgoingDamageMultiplier() {
    return statusValue(EffectType.FEEBLE) > 0 ? FEEBLE_DAMAGE_MULTIPLIER : 1.0f;
  }
}
