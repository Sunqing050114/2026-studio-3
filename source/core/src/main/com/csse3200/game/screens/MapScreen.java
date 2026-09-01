package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.maps.DemoMapFactory;
import com.csse3200.game.maps.MapGraph;
import com.csse3200.game.maps.MapNode;
import com.csse3200.game.maps.MapSelectionController;
import com.csse3200.game.maps.RunState;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shows the run's map and lets the player pick where to go next. The map comes from the run state,
 * so coming back from an encounter shows the same map rather than a new one.
 *
 * <p>Layout here is plain on purpose and gets replaced by the real map rendering (#14).
 */
public class MapScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(MapScreen.class);

  private static final Color BACKGROUND = new Color(248f / 255f, 249f / 255f, 178f / 255f, 1f);
  private static final float NODE_WIDTH = 150f;
  private static final float NODE_HEIGHT = 60f;
  private static final float LAYER_SPACING = 190f;
  private static final float NODE_SPACING = 90f;
  private static final float MARGIN = 60f;

  private final GdxGame game;
  private final RunState runState;
  private final MapGraph mapGraph;
  private final MapSelectionController controller;
  private final Stage stage;
  private final Skin skin;

  public MapScreen(GdxGame game) {
    this.game = game;
    this.runState = game.getRunState();

    if (!runState.isRunActive()) {
      logger.info("Starting a new run");
      runState.startRun(DemoMapFactory.create(null), DemoMapFactory.getStartNodeId());
    }

    this.mapGraph = runState.getMapGraph();
    this.controller = new MapSelectionController(mapGraph);
    this.stage = new Stage(new ScreenViewport());
    this.skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));

    controller.getEvents().addListener("nodeSelected", (Integer nodeId) -> enterEncounter(nodeId));

    buildUi();
    Gdx.input.setInputProcessor(stage);
  }

  private void buildUi() {
    List<List<Integer>> layers = DemoMapFactory.getLayers();
    float screenHeight = Gdx.graphics.getHeight();

    Label title = new Label("Choose your next room", skin);
    title.setPosition(MARGIN, screenHeight - 50f);
    stage.addActor(title);

    TextButton menuButton = new TextButton("Main menu", skin);
    menuButton.setBounds(MARGIN, MARGIN, 140f, 45f);
    menuButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            game.setScreen(GdxGame.ScreenType.MAIN_MENU);
          }
        });
    stage.addActor(menuButton);

    for (int layer = 0; layer < layers.size(); layer++) {
      List<Integer> layerNodes = layers.get(layer);
      float x = MARGIN + (layer * LAYER_SPACING);
      float topY = (screenHeight / 2f) + ((layerNodes.size() - 1) * NODE_SPACING / 2f);

      for (int index = 0; index < layerNodes.size(); index++) {
        addNode(layerNodes.get(index), x, topY - (index * NODE_SPACING));
      }
    }
  }

  private void addNode(Integer nodeId, float x, float y) {
    MapNode node = mapGraph.getNode(nodeId);

    if (node == null) {
      return;
    }

    TextButton button = new TextButton(node.getRoomType() + "\n(" + node.getState() + ")", skin);
    button.setBounds(x, y, NODE_WIDTH, NODE_HEIGHT);
    button.setDisabled(!controller.isSelectable(nodeId));

    button.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            controller.onNodeClicked(nodeId);
          }
        });

    stage.addActor(button);
  }

  private void enterEncounter(Integer nodeId) {
    runState.enterEncounter(nodeId);
    game.setScreen(GdxGame.ScreenType.ENCOUNTER);
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(BACKGROUND);
    stage.act(delta);
    stage.draw();
  }

  @Override
  public void resize(int width, int height) {
    stage.getViewport().update(width, height, true);
  }

  @Override
  public void dispose() {
    Gdx.input.setInputProcessor(null);
    stage.dispose();
    skin.dispose();
  }
}
