package com.csse3200.game.components.combat;

/** Enumerates the permitted inputs to the FSM. These are used to validity check transitions. */
public enum BattleEvent {
  // Battle setup
  SETUP_COMPLETE,
  INTENTS_REVEALED,

  // Player phase
  PLAYER_TURN_STARTED,
  PLAYER_ATTACK_SELECTED,
  PLAYER_DEFEND_SELECTED,
  PLAYER_OTHER_SELECTED,
  PLAYER_END_REQUESTED,
  PLAYER_ACTION_RESOLVED,
  PLAYER_CONTINUES,
  PLAYER_TURN_ENDED,

  // Enemy phase
  ENEMY_ATTACK_SELECTED,
  ENEMY_DEFEND_SELECTED,
  ENEMY_OTHER_SELECTED,
  ENEMY_ACTION_RESOLVED,
  ADVANCE_ENEMY,
  MORE_ENEMIES,
  ENEMY_PHASE_COMPLETE,

  // Battle outcomes
  ENEMIES_DEFEATED,
  PLAYER_DEFEATED
}
