package com.csse3200.game.cards.effects;

import com.csse3200.game.cards.config.EffectConfig;

/** Applies a configured card effect to one already-resolved target. */
public class EffectExecutor {
  public void execute(EffectConfig effect, CharacterEffectGateway target) {
    if (effect == null) {
      throw new IllegalArgumentException("Effect cannot be null");
    }
    if (effect.type == null) {
      throw new IllegalArgumentException("Effect type cannot be null");
    }
    if (target == null) {
      throw new IllegalArgumentException("Effect target cannot be null");
    }

    switch (effect.type) {
      case DAMAGE:
        target.damage(effect.amount);
        break;
      case BLOCK:
        target.gainBlock(effect.amount);
        break;
      case HEAL:
        target.heal(effect.amount);
        break;
      case POISON:
        target.applyPoison(effect.amount);
        break;
      case VULNERABLE:
        target.applyVulnerable(effect.amount);
        break;
      case STRENGTH:
        target.applyStrength(effect.amount);
        break;
      default:
        throw new IllegalArgumentException("Unsupported effect type: " + effect.type);
    }
  }
}
