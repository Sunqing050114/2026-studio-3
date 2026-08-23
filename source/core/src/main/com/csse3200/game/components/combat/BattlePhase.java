package com.csse3200.game.components.combat;

/**
 * Defines the predetermined states of the Finite State Machine. Represents the current phase of the
 * deterministic game loop.
 */
public enum BattlePhase {
  // Beginning States
  SETUP,
  REVEAL_INTENTS,

  // Player States
  PLAYER_START,
  PLAYER_TURN,
  PLAYER_ATTACK,
  PLAYER_DEFEND,
  PLAYER_OTHER,
  PLAYER_END,
  PLAYER_RESOLVED,

  // Enemy States
  ENEMY_TURN,
  ENEMY_ATTACK,
  ENEMY_DEFEND,
  ENEMY_OTHER,
  ENEMY_RESOLVED,
  NEXT_ENEMY,

  // Terminal States
  VICTORY,
  DEFEAT
}
