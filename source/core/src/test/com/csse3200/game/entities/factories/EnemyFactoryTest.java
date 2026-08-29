package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.configs.EnemyTier;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;

@ExtendWith(GameExtension.class)
class EnemyFactoryTest {

  private ResourceService resourceService;

  @BeforeEach
  void registerServices() {
    // Headless tests have no real assets. Hand the factory a mocked ResourceService that returns a
    // dummy texture for any request, so TextureRenderComponent can be constructed without a GPU.
    resourceService = mock(ResourceService.class);
    when(resourceService.getAsset(anyString(), eq(Texture.class))).thenReturn(mock(Texture.class));
    ServiceLocator.registerResourceService(resourceService);

    RenderService renderService = new RenderService();
    renderService.setDebug(mock(DebugRenderer.class));
    ServiceLocator.registerRenderService(renderService);
  }

  @Test
  void createAttachesStatsAndBehaviour() {
    Entity enemy = EnemyFactory.create("lesser_shade");

    assertNotNull(enemy.getComponent(EnemyStatsComponent.class));
    assertNotNull(enemy.getComponent(EnemyBehaviourComponent.class));
  }

  @Test
  void createAttachesRenderComponent() {
    Entity enemy = EnemyFactory.create("lesser_shade");

    assertNotNull(enemy.getComponent(TextureRenderComponent.class));
  }

  @Test
  void createUsesRosterStats() {
    Entity enemy = EnemyFactory.create("lesser_shade");
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

    assertEquals(24, stats.getHealth());
  }

  @Test
  void createFallsBackForUnknownId() {
    Entity enemy = EnemyFactory.create("does_not_exist");
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

    assertNotNull(stats);
    assertEquals(1, stats.getHealth()); // BaseEntityConfig default health
  }

  @Test
  void createFromConfigUsesConfigValues() {
    EnemyConfig config = new EnemyConfig();
    config.health = 50;
    config.baseAttack = 9;
    config.armour = 3;

    Entity enemy = EnemyFactory.create(config);
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

    assertEquals(50, stats.getHealth());
    assertEquals(3, stats.getArmour());
  }

  @Test
  void createWithFloorZeroKeepsBaseStats() {
    Entity enemy = EnemyFactory.create("void_knight", 0);

    assertEquals(72, enemy.getComponent(EnemyStatsComponent.class).getHealth());
  }

  @Test
  void createWithHigherFloorScalesStatsUp() {
    int baseHealth =
        EnemyFactory.create("void_knight", 0).getComponent(EnemyStatsComponent.class).getHealth();
    int scaledHealth =
        EnemyFactory.create("void_knight", 5).getComponent(EnemyStatsComponent.class).getHealth();

    assertTrue(scaledHealth > baseHealth);
  }

  @Test
  void getIdsByTierReturnsOnlyMatchingTier() {
    List<String> normals = EnemyFactory.getIdsByTier(EnemyTier.NORMAL);
    List<String> elites = EnemyFactory.getIdsByTier(EnemyTier.ELITE);

    assertTrue(normals.contains("lesser_shade"));
    assertFalse(normals.contains("void_knight"));
    assertTrue(elites.contains("void_knight"));
  }

  @Test
  void getIdsByTierReturnsEmptyListWhenNoneMatch() {
    assertTrue(EnemyFactory.getIdsByTier(EnemyTier.BOSS).isEmpty());
  }

  @Test
  void availableEnemiesIsNeverNull() {
    assertNotNull(EnemyFactory.availableEnemies());
  }

  @Test
  void getTexturePathsIncludesDefaultAndRosterSprites() {
    List<String> paths = Arrays.asList(EnemyFactory.getTexturePaths());

    assertTrue(paths.contains("images/enemies/default.png"));
    assertTrue(paths.contains("images/enemies/lesser_shade.png"));
    assertTrue(paths.contains("images/enemies/void_knight.png"));
  }

  @Test
  void getTexturePathsHasNoDuplicates() {
    String[] paths = EnemyFactory.getTexturePaths();

    assertEquals(paths.length, new HashSet<>(Arrays.asList(paths)).size());
  }

  @Test
  void loadAssetsQueuesEnemyTextures() {
    EnemyFactory.loadAssets();

    ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);
    verify(resourceService).loadTextures(captor.capture());
    assertArrayEquals(EnemyFactory.getTexturePaths(), captor.getValue());
  }

  @Test
  void unloadAssetsReleasesEnemyTextures() {
    EnemyFactory.unloadAssets();

    ArgumentCaptor<String[]> captor = ArgumentCaptor.forClass(String[].class);
    verify(resourceService).unloadAssets(captor.capture());
    assertArrayEquals(EnemyFactory.getTexturePaths(), captor.getValue());
  }
}
