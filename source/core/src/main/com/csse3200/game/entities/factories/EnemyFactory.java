package com.csse3200.game.entities.factories;

import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.configs.EnemyConfigs;
import java.util.List;

/**
 * Creates enemy entities from configuration.
 *
 * <p>Placeholder implementation: the roster is empty and unknown ids fall back to a default enemy.
 * Loading from {@code configs/enemies.json} is added in #18.
 */
public class EnemyFactory {
  private static final EnemyConfigs roster = new EnemyConfigs();

  /**
   * Creates an enemy by id, falling back to a default enemy when the id is unknown.
   *
   * @param id enemy id
   * @return the assembled enemy entity
   */
  public static Entity create(String id) {
    EnemyConfig config = roster.get(id);
    return create(config == null ? new EnemyConfig() : config);
  }

  /**
   * Creates an enemy from the given configuration.
   *
   * @param config enemy configuration
   * @return the assembled enemy entity
   */
  public static Entity create(EnemyConfig config) {
    return new Entity()
        .addComponent(new EnemyStatsComponent(config.health, config.baseAttack, config.armour))
        .addComponent(new EnemyBehaviourComponent(config.behaviour));
  }

  /**
   * @return the ids of every enemy in the roster
   */
  public static List<String> availableEnemies() {
    return roster.ids();
  }

  private EnemyFactory() {
    throw new IllegalStateException("Instantiating utility class");
  }
}
