package com.csse3200.game.maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Represents a single node in the map graph. */
public class MapNode {

  private Integer nodeId;

  /** Refers to the layer of the map the node is placed */
  private Integer height;

  private final RoomType roomType;

  private NodeState state;

  private final List<MapNode> connections;

  /**
   * Creates a map node.
   *
   * @param nodeId unique identifier
   * @param roomType type of room
   */
  public MapNode(Integer nodeId, RoomType roomType) {
    this.nodeId = nodeId;
    this.roomType = roomType;

    this.state = NodeState.LOCKED;
    Random random = new Random();
    this.height = random.nextInt(10);
    this.connections = new ArrayList<>();
  }

  public Integer getNodeId() {
    return nodeId;
  }

  public Integer getHeight() {
    return height;
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
