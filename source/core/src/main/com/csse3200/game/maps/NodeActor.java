package com.csse3200.game.maps;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class NodeActor extends Actor {

  private final Integer nodeId;

  public NodeActor(Integer nodeId, float x, float y, float width, float height) {
    this.nodeId = nodeId;
    setBounds(x, y, width, height);
  }

  public Integer getNodeId() {
    return nodeId;
  }
}
