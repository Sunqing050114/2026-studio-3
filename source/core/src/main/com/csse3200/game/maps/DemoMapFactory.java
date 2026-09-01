package com.csse3200.game.maps;

import java.util.ArrayList;
import java.util.List;

/**
 * Builds a small connected map to develop against until the real branch generation (#15) is done.
 * The node pool gives us typed nodes but no connections, so this lays them out in fixed layers and
 * joins each layer to the next. Delete this once graph generation lands.
 */
public final class DemoMapFactory {

  private static final int[] LAYER_SIZES = {1, 2, 2, 1};

  private DemoMapFactory() {}

  /**
   * Builds a connected map running from the start node through to the final node.
   *
   * @param seed seed for repeatable room types, or null for random
   * @return the generated map
   */
  public static MapGraph create(Long seed) {
    int normalNodeCount = 0;
    for (int size : LAYER_SIZES) {
      normalNodeCount += size;
    }

    RoomDistributionConfig config = new RoomDistributionConfig(normalNodeCount, 6, 3, 1, seed);
    MapGraph graph = new MapGraph(NodePoolGenerator.generate(config));

    List<List<Integer>> layers = getLayers();

    // Not using connectNodes, which links both ways. The player should only move forwards, so each
    // node only knows about the layer ahead of it.
    for (int layer = 0; layer < layers.size() - 1; layer++) {
      for (Integer fromId : layers.get(layer)) {
        for (Integer toId : layers.get(layer + 1)) {
          graph.getNode(fromId).addConnection(graph.getNode(toId));
        }
      }
    }

    return graph;
  }

  /**
   * Node ids grouped into layers, start layer first. The map screen uses this to position nodes.
   *
   * @return node ids by layer
   */
  public static List<List<Integer>> getLayers() {
    List<List<Integer>> layers = new ArrayList<>();
    int nextId = 0;

    for (int size : LAYER_SIZES) {
      List<Integer> layer = new ArrayList<>();
      for (int index = 0; index < size; index++) {
        layer.add(nextId);
        nextId++;
      }
      layers.add(layer);
    }

    // The generator always gives the final node the id straight after the normal ones.
    layers.add(List.of(nextId));
    return layers;
  }

  public static Integer getStartNodeId() {
    return 0;
  }
}
