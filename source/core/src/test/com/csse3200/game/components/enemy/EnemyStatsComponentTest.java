package com.csse3200.game.components.enemy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.entities.Entity;
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
}
