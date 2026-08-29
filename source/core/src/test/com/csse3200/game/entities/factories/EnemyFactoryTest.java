package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.configs.EnemyTier;
import com.csse3200.game.extensions.GameExtension;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class EnemyFactoryTest {

  @Test
  void createAttachesStatsAndBehaviour() {
    Entity enemy = EnemyFactory.create("lesser_shade");

    assertNotNull(enemy.getComponent(EnemyStatsComponent.class));
    assertNotNull(enemy.getComponent(EnemyBehaviourComponent.class));
  }

  @Test
  void createUsesRosterStats() {
    Entity enemy = EnemyFactory.create("lesser_shade");
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

    assertEquals(24, stats.getHealth());
  }

  @Test
  void createFallsBackForUnknownId() {
    Entity enemy = EnemyFactory.create("does_not_exist");
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

    assertNotNull(stats);
    assertEquals(1, stats.getHealth()); // BaseEntityConfig default health
  }

  @Test
  void createFromConfigUsesConfigValues() {
    EnemyConfig config = new EnemyConfig();
    config.health = 50;
    config.baseAttack = 9;
    config.armour = 3;

    Entity enemy = EnemyFactory.create(config);
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

    assertEquals(50, stats.getHealth());
    assertEquals(3, stats.getArmour());
  }

  @Test
  void createWithFloorZeroKeepsBaseStats() {
    Entity enemy = EnemyFactory.create("void_knight", 0);

    assertEquals(72, enemy.getComponent(EnemyStatsComponent.class).getHealth());
  }

  @Test
  void createWithHigherFloorScalesStatsUp() {
    int baseHealth =
        EnemyFactory.create("void_knight", 0).getComponent(EnemyStatsComponent.class).getHealth();
    int scaledHealth =
        EnemyFactory.create("void_knight", 5).getComponent(EnemyStatsComponent.class).getHealth();

    assertTrue(scaledHealth > baseHealth);
  }

  @Test
  void getIdsByTierReturnsOnlyMatchingTier() {
    List<String> normals = EnemyFactory.getIdsByTier(EnemyTier.NORMAL);
    List<String> elites = EnemyFactory.getIdsByTier(EnemyTier.ELITE);

    assertTrue(normals.contains("lesser_shade"));
    assertFalse(normals.contains("void_knight"));
    assertTrue(elites.contains("void_knight"));
  }

  @Test
  void getIdsByTierReturnsEmptyListWhenNoneMatch() {
    assertTrue(EnemyFactory.getIdsByTier(EnemyTier.BOSS).isEmpty());
  }

  @Test
  void availableEnemiesIsNeverNull() {
    assertNotNull(EnemyFactory.availableEnemies());
  }
}
