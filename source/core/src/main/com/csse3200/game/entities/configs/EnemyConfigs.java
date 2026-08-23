package com.csse3200.game.entities.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Roster of all enemies, loaded from {@code configs/enemies.json}. */
public class EnemyConfigs {
  public EnemyConfig[] enemies = new EnemyConfig[0];
  private transient Map<String, EnemyConfig> index;

  public EnemyConfig get(String id) {
    return index.get(id);
  }

  public boolean contains(String id) {
    return index.containsKey(id);
  }

  public List<String> ids() {
    return new ArrayList<>(index.keySet());
  }

  private Map<String, EnemyConfig> index() {
    if (index == null) {
      index = new HashMap<>();
      for (EnemyConfig config : enemies) {
        index.put(config.id, config);
      }
    }
    return index;
  }
}
