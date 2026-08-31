package com.csse3200.game.components.combat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyIntent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BattleControllerTest {
  private BattleController controller;
  private Entity player;
  private List<Entity> enemies;
  private EnemyBehaviourComponent firstEnemyBehaviour;
  private EnemyBehaviourComponent secondEnemyBehaviour;

  @BeforeEach
  void setUp() {
    player = new Entity().addComponent(new CombatStatsComponent(20, 0));
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
    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());

    controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED);
    assertEquals(BattlePhase.PLAYER_ATTACK, controller.getCurrentPhase());
  }

  @Test
  void shouldReportHandledEvents() {
    assertTrue(controller.canHandle(BattleEvent.SETUP_COMPLETE));
    assertFalse(controller.canHandle(BattleEvent.PLAYER_ATTACK_SELECTED));

    controller.handle(BattleEvent.SETUP_COMPLETE);

    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());
    assertTrue(controller.canHandle(BattleEvent.PLAYER_ATTACK_SELECTED));
    assertTrue(controller.canHandle(BattleEvent.PLAYER_END_REQUESTED));
    assertFalse(controller.canHandle(BattleEvent.INTENTS_REVEALED));
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
  void shouldProcessMultipleEnemies() {
    advanceToEnemyTurn();

    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());
    assertEquals(0, controller.getCurrentEnemyIndex());
    verify(firstEnemyBehaviour, times(2)).rollIntent();
    verify(firstEnemyBehaviour).executeIntent(player);
    verify(secondEnemyBehaviour).rollIntent();
    verify(secondEnemyBehaviour).executeIntent(player);
  }

  @Test
  void shouldCompleteTwoBattleRounds() {
    controller.start();

    completePlayerTurn();
    completePlayerTurn();

    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());
    verify(firstEnemyBehaviour, times(2)).executeIntent(player);
    verify(secondEnemyBehaviour, times(2)).executeIntent(player);
  }

  @Test
  void shouldSkipDeadEnemies() {
    EnemyBehaviourComponent deadEnemyBehaviour = mock(EnemyBehaviourComponent.class);
    controller =
        new BattleController(
            player,
            List.of(
                createDefendingEnemy(deadEnemyBehaviour, false),
                createLivingDefendingEnemy(firstEnemyBehaviour)));

    controller.start();
    completePlayerTurn();

    verify(deadEnemyBehaviour, never()).rollIntent();
    verify(deadEnemyBehaviour, never()).executeIntent(player);
    verify(firstEnemyBehaviour).executeIntent(player);
  }

  @Test
  void shouldRollLivingIntents() {
    controller.start();

    assertEquals(BattlePhase.PLAYER_TURN, controller.getCurrentPhase());
    verify(firstEnemyBehaviour).rollIntent();
    verify(secondEnemyBehaviour).rollIntent();
  }

  @Test
  void shouldEnterVictoryWhenAllEnemiesAreDead() {
    controller =
        new BattleController(
            player,
            List.of(
                createDefendingEnemy(firstEnemyBehaviour, false),
                createDefendingEnemy(secondEnemyBehaviour, false)));

    controller.start();

    assertEquals(BattlePhase.VICTORY, controller.getCurrentPhase());
  }

  @Test
  void shouldEnterDefeatWhenPlayerIsDead() {
    player = new Entity().addComponent(new CombatStatsComponent(0, 0));
    controller = new BattleController(player, enemies);

    controller.start();

    assertEquals(BattlePhase.DEFEAT, controller.getCurrentPhase());
  }

  @Test
  @Disabled("Enemy attacks do not yet damage a player CombatStatsComponent")
  void shouldDamagePlayer() throws ReflectiveOperationException {
    EnemyBehaviourComponent attackingBehaviour = new EnemyBehaviourComponent("test");
    setCurrentIntent(attackingBehaviour, EnemyIntent.attack(5));
    Entity enemy =
        new Entity().addComponent(new EnemyStatsComponent(10, 1)).addComponent(attackingBehaviour);
    controller = new BattleController(player, List.of(enemy));

    controller.start();
    completePlayerTurn();

    assertEquals(15, player.getComponent(CombatStatsComponent.class).getHealth());
  }

  @Test
  void shouldRejectStartingBattleTwice() {
    controller.start();

    assertThrows(IllegalStateException.class, controller::start);
  }

  @Test
  void shouldQueueListenerEvents() {
    AtomicReference<BattlePhase> phaseAfterListenerHandle = new AtomicReference<>();
    controller.addPhaseChangeListener(
        (previousPhase, nextPhase) -> {
          if (nextPhase == BattlePhase.PLAYER_TURN) {
            controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED);
            phaseAfterListenerHandle.set(controller.getCurrentPhase());
          }
        });

    controller.start();

    assertEquals(BattlePhase.PLAYER_TURN, phaseAfterListenerHandle.get());
    assertEquals(BattlePhase.PLAYER_ATTACK, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectEventsAfterVictory() {
    advanceToPlayerResolved();

    controller.handle(BattleEvent.ENEMIES_DEFEATED);

    assertEquals(BattlePhase.VICTORY, controller.getCurrentPhase());
    assertFalse(controller.canHandle(BattleEvent.PLAYER_TURN_STARTED));
    assertThrows(
        IllegalStateException.class, () -> controller.handle(BattleEvent.PLAYER_TURN_STARTED));
    assertEquals(BattlePhase.VICTORY, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectEventsAfterDefeat() {
    advanceToPlayerResolved();

    controller.handle(BattleEvent.PLAYER_DEFEATED);

    assertEquals(BattlePhase.DEFEAT, controller.getCurrentPhase());
    assertFalse(controller.canHandle(BattleEvent.PLAYER_TURN_STARTED));
    assertThrows(
        IllegalStateException.class, () -> controller.handle(BattleEvent.PLAYER_TURN_STARTED));
    assertEquals(BattlePhase.DEFEAT, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectNullEvent() {
    NullPointerException exception =
        assertThrows(NullPointerException.class, () -> controller.handle(null));

    assertEquals("event cannot be null", exception.getMessage());
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
  }

  @Test
  void shouldRejectInvalidTransition() {
    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class,
            () -> controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED));

    assertEquals(
        "Invalid battle transition: SETUP-->PLAYER_ATTACK_SELECTED", exception.getMessage());
    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
  }

  private void advanceToPlayerTurn() {
    controller.handle(BattleEvent.SETUP_COMPLETE);
  }

  private void advanceToPlayerResolved() {
    advanceToPlayerTurn();
    controller.handle(BattleEvent.PLAYER_ATTACK_SELECTED);
    controller.handle(BattleEvent.PLAYER_ACTION_RESOLVED);
  }

  private void advanceToEnemyTurn() {
    advanceToPlayerTurn();
    completePlayerTurn();
  }

  private void completePlayerTurn() {
    controller.handle(BattleEvent.PLAYER_END_REQUESTED);
    controller.handle(BattleEvent.PLAYER_TURN_ENDED);
  }

  private Entity createLivingDefendingEnemy(EnemyBehaviourComponent behaviour) {
    return createDefendingEnemy(behaviour, true);
  }

  private Entity createDefendingEnemy(EnemyBehaviourComponent behaviour, boolean alive) {
    Entity enemy = mock(Entity.class);
    EnemyStatsComponent stats = mock(EnemyStatsComponent.class);
    when(enemy.getComponent(EnemyBehaviourComponent.class)).thenReturn(behaviour);
    when(enemy.getComponent(EnemyStatsComponent.class)).thenReturn(stats);
    when(behaviour.rollIntent()).thenReturn(EnemyIntent.defend(1));
    when(stats.isAlive()).thenReturn(alive);
    return enemy;
  }

  private void setCurrentIntent(EnemyBehaviourComponent behaviour, EnemyIntent intent)
      throws ReflectiveOperationException {
    Field currentIntent = EnemyBehaviourComponent.class.getDeclaredField("currentIntent");
    currentIntent.setAccessible(true);
    currentIntent.set(behaviour, intent);
  }

  @Test
  void shouldEnterEnemyAttackForAttackIntent() {
    EnemyBehaviourComponent attackingBehaviour = mock(EnemyBehaviourComponent.class);
    Entity attackingEnemy = mock(Entity.class);
    EnemyStatsComponent stats = mock(EnemyStatsComponent.class);

    EnemyIntent attackIntent = EnemyIntent.attack(5);

    when(attackingEnemy.getComponent(EnemyBehaviourComponent.class))
            .thenReturn(attackingBehaviour);
    when(attackingEnemy.getComponent(EnemyStatsComponent.class))
            .thenReturn(stats);
    when(stats.isAlive()).thenReturn(true);
    when(attackingBehaviour.rollIntent()).thenReturn(attackIntent);
    when(attackingBehaviour.getCurrentIntent()).thenReturn(attackIntent);

    controller = new BattleController(player, List.of(attackingEnemy));

    AtomicReference<BattlePhase> attackPhase = new AtomicReference<>();

    controller.addPhaseChangeListener(
            (previousPhase, nextPhase) -> {
              if (nextPhase == BattlePhase.ENEMY_ATTACK) {
                attackPhase.set(nextPhase);
              }
            });

    controller.start();
    completePlayerTurn();

    assertEquals(BattlePhase.ENEMY_ATTACK, attackPhase.get());
  }

  @Test
  void shouldExecuteAttackIntent() {
    EnemyBehaviourComponent attackingBehaviour = mock(EnemyBehaviourComponent.class);
    Entity attackingEnemy = mock(Entity.class);
    EnemyStatsComponent stats = mock(EnemyStatsComponent.class);

    EnemyIntent attackIntent = EnemyIntent.attack(5);

    when(attackingEnemy.getComponent(EnemyBehaviourComponent.class))
            .thenReturn(attackingBehaviour);
    when(attackingEnemy.getComponent(EnemyStatsComponent.class))
            .thenReturn(stats);
    when(stats.isAlive()).thenReturn(true);
    when(attackingBehaviour.rollIntent()).thenReturn(attackIntent);
    when(attackingBehaviour.getCurrentIntent()).thenReturn(attackIntent);

    controller = new BattleController(player, List.of(attackingEnemy));

    controller.start();
    completePlayerTurn();

    verify(attackingBehaviour).executeIntent(player);
  }

  @Test
  void shouldEnterEnemyOtherForOtherIntent() {
    EnemyBehaviourComponent behaviour = mock(EnemyBehaviourComponent.class);
    Entity enemy = mock(Entity.class);
    EnemyStatsComponent stats = mock(EnemyStatsComponent.class);

    EnemyIntent otherIntent = EnemyIntent.unknown();

    when(enemy.getComponent(EnemyBehaviourComponent.class)).thenReturn(behaviour);
    when(enemy.getComponent(EnemyStatsComponent.class)).thenReturn(stats);
    when(stats.isAlive()).thenReturn(true);
    when(behaviour.rollIntent()).thenReturn(otherIntent);
    when(behaviour.getCurrentIntent()).thenReturn(otherIntent);

    controller = new BattleController(player, List.of(enemy));

    AtomicReference<BattlePhase> otherPhase = new AtomicReference<>();

    controller.addPhaseChangeListener(
            (previousPhase, nextPhase) -> {
              if (nextPhase == BattlePhase.ENEMY_OTHER) {
                otherPhase.set(nextPhase);
              }
            });

    controller.start();
    completePlayerTurn();

    assertEquals(BattlePhase.ENEMY_OTHER, otherPhase.get());
  }

  @Test
  void shouldMoveToNextEnemyAfterFirstEnemyResolves() {
    AtomicReference<Integer> enemyIndexWhenSecondEnemyStarts = new AtomicReference<>();

    controller.addPhaseChangeListener(
            (previousPhase, nextPhase) -> {
              if (nextPhase == BattlePhase.ENEMY_TURN
                      && controller.getCurrentEnemyIndex() == 1) {
                enemyIndexWhenSecondEnemyStarts.set(controller.getCurrentEnemyIndex());
              }
            });

    controller.start();
    completePlayerTurn();

    assertEquals(1, enemyIndexWhenSecondEnemyStarts.get());
  }
}
