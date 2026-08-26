package com.csse3200.game.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Generates a seeded pool of typed map nodes without creating graph connections. */
public final class NodePoolGenerator {

  private NodePoolGenerator() {}

  /**
   * Generates the configured normal nodes followed by exactly one final node.
   *
   * @param config room distribution configuration
   * @return immutable node pool with unique, sequential IDs
   * @throws NullPointerException if the configuration is null
   */
  public static List<MapNode> generate(RoomDistributionConfig config) {
    if (config == null) {
      throw new NullPointerException("Config cannot be null!");
    }
    Random random = new Random(config.getSeed());
    List<MapNode> nodes = new ArrayList<>();
    int nodeCount = config.getNormalNodeCount();

    for (int index = 0; index < nodeCount; index++) {
      nodes.add(new MapNode(index, selectRoomType(config, random)));
    }

    nodes.add(new MapNode((nodeCount + 1), RoomType.FINAL)); // Boss
    return List.copyOf(nodes);
  }

  /** Randomly selects a room type using the configured weights. */
  private static RoomType selectRoomType(RoomDistributionConfig config, Random random) {
    int totalWeight = config.getCombatWeight() + config.getEventWeight() + config.getShopWeight();
    int selection = random.nextInt(totalWeight);

    if (selection < config.getCombatWeight()) {
      return RoomType.COMBAT;
    }

    selection -= config.getCombatWeight();
    if (selection < config.getEventWeight()) {
      return RoomType.EVENT;
    }

    return RoomType.SHOP;
  }
}
