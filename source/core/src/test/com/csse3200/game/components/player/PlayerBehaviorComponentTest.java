package com.csse3200.game.components.player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class PlayerBehaviorComponentTest {
  @Test
  void shouldDamageTargetOnAttack() {
    Entity playerEntity = new Entity();
    Entity enemyEntity = new Entity();
    CombatStatsComponent playerStats = new CombatStatsComponent(100, 5, 100);
    CombatStatsComponent enemyStats = new CombatStatsComponent(50, 5, 100);
    PlayerBehaviorComponent behavior = new PlayerBehaviorComponent();
    playerEntity.addComponent(playerStats);
    playerEntity.addComponent(behavior);
    enemyEntity.addComponent(enemyStats);
    playerEntity.create();
    enemyEntity.create();
    behavior.attack(enemyEntity, 5);
    assertEquals(45, enemyStats.getHealth());
  }

  @Test
  void shouldAddArmorOnDefend() {
    Entity playerEntity = new Entity();
    CombatStatsComponent playerStats = new CombatStatsComponent(100, 5, 100);
    PlayerBehaviorComponent behavior = new PlayerBehaviorComponent();
    playerEntity.addComponent(playerStats);
    playerEntity.addComponent(behavior);
    playerEntity.create();
    behavior.defend(5);
    assertEquals(5, playerStats.getArmor());
  }
}
