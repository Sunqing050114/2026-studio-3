package com.csse3200.game.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/** Represents the map graph containing all map nodes. */
public class MapGraph implements EncounterCallback {

  private final Map<Integer, MapNode> nodes;
  private MapNode currentNode;

  public static final int MAP_WIDTH = 7;
  public static final int MAP_HEIGHT = 15;
  public static final int MAX_NODE_COUNT = MAP_WIDTH * MAP_HEIGHT;

  public MapGraph() {
    nodes = new HashMap<>();
  }

  /**
   * Creates a graph containing an existing node pool.
   *
   * @param nodes nodes keyed by their unique identifiers
   */
  public MapGraph(Map<Integer, MapNode> nodes) {
    this.nodes = new HashMap<>(nodes);
  }

  /**
   * Primary map generation function. The player is able to start from any of the
   * nodes at height =
   * 1.
   * Should be private and only be called once in constructor. Currently public
   * for testing.
   * TODO: currently just does a weird bfs with a lot of things randomly shaved
   * needs more work
   * and refining branches but they are there and should have some variation
   *
   * @param void
   */
  public void generatePathing() {

    MapNode top = nodes.get(MAX_NODE_COUNT);
    List<MapNode> prevRow, nextRow;
    Random rand = new Random();

    // set connections to boss/final first
    int pathCount = rand.nextInt(1, 3);

    nextRow = getNodesByHeight(MAP_HEIGHT - 1);

    for (int j = 0; j < pathCount; j++) {

      connectNodes(top, nextRow.get(rand.nextInt(0, MAP_WIDTH)));
    }
    pruneUnconnectedNodes(nextRow);
    prevRow = nextRow;

    // begin to loop the bulk of connections down the tree
    for (int i = 2; i < MAP_HEIGHT; i++) {

      nextRow = getNodesByHeight(MAP_HEIGHT - i);

      pathCount = rand.nextInt(3, 5);
      pruneRandomNodes(nextRow, pathCount);

      for (MapNode parentNode : prevRow) {
        int minDistance = MAP_WIDTH;
        for (MapNode childNode : nextRow) {
          int distance = Math.abs(parentNode.getNodeId() % MAP_WIDTH) - (childNode.getNodeId() % MAP_WIDTH);
          if (distance < minDistance) {
            minDistance = distance;
            if (rand.nextGaussian() < 0.3) {
              if (parentNode.getConnections().size() < 3) {
                connectNodes(parentNode, childNode);
              }
            } else  {
              if (parentNode.getConnections().size() < 2) {
                connectNodes(parentNode, childNode);
              }
            }
          }
        }
      }
      pruneUnconnectedNodes(nextRow);
      prevRow = nextRow;
    }

    pruneUnconnectedMapNodes();
  }

  /**
   * Removes all unconnected nodes from the MapGraph. Only called as the final
   * step of generation.
   *
   * @param void
   */
  private void pruneUnconnectedMapNodes() {
    nodes.values().removeIf(node -> node.getConnections().isEmpty());
  }

  /**
   * Removes random nodes from a list. Does not remove from MapGraph state.
   *
   * @param nodelist list of nodes to be pruned
   */
  private void pruneRandomNodes(List<MapNode> nodelist, int count) {

    Random rand = new Random();

    for (int i = 0; i < count; i++) {
      nodelist.remove(rand.nextInt(0, nodelist.size() - 1));
    }
  }

  /**
   * Removes all nodes in a list with no connections. Does not remove from
   * MapGraph state.
   *
   * @param nodelist list of nodes to be pruned
   */
  private void pruneUnconnectedNodes(List<MapNode> nodelist) {

    nodelist.removeIf(node -> node.getConnections().isEmpty());
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
   * Adds a list of nodes to the graph. TO BE DELETED, JUST FOR TESTING
   *
   * @param nodeList nodes to add to the graph
   */
  public void addNodes(Map<Integer, MapNode> nodeList) {
    for (MapNode node : nodeList.values()) {
      addNode(node);
    }
  }

  /**
   * Gets a node by its id.
   *
   * @param nodeId node identifier
   * @return matching node
   */
  public MapNode getNode(Integer nodeId) {
    return nodes.get(nodeId);
  }

  public MapNode getNodeByCoords(int x, int y) {

    return nodes.get((y * MAP_WIDTH + x) - 1);
  }

  /**
   * Gets the current node.
   *
   * @return current node or null if no current node is set
   */
  public MapNode getCurrentNode() {
    return currentNode;
  }

  /**
   * Gets all nodes in the graph.
   *
   * @return all map nodes
   */
  public Map<Integer, MapNode> getNodes() {
    return nodes;
  }

  public List<MapNode> getNodesByHeight(int height) {

    List<MapNode> result = new ArrayList<>();
    for (MapNode node : nodes.values()) {
      if (node.getHeight() == height) {
        result.add(node);
      }
    }
    return result;
  }

  /** Connects two nodes. */
  public void connectNodes(MapNode first, MapNode second) {

    if (first != null && second != null) {
      first.addConnection(second);
      second.addConnection(first);
    }
  }

  /**
   * Called after an encounter finishes.
   *
   * @param nodeId  completed node id
   * @param success whether encounter completed successfully
   */
  public void completeNode(Integer nodeId, boolean success) {

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

  /**
   * Get all nodes with the specified state
   *
   * @param state state to match
   * @return nodes with the specified state
   */
  public List<MapNode> getNodesByState(NodeState state) {
    List<MapNode> result = new ArrayList<>();

    for (MapNode node : nodes.values()) {
      if (node.getState() == state) {
        result.add(node);
      }
    }

    return result;
  }

  /**
   * Checks if a move is valid and updates currentNode accordingly.
   *
   * @param nodeId id of the node to move to
   * @return true if the move is valid, false otherwise
   */
  public boolean moveToNode(Integer nodeId) {
    MapNode targetNode = nodes.get(nodeId);

    // targetNode must be connected to currentNode and must be available
    // TODO: Handle case where currentNode is null
    if (targetNode == null
        || targetNode.getState() != NodeState.AVAILABLE
        || !currentNode.getConnections().contains(targetNode)) {
      return false;
    }

    // state of previous currentNode should be updated when its encounter completes.
    currentNode = targetNode;
    targetNode.setState(NodeState.CURRENT);

    return true;
  }

  @Override
  public void onEncounterComplete(Integer nodeId, boolean success) {
    completeNode(nodeId, success);
  }
}
