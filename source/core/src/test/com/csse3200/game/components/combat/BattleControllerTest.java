package com.csse3200.game.components.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyIntent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BattleControllerTest {
  private BattleController controller;
  private Entity player;
  private List<Entity> enemies;
  private EnemyBehaviourComponent firstEnemyBehaviour;
  private EnemyBehaviourComponent secondEnemyBehaviour;

  @BeforeEach
  void setUp() {
    player = new Entity();
    firstEnemyBehaviour = mock(EnemyBehaviourComponent.class);
    secondEnemyBehaviour = mock(EnemyBehaviourComponent.class);
    enemies =
        List.of(
            createLivingDefendingEnemy(firstEnemyBehaviour),
            createLivingDefendingEnemy(secondEnemyBehaviour));
    controller = new BattleController(player, enemies);
  }

  @Test
  void shouldRejectNullPlayer() {
    assertThrows(IllegalArgumentException.class, () -> new BattleController(null, enemies));
  }

  @Test
  void shouldRejectEmptyEnemyList() {
    assertThrows(IllegalArgumentException.class, () -> new BattleController(player, List.of()));
  }

  @Test
  void shouldRejectNullEnemyList() {
    assertThrows(IllegalArgumentException.class, () -> new BattleController(player, null));
  }

  @Test
  void shouldRejectNullEnemy() {
    List<Entity> enemiesWithNull = Collections.singletonList(null);

    assertThrows(
        IllegalArgumentException.class, () -> new BattleController(player, enemiesWithNull));
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

    assertEquals(BattlePhase.PLAYER_START, controller.getCurrentPhase());
    assertEquals(-1, controller.getCurrentEnemyIndex());
    verify(firstEnemyBehaviour).rollIntent();
    verify(firstEnemyBehaviour).executeIntent(player);
    verify(secondEnemyBehaviour).rollIntent();
    verify(secondEnemyBehaviour).executeIntent(player);
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

  private Entity createLivingDefendingEnemy(EnemyBehaviourComponent behaviour) {
    Entity enemy = mock(Entity.class);
    EnemyStatsComponent stats = mock(EnemyStatsComponent.class);
    when(enemy.getComponent(EnemyBehaviourComponent.class)).thenReturn(behaviour);
    when(enemy.getComponent(EnemyStatsComponent.class)).thenReturn(stats);
    when(behaviour.rollIntent()).thenReturn(EnemyIntent.defend(1));
    when(stats.isAlive()).thenReturn(true);
    return enemy;
  }
}
