package com.csse3200.game.entities.factories;

import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.configs.EnemyConfigs;
import com.csse3200.game.entities.configs.EnemyScaling;
import com.csse3200.game.entities.configs.EnemyTier;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.rendering.TextureRenderComponent;
import java.util.ArrayList;
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

  private static final String SPRITE_DIR = "images/enemies/";
  private static final String DEFAULT_SPRITE = SPRITE_DIR + "default.png";

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
    return create(resolve(id));
  }

  /**
   * Creates an enemy by id with stats scaled for the given floor.
   *
   * <p>Deeper floors produce tougher enemies through {@link EnemyScaling}. A floor of {@code 0}, or
   * a negative floor, yields the enemy's base stats.
   *
   * @param id enemy id
   * @param floor the current run floor, used as the scaling progression
   * @return the assembled enemy entity
   */
  public static Entity create(String id, int floor) {
    return create(EnemyScaling.scale(resolve(id), floor));
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
        .addComponent(new EnemyBehaviourComponent(config.behaviour))
        .addComponent(new TextureRenderComponent(spritePath(config)));
  }

  /**
   * Resolves the texture path for an enemy.
   *
   * <p>An explicit {@link EnemyConfig#sprite} wins. Otherwise the path is {@code
   * images/enemies/<id>.png} by convention, falling back to {@link #DEFAULT_SPRITE} when the id is
   * missing.
   *
   * @param config enemy configuration
   * @return an internal texture path
   */
  private static String spritePath(EnemyConfig config) {
    if (config.sprite != null && !config.sprite.isBlank()) {
      return config.sprite;
    }
    if (config.id != null && !config.id.isBlank() && !"unknown".equals(config.id)) {
      return SPRITE_DIR + config.id + ".png";
    }
    return DEFAULT_SPRITE;
  }

  /**
   * @return the ids of every enemy in the roster
   */
  public static List<String> availableEnemies() {
    return roster.ids();
  }

  /**
   * Returns the ids of every roster enemy belonging to the given tier.
   *
   * <p>Used by level design to pick enemies of a particular difficulty. Returns an empty list when
   * nothing matches or the roster failed to load.
   *
   * @param tier the tier to filter by
   * @return a new list of matching enemy ids
   */
  public static List<String> getIdsByTier(EnemyTier tier) {
    List<String> matches = new ArrayList<>();

    for (String id : roster.ids()) {
      EnemyConfig config = roster.get(id);
      if (config != null && config.tier == tier) {
        matches.add(id);
      }
    }

    return matches;
  }

  /**
   * Looks up a config by id, returning a fresh default config (and logging a warning) when the id
   * is unknown.
   *
   * @param id enemy id
   * @return the matching config, or a default {@link EnemyConfig}
   */
  private static EnemyConfig resolve(String id) {
    EnemyConfig config = roster.get(id);

    if (config == null) {
      logger.warn("Unknown enemy id: {}", id);
      return new EnemyConfig();
    }

    return config;
  }

  private EnemyFactory() {
    throw new IllegalStateException("Instantiating utility class");
  }
}
