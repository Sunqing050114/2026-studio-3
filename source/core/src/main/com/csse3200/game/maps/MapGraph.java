package com.csse3200.game.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Represents the map graph containing all map nodes. */
public class MapGraph implements EncounterCallback {

  private final Map<Integer, MapNode> nodes;
  private MapNode currentNode;

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

  /** Connects two nodes. */
  public void connectNodes(Integer firstId, Integer secondId) {

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
