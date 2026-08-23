package com.csse3200.game.maps;

import java.util.ArrayList;
import java.util.List;

/** Represents a single node in the map graph. */
public class MapNode {

  private final String nodeId;

  private final RoomType roomType;

  private NodeState state;

  private final List<MapNode> connections;

  /**
   * Creates a map node.
   *
   * @param nodeId unique identifier
   * @param roomType type of room
   */
  public MapNode(String nodeId, RoomType roomType) {
    this.nodeId = nodeId;
    this.roomType = roomType;

    this.state = NodeState.LOCKED;

    this.connections = new ArrayList<>();
  }

  public String getNodeId() {
    return nodeId;
  }

  public RoomType getRoomType() {
    return roomType;
  }

  public NodeState getState() {
    return state;
  }

  public void setState(NodeState state) {
    this.state = state;
  }

  public List<MapNode> getConnections() {
    return connections;
  }

  public void addConnection(MapNode node) {
    connections.add(node);
  }
}
