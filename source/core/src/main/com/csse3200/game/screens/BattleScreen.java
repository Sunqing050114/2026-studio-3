package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.battle.*;
import com.csse3200.game.components.spritedisplay.clickable.ClickableFactory;
import com.csse3200.game.components.spritedisplay.displaying.CardDisplay;
import com.csse3200.game.components.spritedisplay.displaying.DisplayingRecord;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***scene when clicked on map and display the stag will probably take reuseable
 *stages and setting stuff up
 *
 *
 */
/// scene component for ease of building multiple stages and setting stuff up
public class BattleScreen extends ScreenAdapter {
  private final GdxGame game;
  private static final Logger logger = LoggerFactory.getLogger(BattleScreen.class);
  private final Renderer renderer;

  public BattleScreen(GdxGame game) {
    this.game = game;

    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());
    renderer = RenderFactory.createRenderer();
    createUI();
  }

  public void createUI() {
      DisplayingRecord cardLabelRecord =
              DisplayingRecord.builder("Click a card")
                      .position(1000, 1000) // Position on screen
                      .fontName("large") // Use a large font
                      .scale(1.2f)       // Scale it up
                      .variant("cardDisplay") // Use CardDisplay
                      .build();


    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity battleUi =
        new Entity()
            .addComponent(new BattleDisplay())
            .addComponent(new InputDecorator(stage, 10))
            .addComponent(new ClickableFactory(Path.of("sprites/BattleUi.json")))
            .addComponent(new BattleActions(game))
                .addComponent(new CardDisplay(cardLabelRecord));
    ;

    ServiceLocator.getEntityService().register(battleUi);
  }

  public void render(float delta) {
    ServiceLocator.getEntityService().update();
    renderer.render();
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
    logger.trace("Resized renderer: ({} x {})", width, height);
  }

  @Override
  public void dispose() {
    renderer.dispose();
    ServiceLocator.getRenderService().dispose();
    ServiceLocator.getEntityService().dispose();
    ServiceLocator.clear();
  }
}
