package com.csse3200.game.components.battle;

import com.csse3200.game.GdxGame;
import com.csse3200.game.cards.CardLibrary;
import com.csse3200.game.cards.CardPlayRequest;
import com.csse3200.game.cards.CardType;
import com.csse3200.game.cards.EffectType;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.configs.EffectConfig;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.combat.BattleController;
import com.csse3200.game.components.combat.BattleEvent;
import com.csse3200.game.components.player.PlayerIntent;

/** Connects battle UI events to valid transitions in the battle controller. */
public class BattleActions extends Component {
  static final String ATTACK_SELECTED_EVENT = "attackCardSelected";
  static final String DEFEND_SELECTED_EVENT = "defendCardSelected";
  static final String END_TURN_SELECTED_EVENT = "endTurnSelected";
  static final String PHASE_CHANGED_EVENT = "phaseChange";
  static final String PLAY_CARD_EVENT = "playCard";

  private final BattleController controller;
  private final GdxGame game;
  private final CardLibrary library;

  public BattleActions(BattleController controller, GdxGame game, CardLibrary library) {
    this.controller = controller;
    this.game = game;
    this.library = library;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("battle", this::onStart);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener(ATTACK_SELECTED_EVENT, controller::selectAttack);

    entity.getEvents().addListener(DEFEND_SELECTED_EVENT, controller::selectDefend);

    entity.getEvents().addListener(END_TURN_SELECTED_EVENT, controller::endPlayerTurn);
    entity.getEvents().addListener(PLAY_CARD_EVENT, this::onCardPlayed);
    controller.addPhaseChangeListener(
        (previousPhase, nextPhase) -> entity.getEvents().trigger(PHASE_CHANGED_EVENT, nextPhase));
    entity.getEvents().addListener("cardPlayed", this::logCardPlayed);
    entity.getEvents().addListener("endturn", this::triggerEndTurn);
  }
  //test is card is played
  private void logCardPlayed(String cardName, String targetID) {
    System.out.println("Card played: " + cardName + " on target: " + targetID);
  }
  /**
   * A card was played (self-target on click, or dropped on a target) — see Clickable/DragNDrop
   * and EnemyDropTargetComponent for how "playCard" ends up firing with (cardId, targetId).
   * Translates the raw cardId into its display name and re-fires as "cardPlayed" for UI feedback.
   */

  private void onCardPlayed(String cardID, String targetID) {
    var optionalCard = library.getCard(cardID);
    if (optionalCard.isEmpty()) {
      return;
    }
    CardConfig cardConfig = optionalCard.get();
    CardPlayRequest request = new CardPlayRequest(cardID,targetID);
    PlayerIntent intent = classifyCard(cardConfig);

    if (controller.submitCardPlayRequest(request,intent)) {
      entity.getEvents().trigger("cardPlayed", cardConfig.name, targetID);
    }

  }

//  private void selectAttack() {
//    handleIfAllowed(BattleEvent.PLAYER_ATTACK_SELECTED);
//  }
//
//  private void selectDefend() {
//    handleIfAllowed(BattleEvent.PLAYER_DEFEND_SELECTED);
//  }
//
  private void triggerEndTurn() {
    controller.endPlayerTurn();
  }
  private void selectEndTurn() {
    if (controller.canHandle(BattleEvent.PLAYER_END_REQUESTED)) {

    }
  }

  private PlayerIntent classifyCard(CardConfig card) {
    if (card.type == CardType.ATTACK) {
      return PlayerIntent.ATTACK;
    }

    for (EffectConfig effect : card.effects) {
      if (effect.type == EffectType.BLOCK) {
        return PlayerIntent.DEFEND;
      }
    }

    return PlayerIntent.OTHER;
  }
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
