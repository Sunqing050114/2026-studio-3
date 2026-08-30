package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.csse3200.game.GdxGame;
import com.csse3200.game.areas.ForestGameArea;
import com.csse3200.game.areas.terrain.TerrainFactory;
import com.csse3200.game.cards.CardConfigLoader;
import com.csse3200.game.cards.CardLibrary;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.configs.EffectConfig;
import com.csse3200.game.components.battle.*;
import com.csse3200.game.components.combat.BattleController;
import com.csse3200.game.components.spritedisplay.clickable.ClickableFactory;
import com.csse3200.game.components.spritedisplay.clickable.ClickableRecord;
import com.csse3200.game.components.spritedisplay.displaying.CardDisplay;
import com.csse3200.game.components.spritedisplay.displaying.DisplayingRecord;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.factories.EnemyFactory;
import com.csse3200.game.entities.factories.RenderFactory;
import com.csse3200.game.input.InputDecorator;
import com.csse3200.game.input.InputService;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.Renderer;
import com.csse3200.game.services.DragNDropService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.HashMap;
import java.util.Map;

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
    private static final Map<String, Skin> textureSkinCache = new HashMap<>();
    private final BattleController controller;

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

        Entity player = new Entity();
        List<Entity> enemies = List.of(EnemyFactory.create(new EnemyConfig()));
        controller = new BattleController(player, enemies);


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

        List<CardConfig> configs = CardConfigLoader.loadCards(); // reads configs/cards.json
        CardLibrary library = new CardLibrary(configs);
        ServiceLocator.registerCardLibrary(library);

        List<CardConfig> allCards = library.getAllCards();
        List<ClickableRecord> records = new ArrayList<>();
        float x = HAND_START_X;
        for (CardConfig card : allCards) {
            Skin cardSkin = skinFromTexturePath(card.texturePath);
            records.add(
                    ClickableRecord.builder(card.id)
                            .label(card.name)
                            .variant("drag")
                            .position(x, HAND_Y)
                            .size(300, 456)
                            .skin(cardSkin)   // no .text(...) => Builder infers ButtonType.IMAGE
                            .build());
            x += HAND_SPACING;
        }

        records.addAll(new ArrayList<>(ClickableFactory.loadRecordsFromJson(Path.of("sprites/BattleUi.json"))));



        Stage stage = ServiceLocator.getRenderService().getStage();
        Entity battleUi =
                new Entity()
                        .addComponent(new InputDecorator(stage, 10))
                        .addComponent(new ClickableFactory(records))
                        .addComponent(new CardDisplay(cardLabelRecord))
                        .addComponent(new BattleActions(controller, game));

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


    private Skin skinFromTexturePath(String texturePath) {
        return textureSkinCache.computeIfAbsent(texturePath, path -> {
            Texture texture = new Texture(Gdx.files.internal(path));
            TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = drawable;

            Skin skin = new Skin();
            skin.add("default", style, ImageButton.ImageButtonStyle.class);
            return skin;
        });
    }
}
