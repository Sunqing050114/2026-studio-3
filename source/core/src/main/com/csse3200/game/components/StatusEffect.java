package com.csse3200.game.components;

/**
 * Represents a single active status effect on a combat entity, such as Vulnerable or Weak. A status
 * effect has an identifier, a value used to modify combat calculations, and a duration measured in
 * remaining turns.
 *
 * <p>NOTE: effectValue is an int (aligned with Team 6's EffectConfig_value, which represents a
 * number of stacks). This class does not interpret what the value/stacks mean for any specific
 * effect type - that calculation is owned elsewhere (see CombatStatsComponent javadoc).
 *
 * <p>NOTE: a duration of 0 or less represents a permanent effect that does not expire from ticking
 * (e.g. Strength, which Team 6 defines with duration=0 and lasts for the rest of combat). Only a
 * positive duration counts down and eventually expires. Pending confirmation with Team 6 that this
 * interpretation matches their design intent.
 */
public class StatusEffect {

  private final String type;
  private final int value;
  private int duration;

  /**
   * Creates a new status effect.
   *
   * @param type identifier for the effect, e.g. "VULNERABLE"
   * @param value magnitude of the effect (e.g. number of stacks), interpretation is defined
   *     elsewhere per effect type
   * @param duration number of turns the effect remains active for; 0 or less means permanent (does
   *     not expire from ticking)
   */
  public StatusEffect(String type, int value, int duration) {
    this.type = type;
    this.value = value;
    this.duration = duration;
  }

  /**
   * Returns the type identifier of this effect.
   *
   * @return effect type
   */
  public String getType() {
    return type;
  }

  /**
   * Returns the magnitude/stack value of this effect.
   *
   * @return effect value
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the number of turns remaining before this effect expires. 0 or less means the effect is
   * permanent and will not expire from ticking.
   *
   * @return remaining duration
   */
  public int getDuration() {

    return duration;
  }

  /**
   * Called when a turn ends. Decrements the remaining duration by 1.
   *
   * @return true if the effect has now expired (duration reached 0 or below) and should be removed
   */
  public boolean tickAndCheckExpired() {
    if (duration <= 0) {
      return false;
    }
    duration--;
    return duration <= 0;
  }
}
