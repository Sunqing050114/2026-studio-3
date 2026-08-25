package com.csse3200.game.cards.effects;

/**
 * Minimal bridge from card effects to whichever character/status implementation owns runtime combat
 * state.
 */
public interface CharacterEffectGateway {
  void damage(int amount);

  void gainBlock(int amount);

  void heal(int amount);

  void applyPoison(int amount);

  void applyVulnerable(int amount);

  void applyStrength(int amount);
}
