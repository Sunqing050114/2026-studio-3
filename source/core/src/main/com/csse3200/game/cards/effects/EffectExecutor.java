package com.csse3200.game.cards.effects;

import com.csse3200.game.cards.EffectType;
import com.csse3200.game.cards.TargetType;
import com.csse3200.game.cards.configs.EffectConfig;

/** Resolves one Team 6 effect config into a Team 5 card effect result. */
public class EffectExecutor {
  /**
   * Resolves one effect from a card.
   *
   * <p>This class does not mutate enemies or the player entity. It only updates Team 5 calculation
   * state where needed, such as strength, and returns a resolved effect record.
   */
  public ResolvedCardEffect resolve(
      String cardId,
      EffectConfig effect,
      TargetType target,
      int sequence,
      PlayerEffectState playerState) {
    return resolve(cardId, effect, target, sequence, CardEffectResolutionContext.from(playerState));
  }

  /** Resolves one effect using a snapshot of the current player and target combat modifiers. */
  public ResolvedCardEffect resolve(
      String cardId,
      EffectConfig effect,
      TargetType target,
      int sequence,
      CardEffectResolutionContext context) {
    validate(cardId, effect, target, sequence, context);

    if (target == TargetType.SELF) {
      return resolveSelfEffect(cardId, effect, sequence);
    }

    return resolveEnemyEffect(cardId, effect, target, sequence, context);
  }

  private void validate(
      String cardId,
      EffectConfig effect,
      TargetType target,
      int sequence,
      CardEffectResolutionContext context) {
    if (cardId == null || cardId.isBlank()) {
      throw new IllegalArgumentException("Card ID cannot be null or blank");
    }
    if (effect == null) {
      throw new IllegalArgumentException("Effect config cannot be null");
    }
    if (effect.type == null) {
      throw new IllegalArgumentException("Effect type cannot be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("Target type cannot be null");
    }
    if (sequence < 0) {
      throw new IllegalArgumentException("Effect sequence cannot be negative");
    }
    if (context == null) {
      throw new IllegalArgumentException("Card effect resolution context cannot be null");
    }
    if (effect.value <= 0) {
      throw new IllegalArgumentException("Effect value must be positive");
    }
    if (effect.type.usesDuration() && effect.duration <= 0) {
      throw new IllegalArgumentException("Ongoing effect duration must be positive");
    }
    if (!effect.type.usesDuration() && effect.duration != 0) {
      throw new IllegalArgumentException("Instant or combat-long effect duration must be zero");
    }
  }

  private ResolvedCardEffect resolveSelfEffect(String cardId, EffectConfig effect, int sequence) {
    if (effect.type != EffectType.STRENGTH
        && effect.type != EffectType.BLOCK
        && effect.type != EffectType.HEAL) {
      throw new IllegalArgumentException("Unsupported self-targeting effect type: " + effect.type);
    }

    return new ResolvedCardEffect(
        cardId, effect.type, TargetType.SELF, effect.value, effect.duration, sequence);
  }

  private ResolvedCardEffect resolveEnemyEffect(
      String cardId,
      EffectConfig effect,
      TargetType target,
      int sequence,
      CardEffectResolutionContext context) {
    if (effect.type == EffectType.DAMAGE) {
      return new ResolvedCardEffect(
          cardId, EffectType.DAMAGE, target, context.resolveDamage(effect.value), 0, sequence);
    }

    if (effect.type.usesDuration()) {
      return new ResolvedCardEffect(
          cardId, effect.type, target, effect.value, effect.duration, sequence);
    }

    throw new IllegalArgumentException("Unsupported enemy-targeting effect type: " + effect.type);
  }
}
