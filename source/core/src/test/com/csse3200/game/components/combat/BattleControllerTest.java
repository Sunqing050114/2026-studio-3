package com.csse3200.game.components.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BattleControllerTest {
  private BattleController controller;

  @BeforeEach
  void setUp() {
    controller = new BattleController();
  }

  @Test
  void shouldStartInSetupWithNoCurrentEnemy() {
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
    assertEquals(-1, controller.getCurrentEnemyIndex());
  }

  @Test
  void shouldApplyValidTransitions() {
    controller.handle(BattleEvent.SETUP_COMPLETE);
    assertEquals(BattlePhase.REVEAL_INTENTS, controller.getCurrentPhase());

    controller.handle(BattleEvent.INTENTS_REVEALED);
    assertEquals(BattlePhase.PLAYER_START, controller.getCurrentPhase());

    controller.handle(BattleEvent.PLAYER_TURN_STARTED);
    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());

    controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED);
    assertEquals(BattlePhase.PLAYER_ATTACK, controller.getCurrentPhase());
  }

  @Test
  void shouldReportWhetherCurrentPhaseCanHandleEvent() {
    assertTrue(controller.canHandle(BattleEvent.SETUP_COMPLETE));
    assertFalse(controller.canHandle(BattleEvent.PLAYER_ATTACK_SELECTED));

    controller.handle(BattleEvent.SETUP_COMPLETE);

    assertTrue(controller.canHandle(BattleEvent.INTENTS_REVEALED));
    assertFalse(controller.canHandle(BattleEvent.SETUP_COMPLETE));
  }

  @Test
  void shouldCompletePlayerActionCycle() {
    advanceToPlayerTurn();

    controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED);
    assertEquals(BattlePhase.PLAYER_ATTACK, controller.getCurrentPhase());

    controller.handle(BattleEvent.PLAYER_ACTION_RESOLVED);
    assertEquals(BattlePhase.PLAYER_RESOLVED, controller.getCurrentPhase());

    controller.handle(BattleEvent.PLAYER_CONTINUES);
    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());
  }

  @Test
  void shouldEndPlayerTurnAndProcessMultipleEnemies() {
    advanceToEnemyTurn();

    controller.handle(BattleEvent.ENEMY_ATTACK_SELECTED);
    assertEquals(BattlePhase.ENEMY_ATTACK, controller.getCurrentPhase());

    controller.handle(BattleEvent.ENEMY_ACTION_RESOLVED);
    assertEquals(BattlePhase.ENEMY_RESOLVED, controller.getCurrentPhase());

    controller.handle(BattleEvent.ADVANCE_ENEMY);
    assertEquals(BattlePhase.NEXT_ENEMY, controller.getCurrentPhase());

    controller.handle(BattleEvent.MORE_ENEMIES);
    assertEquals(BattlePhase.ENEMY_TURN, controller.getCurrentPhase());

    controller.handle(BattleEvent.ENEMY_OTHER_SELECTED);
    controller.handle(BattleEvent.ENEMY_ACTION_RESOLVED);
    controller.handle(BattleEvent.ADVANCE_ENEMY);
    controller.handle(BattleEvent.ENEMY_PHASE_COMPLETE);

    assertEquals(BattlePhase.PLAYER_START, controller.getCurrentPhase());
  }

  @Test
  void shouldEnterVictoryAndRejectFurtherEvents() {
    advanceToPlayerStart();

    controller.handle(BattleEvent.ENEMIES_DEFEATED);

    assertEquals(BattlePhase.VICTORY, controller.getCurrentPhase());
    assertFalse(controller.canHandle(BattleEvent.PLAYER_TURN_STARTED));
    assertThrows(
        IllegalStateException.class, () -> controller.handle(BattleEvent.PLAYER_TURN_STARTED));
    assertEquals(BattlePhase.VICTORY, controller.getCurrentPhase());
  }

  @Test
  void shouldEnterDefeatAndRejectFurtherEvents() {
    advanceToPlayerStart();

    controller.handle(BattleEvent.PLAYER_DEFEATED);

    assertEquals(BattlePhase.DEFEAT, controller.getCurrentPhase());
    assertFalse(controller.canHandle(BattleEvent.PLAYER_TURN_STARTED));
    assertThrows(
        IllegalStateException.class, () -> controller.handle(BattleEvent.PLAYER_TURN_STARTED));
    assertEquals(BattlePhase.DEFEAT, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectNullEventWithoutChangingPhase() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> controller.handle(null));

    assertEquals("event cannot be null", exception.getMessage());
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectInvalidTransitionWithoutChangingPhase() {
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED));

    assertEquals(
        "Invalid battle transition: SETUP-->PLAYER_ATTACK_SELECTED", exception.getMessage());
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
  }

  private void advanceToPlayerStart() {
    controller.handle(BattleEvent.SETUP_COMPLETE);
    controller.handle(BattleEvent.INTENTS_REVEALED);
  }

  private void advanceToPlayerTurn() {
    advanceToPlayerStart();
    controller.handle(BattleEvent.PLAYER_TURN_STARTED);
  }

  private void advanceToEnemyTurn() {
    advanceToPlayerTurn();
    controller.handle(BattleEvent.PLAYER_END_REQUESTED);
    controller.handle(BattleEvent.PLAYER_TURN_ENDED);
  }
}
