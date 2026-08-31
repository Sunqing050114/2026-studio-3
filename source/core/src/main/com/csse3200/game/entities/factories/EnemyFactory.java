package com.csse3200.game.entities.factories;

import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.components.spritedisplay.reactive.EnemyDropTargetComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.configs.EnemyConfigs;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates enemy entities from configuration loaded from {@code configs/enemies.json}.
 *
 * <p>Unknown enemy ids fall back to a default enemy configuration.
 */
public class EnemyFactory {
  private static final Logger logger = LoggerFactory.getLogger(EnemyFactory.class);
  private static final EnemyConfigs roster = loadRoster();

  private static EnemyConfigs loadRoster() {
    EnemyConfigs configs = FileLoader.readClass(EnemyConfigs.class, "configs/enemies.json");

    if (configs == null) {
      logger.warn("Failed to load enemy configs, using empty roster");
      return new EnemyConfigs();
    }

    return configs;
  }

  /**
   * Creates an enemy by id, falling back to a default enemy when the id is unknown.
   *
   * @param id enemy id
   * @return the assembled enemy entity
   */
  public static Entity create(String id) {
    EnemyConfig config = roster.get(id);

    if (config == null) {
      logger.warn("Unknown enemy id: {}", id);
      config = new EnemyConfig();
    }

    return create(config);
  }

  /**
   * Creates an enemy from the given configuration.
   *
   * @param config enemy configuration
   * @return the assembled enemy entity
   */
  public static Entity create(EnemyConfig config) {
    return new Entity()
        .addComponent(new TextureRenderComponent("images/heart.png")) //place holder image
        .addComponent(new EnemyStatsComponent(config.health, config.baseAttack, config.armour))
        .addComponent(new EnemyBehaviourComponent(config.behaviour))
        .addComponent(
            new EnemyDropTargetComponent(
                ServiceLocator.getDragAndDropService().getDragAndDrop(),
                ServiceLocator.getCamera(), config.id)); //allow the user to drag a card on it
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
