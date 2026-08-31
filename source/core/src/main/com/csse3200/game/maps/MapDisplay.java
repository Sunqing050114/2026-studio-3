package com.csse3200.game.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


/**
 * Displays the procedural map for the game
 * 
 */
public class MapDisplay extends UIComponent {

  private final MapGraph mapGraph;
  
  private Group group;
  private ScrollPane scrollPane;
  private final float layerHeight = 128f;
  private final float borderPadding = 256f;
  private final float mapWidth = Gdx.graphics.getWidth();
  private final float mapHeight = Gdx.graphics.getHeight() * 2;
  // to store positions
  private final Map<Integer, Vector2> nodePositions = new HashMap<>();

  public MapDisplay(MapGraph mapGraph) {
    this.mapGraph = mapGraph;
  }

   private void addBackground(){
    ResourceService resourceService = ServiceLocator.getResourceService();
    resourceService.loadTextures(new String[] {"images/map_background.png"});
    ServiceLocator.getResourceService().loadAll();
    Image background =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/map_background.png",
                 Texture.class));
    background.setSize(group.getWidth(), group.getHeight());
    background.setPosition(0, 0);
    group.addActor(background);
  }

  @Override
public void create() {
    super.create();

    group = new Group();
    group.setSize(mapWidth, mapHeight);

    addBackground();
    addNodes();
    addConnections();

    scrollPane = new ScrollPane(group);
    scrollPane.setActor(group);
    scrollPane.setFillParent(true);
    scrollPane.setScrollingDisabled(true, false);
    scrollPane.setOverscroll(false, false);
    
    stage.addActor(scrollPane);

    scrollPane.layout();
    scrollPane.setScrollPercentY(1f);
}

  private void addNodes() {
    for (MapNode node: mapGraph.getNodes().values()) {
      MapNodeGroup nodeGroup = new MapNodeGroup(node);
      float nodeWidth = nodeGroup.getNodeSize();

      float x = getNodeX(node.getNodeId(), nodeWidth);
      float y = getNodeY(node);

      nodePositions.put(node.getNodeId(), new Vector2(x, y).scl(2f / nodeWidth));

      nodeGroup.setPosition(x, y);
      group.addActor(nodeGroup);

    }
  }

  private float getNodeX(int nodeId, float nodeWidth) {
    return (nodeId & 7) * nodeWidth + getCentrePadding(nodeWidth*8);
  }

  private float getNodeY(MapNode node) {

    return node.getHeight() * layerHeight + borderPadding;
  }

  private float getCentrePadding(float width) {
    return mapWidth / 2f - width / 2f;
  }

  private void addConnections() {
    for (MapNode node : mapGraph.getNodes().values()) {
      for (MapNode connection : node.getConnections()) {

        if (node.getNodeId() >= connection.getNodeId()) {
          continue;
        }

        Vector2 start = nodePositions.get(node.getNodeId());
        Vector2 end = nodePositions.get(connection.getNodeId());
        MapConnectionGroup mapConnectionGroup = new MapConnectionGroup(start,
           end);

        group.addActor(mapConnectionGroup);
        
      }
      
    }
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    super.dispose();
    scrollPane.remove();
  }
}
