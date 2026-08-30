package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.ForestGameArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.components.battle.*;
import com.csse3200.game.components.spritedisplay.clickable.ClickableFactory;
import com.csse3200.game.components.spritedisplay.clickable.ClickableRecord;
import com.csse3200.game.components.spritedisplay.displaying.CardDisplay;
import com.csse3200.game.components.spritedisplay.displaying.DisplayingRecord;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.CardService;
import com.csse3200.game.services.DragNDropService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    private ForestGameArea gameArea;

    private static final String[] mainGameTextures = {"images/heart.png"};
    private static final Vector2 CAMERA_POSITION = new Vector2(7.5f, 7.5f);

    private static final int HAND_SIZE = 4;
    private static final float HAND_START_X = 50f;
    private static final float HAND_Y = 1700f;
    private static final float HAND_SPACING = 350f;

    private final PhysicsEngine physicsEngine;

    public BattleScreen(GdxGame game) {
        this.game = game;

        ServiceLocator.registerDragNDropService(new DragNDropService());

        logger.debug("Initialising main game screen services");
        ServiceLocator.registerTimeSource(new GameTime());

        PhysicsService physicsService = new PhysicsService();
        ServiceLocator.registerPhysicsService(physicsService);
        physicsEngine = physicsService.getPhysics();

        ServiceLocator.registerInputService(new InputService());
        ServiceLocator.registerResourceService(new ResourceService());

        ServiceLocator.registerEntityService(new EntityService());
        ServiceLocator.registerRenderService(new RenderService());

        ServiceLocator.registerCardService(new CardService(Path.of("configs/cards.json")));
        ServiceLocator.getCardService().startNewRound(HAND_SIZE);

        renderer = RenderFactory.createRenderer();
        renderer.getCamera().getEntity().setPosition(CAMERA_POSITION);
        renderer.getDebug().renderPhysicsWorld(physicsEngine.getWorld());

        ServiceLocator.registerCamera(renderer.getCamera().getCamera());

        loadAssets();

        logger.debug("Initialising main game screen entities");
        TerrainFactory terrainFactory = new TerrainFactory(renderer.getCamera());
        ForestGameArea forestGameArea = new ForestGameArea(terrainFactory);
        this.gameArea = forestGameArea;
        forestGameArea.create();      // spawns player, terrain, etc. — needed before UI/hitboxes reference it

        createUI();                   // now gameArea is non-null when this runs
    }

    public void createUI() {
        DisplayingRecord cardLabelRecord =
                DisplayingRecord.builder("Click a card")
                        .position(1000, 1000) // Position on screen
                        .fontName("large") // Use a large font
                        .scale(1.2f) // Scale it up
                        .variant("cardDisplay") // Use CardDisplay
                        .build();

        // sprites/BattleUi.json defines the static UI (exit/up/down); the card hand itself
        // is dealt dynamically by CardService each round, so it's merged in here rather than
        // being hardcoded in JSON.
        List<ClickableRecord> records =
                new ArrayList<>(ClickableFactory.loadRecordsFromJson(Path.of("sprites/BattleUi.json")));
        records.addAll(
                ServiceLocator.getCardService().buildHandRecords(HAND_START_X, HAND_Y, HAND_SPACING));

        Stage stage = ServiceLocator.getRenderService().getStage();
        Entity battleUi =
                new Entity()
                        .addComponent(new BattleDisplay())
                        .addComponent(new InputDecorator(stage, 10))
                        .addComponent(new ClickableFactory(records))
                        .addComponent(new BattleActions(game))
                        .addComponent(new CardDisplay(cardLabelRecord));

        gameArea.displayUI(battleUi);

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

    private void loadAssets() {
        logger.debug("Loading assets");
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(mainGameTextures);
        ServiceLocator.getResourceService().loadAll();
    }
}
