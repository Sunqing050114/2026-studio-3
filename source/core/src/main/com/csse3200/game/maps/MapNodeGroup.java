package com.csse3200.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;

public class MapNodeGroup extends Group {

  private final MapNode node;
  public float size;
  private Image nodeIcon;

  public MapNodeGroup(MapNode node) {
    this.node = node;
    float mapWidth = Gdx.graphics.getWidth();
    this.size = mapWidth / 13f;
    loadNodeAssets();
    nodeIcon =
        new Image(ServiceLocator.getResourceService().getAsset(getNodeIcon(), Texture.class));

    nodeIcon.setSize(size, size);
    checkNodeState();
    addActor(nodeIcon);
  }

  public MapNodeGroup(MapNode node, int size) {
    this.node = node;
    this.size = size;

    loadNodeAssets();
    nodeIcon =
        new Image(ServiceLocator.getResourceService().getAsset(getNodeIcon(), Texture.class));

    nodeIcon.setSize(size, size);
    checkNodeState();
    addActor(nodeIcon);
  }

  public MapNode getNode() {
    return node;
  }

  public float getNodeSize() {
    return this.size;
  }

  public void setNodeSize(float size) {
    this.size = size;
    nodeIcon.setSize(size, size);
  }

  private String getNodeIcon() {
    switch (node.getRoomType()) {
      case COMBAT:
        return "images/combat_icon.png";
      case SHOP:
        return "images/shop_icon.png";
      case EVENT:
        return "images/event_icon.png";
      case FINAL:
        return "images/final_icon.png";
      default:
        return "images/event_icon.png";
    }
  }

  private void checkNodeState() {
    float iconSize = size;
    switch (node.getState()) {
      case LOCKED:
        nodeIcon.getColor().a = 0.5f;
        break;
      case AVAILABLE:
        iconSize = 1.25f;
        break;
      case COMPLETED:
        nodeIcon.setColor(0, 255, 0, 0.5f);
        break;
      case CURRENT:
        iconSize = 1.5f;
        nodeIcon.setColor(255, 255, 0, 1);
        break;
      default:
        break;
    }
    setNodeSize(iconSize);
  }

  private void loadNodeAssets() {
    String[] nodeAssets = {
      "images/combat_icon.png",
      "images/shop_icon.png",
      "images/event_icon.png",
      "images/final_icon.png"
    };
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(nodeAssets);
    ServiceLocator.getResourceService().loadAll();
  }
}
