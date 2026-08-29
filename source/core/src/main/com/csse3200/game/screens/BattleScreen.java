package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.battle.BattleActions;
import com.csse3200.game.components.battle.BattleArea;
import com.csse3200.game.components.battle.BattleDisplay;
import com.csse3200.game.components.combat.BattleController;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Displays the battle scene and coordinates its rendering and UI services. */
public class BattleScreen extends ScreenAdapter {
  private static final Logger logger = LoggerFactory.getLogger(BattleScreen.class);
  private final Renderer renderer;
  private final BattleController controller = new BattleController();

  public BattleScreen(GdxGame game) {
    ServiceLocator.registerInputService(new InputService());
    ServiceLocator.registerResourceService(new ResourceService());
    ServiceLocator.registerEntityService(new EntityService());
    ServiceLocator.registerRenderService(new RenderService());
    ServiceLocator.registerTimeSource(new GameTime());
    renderer = RenderFactory.createRenderer();
    createUI();
  }

  private void createUI() {
    Stage stage = ServiceLocator.getRenderService().getStage();
    Entity battleUi =
        new Entity()
            .addComponent(new BattleDisplay())
            .addComponent(new BattleActions(controller))
            .addComponent(new BattleArea())
            .addComponent(new InputDecorator(stage, 10));
    ServiceLocator.getEntityService().register(battleUi);
  }

  @Override
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
