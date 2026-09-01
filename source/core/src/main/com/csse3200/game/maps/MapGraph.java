package com.csse3200.game.maps;

import java.util.HashMap;
import java.util.Map;

/** Represents the map graph containing all map nodes. */
public class MapGraph implements EncounterCallback {

  private final Map<String, MapNode> nodes;

  public MapGraph() {
    nodes = new HashMap<>();
  }

  /**
   * Adds a node to the graph.
   *
   * @param node node to add
   */
  public void addNode(MapNode node) {
    nodes.put(node.getNodeId(), node);
  }

  /**
   * Gets a node by its id.
   *
   * @param nodeId node identifier
   * @return matching node
   */
  public MapNode getNode(String nodeId) {
    return nodes.get(nodeId);
  }

  /**
   * Gets all nodes in the graph.
   *
   * @return all map nodes
   */
  public Map<String, MapNode> getNodes() {
    return nodes;
  }

  /** Connects two nodes. */
  public void connectNodes(String firstId, String secondId) {

    MapNode first = nodes.get(firstId);
    MapNode second = nodes.get(secondId);

    if (first != null && second != null) {
      first.addConnection(second);
      second.addConnection(first);
    }
  }

  /**
   * Called after an encounter finishes.
   *
   * @param nodeId completed node id
   * @param success whether encounter completed successfully
   */
  public void completeNode(String nodeId, boolean success) {

    MapNode node = nodes.get(nodeId);

    if (node == null) {
      return;
    }

    if (success) {

      node.setState(NodeState.COMPLETED);

      for (MapNode connected : node.getConnections()) {
        if (connected.getState() == NodeState.LOCKED) {
          connected.setState(NodeState.AVAILABLE);
        }
      }
    }
  }

  @Override
  public void onEncounterComplete(String nodeId, boolean success) {
    completeNode(nodeId, success);
  }
}
