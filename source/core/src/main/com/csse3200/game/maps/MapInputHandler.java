package com.csse3200.game.maps;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import java.util.List;

public class MapInputHandler {

  private final MapSelectionController controller;

  public MapInputHandler(MapSelectionController controller) {
    this.controller = controller;
  }

  public void attach(NodeActor actor) {
    actor.addListener(
        new InputListener() {
          @Override
          public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            controller.onNodeClicked(actor.getNodeId());
            return true;
          }
        });
  }

  public void attachAll(List<NodeActor> actors) {
    for (NodeActor actor : actors) {
      attach(actor);
    }
  }
}
