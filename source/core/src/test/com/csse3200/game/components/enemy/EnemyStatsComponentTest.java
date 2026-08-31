package com.csse3200.game.components.enemy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.listeners.EventListener0;
import com.csse3200.game.events.listeners.EventListener1;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class EnemyStatsComponentTest {
  @Test
  void shouldApplyDamageToArmourBeforeHealth() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    stats.takeDamage(8);

    assertEquals(17, stats.getHealth());
    assertEquals(0, stats.getArmour());
  }

  @Test
  void shouldLetArmourFullyAbsorbDamage() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    stats.takeDamage(3);

    assertEquals(20, stats.getHealth());
    assertEquals(2, stats.getArmour());
  }

  @Test
  void shouldDamageHealthDirectlyWhenNoArmour() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 0);
    enemy.addComponent(stats);
    enemy.create();

    stats.takeDamage(6);

    assertEquals(14, stats.getHealth());
    assertEquals(0, stats.getArmour());
  }

  @Test
  void shouldIgnoreZeroDamage() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    stats.takeDamage(0);

    assertEquals(20, stats.getHealth());
    assertEquals(5, stats.getArmour());
  }

  @Test
  void shouldIgnoreNegativeDamage() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    stats.takeDamage(-5);

    assertEquals(20, stats.getHealth());
    assertEquals(5, stats.getArmour());
  }

  @Test
  void shouldNotReduceHealthBelowZero() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 0);
    enemy.addComponent(stats);
    enemy.create();

    stats.takeDamage(999);

    assertEquals(0, stats.getHealth());
  }

  @Test
  void shouldTriggerEnemyDefeatedWhenHealthReachesZero() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 0);
    enemy.addComponent(stats);
    enemy.create();

    EventListener0 listener = mock(EventListener0.class);
    enemy.getEvents().addListener("enemyDefeated", listener);

    stats.takeDamage(20);

    verify(listener).handle();
  }

  @Test
  void shouldTriggerEnemyDamagedWithActualHealthDamage() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    @SuppressWarnings("unchecked")
    EventListener1<Integer> listener = (EventListener1<Integer>) mock(EventListener1.class);
    enemy.getEvents().addListener("enemyDamaged", listener);

    stats.takeDamage(8);

    verify(listener).handle(3);
    assertEquals(17, stats.getHealth());
    assertEquals(0, stats.getArmour());
  }

  @Test
  void shouldClampSetArmourToZero() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    stats.setArmour(-10);

    assertEquals(0, stats.getArmour());
  }

  @Test
  void shouldClampAddArmourToZero() {
    Entity enemy = new Entity();
    EnemyStatsComponent stats = new EnemyStatsComponent(20, 6, 5);
    enemy.addComponent(stats);
    enemy.create();

    stats.addArmour(-10);

    assertEquals(0, stats.getArmour());
  }
}
