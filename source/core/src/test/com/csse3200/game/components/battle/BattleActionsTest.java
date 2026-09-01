package com.csse3200.game.components.battle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import com.csse3200.game.cards.CardLibrary;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.combat.BattleController;
import com.csse3200.game.components.combat.BattleEvent;
import com.csse3200.game.components.combat.BattlePhase;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.EnemyConfig;
import com.csse3200.game.entities.factories.EnemyFactory;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;

import com.csse3200.game.cards.CardPlayRequest;
import com.csse3200.game.cards.CardType;
import com.csse3200.game.cards.configs.CardConfig;
import java.util.Optional;

class BattleActionsTest {
  private BattleController controller;
  private Entity entity;

  @BeforeEach
  void setUp() {
    Entity player = new Entity();
    player.addComponent(new CombatStatsComponent(20, 0));
    Entity enemy = EnemyFactory.create(new EnemyConfig());
    controller = new BattleController(player, List.of(enemy));
    GdxGame game = mock(GdxGame.class);
    CardLibrary library = mock(CardLibrary.class);
    entity = new Entity().addComponent(new BattleActions(controller, game, library));
    entity.create();
  }

  @Test
  void shouldHandleAttackSelectionDuringPlayerTurn() {
    advanceToPlayerTurn();

    entity.getEvents().trigger("attackCardSelected");

    assertEquals(BattlePhase.PLAYER_ATTACK, controller.getCurrentPhase());
  }

  @Test
  void shouldHandleDefendSelectionDuringPlayerTurn() {
    advanceToPlayerTurn();

    entity.getEvents().trigger("defendCardSelected");

    assertEquals(BattlePhase.PLAYER_DEFEND, controller.getCurrentPhase());
  }

  @Test
  void shouldHandleEndTurnSelectionDuringPlayerTurn() {
    advanceToPlayerTurn();

    entity.getEvents().trigger("endTurnSelected");

    assertEquals(BattlePhase.PLAYER_END, controller.getCurrentPhase());
  }

  @Test
  void shouldIgnoreSelectionWhenCurrentPhaseCannotHandleIt() {
    entity.getEvents().trigger("attackCardSelected");

    assertEquals(BattlePhase.SETUP, controller.getCurrentPhase());
  }

  @Test
  void shouldPublishPhaseChangesToTheBattleUi() {
    AtomicReference<BattlePhase> displayedPhase = new AtomicReference<>();
    entity.getEvents().addListener("phaseChange", displayedPhase::set);
    advanceToPlayerTurn();

    entity.getEvents().trigger("attackCardSelected");

    assertEquals(BattlePhase.PLAYER_ATTACK, displayedPhase.get());
  }

  private void advanceToPlayerTurn() {
    controller.handle(BattleEvent.SETUP_COMPLETE);
  }
}
