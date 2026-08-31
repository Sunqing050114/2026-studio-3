package com.csse3200.game.components.battle;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.combat.BattleController;
import com.csse3200.game.components.combat.BattleEvent;

/** Connects battle UI events to valid transitions in the battle controller. */
public class BattleActions extends Component {
  static final String ATTACK_SELECTED_EVENT = "attackCardSelected";
  static final String DEFEND_SELECTED_EVENT = "defendCardSelected";
  static final String END_TURN_SELECTED_EVENT = "endTurnSelected";
  static final String PHASE_CHANGED_EVENT = "phaseChange";

  private final BattleController controller;
  private GdxGame game;

  public BattleActions(BattleController controller, GdxGame game) {
    this.controller = controller;
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("battle", this::onStart);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener(ATTACK_SELECTED_EVENT, controller::selectAttack);

    entity.getEvents().addListener(DEFEND_SELECTED_EVENT, controller::selectDefend);

    entity.getEvents().addListener(END_TURN_SELECTED_EVENT, controller::endPlayerTurn);
    controller.addPhaseChangeListener(
        (previousPhase, nextPhase) -> entity.getEvents().trigger(PHASE_CHANGED_EVENT, nextPhase));
  }

//  private void selectAttack() {
//    handleIfAllowed(BattleEvent.PLAYER_ATTACK_SELECTED);
//  }
//
//  private void selectDefend() {
//    handleIfAllowed(BattleEvent.PLAYER_DEFEND_SELECTED);
//  }
//
//  private void selectEndTurn() {
//    handleIfAllowed(BattleEvent.PLAYER_END_REQUESTED);
//  }
//
//  private void handleIfAllowed(BattleEvent event) {
//    if (controller.canHandle(event)) {
//      controller.handle(event);
//    }
//  }

  private void onStart() {
    game.setScreen(GdxGame.ScreenType.BATTLE_SCREEN);
  }

  private void onExit() {
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }
}
