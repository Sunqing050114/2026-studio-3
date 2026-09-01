package com.csse3200.game.components.enemy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.enemy.EnemyAI.EnemyAIFactory;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.listeners.EventListener1;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class EnemyBehaviourComponentTest {

  private static Entity enemyWith(EnemyBehaviourComponent behaviour, EnemyStatsComponent stats) {
    Entity enemy = new Entity();
    if (stats != null) {
      enemy.addComponent(stats);
    }
    enemy.addComponent(behaviour);
    enemy.create();
    return enemy;
  }

  @Test
  void shouldKeepBehaviourIdGivenToConstructor() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);

    assertEquals(EnemyAIFactory.CYCLE_ATTACK_DEFEND, behaviour.getBehaviourId());
  }

  @Test
  void shouldStartWithUnknownIntent() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);

    assertEquals(IntentType.UNKNOWN, behaviour.getCurrentIntent().getType());
  }

  @Test
  void shouldAttackOnTheFirstTurn() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));

    EnemyIntent intent = behaviour.rollIntent();

    assertEquals(IntentType.ATTACK, intent.getType());
    assertEquals(6, intent.getValue());
  }

  @Test
  void shouldDefendOnTheSecondTurn() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));

    behaviour.rollIntent();
    EnemyIntent intent = behaviour.rollIntent();

    assertEquals(IntentType.DEFEND, intent.getType());
  }

  @Test
  void shouldExposeTheRolledIntentAsCurrent() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));

    EnemyIntent rolled = behaviour.rollIntent();

    assertSame(rolled, behaviour.getCurrentIntent());
  }

  @Test
  void shouldTelegraphTheIntentToListeners() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    Entity enemy = enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));
    EventListener1<EnemyIntent> listener = mock(EventListener1.class);
    enemy.getEvents().addListener("intentChanged", listener);

    behaviour.rollIntent();

    verify(listener).handle(any(EnemyIntent.class));
  }

  @Test
  void shouldRollUnknownWithoutStatsComponent() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    enemyWith(behaviour, null);

    EnemyIntent intent = behaviour.rollIntent();

    assertEquals(IntentType.UNKNOWN, intent.getType());
  }

  @Test
  void shouldFallBackToADefaultBehaviourForUnknownId() {
    EnemyBehaviourComponent behaviour = new EnemyBehaviourComponent("no_such_behaviour");
    enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));

    EnemyIntent intent = behaviour.rollIntent();

    assertNotNull(intent);
    assertEquals(IntentType.ATTACK, intent.getType());
  }

  @Test
  void shouldGainArmorWhenResolvingDefend() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 0);
    enemyWith(behaviour, stats);

    behaviour.rollIntent();
    behaviour.rollIntent();
    behaviour.executeIntent(null);

    assertEquals(2, stats.getArmor());
  }

  @Test
  void shouldDamageTheTargetWhenResolvingAttack() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));

    Entity player = new Entity();
    CombatStatsComponent playerStats = new CombatStatsComponent(30, 4);
    player.addComponent(playerStats);
    player.create();

    behaviour.rollIntent();
    behaviour.executeIntent(player);

    assertEquals(24, playerStats.getHealth());
  }

  @Test
  void shouldIgnoreAttackAgainstNullTarget() {
    EnemyBehaviourComponent behaviour =
        new EnemyBehaviourComponent(EnemyAIFactory.CYCLE_ATTACK_DEFEND);
    enemyWith(behaviour, new EnemyStatsComponent(20, 6, 0));

    behaviour.rollIntent();

    behaviour.executeIntent(null);
  }
}
