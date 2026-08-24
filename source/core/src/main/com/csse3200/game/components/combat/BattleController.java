package com.csse3200.game.components.combat;

import java.util.Objects;

/**
 * The central controller of the current state of the battle loop. Controls what phase the battle is
 * currently in, what actions are currently allowed and what the illegal and legal transitions are
 * This is functionally the Finite State Machine.
 */
public class BattleController {
  private BattlePhase currentPhase;
  private int currentEnemyIndex;
  private final BattleTransitions battleTransitions;

  private BattleController() {
    this.battleTransitions = new BattleTransitions();
    this.currentPhase = BattlePhase.SETUP;
    this.currentEnemyIndex = -1;
  }

  /**
   * Handles an individual event that occurs within a battle loop.
   *
   * @param event The event to be handled.
   * @throws IllegalStateException When the given transition isn't allowed.
   */
  private void handle(BattleEvent event) throws IllegalStateException {
    BattlePhase nextPhase = battleTransitions.getNextPhase(
            this.getCurrentPhase(),
            event
    );

    this.validateEventTransition(event, nextPhase);
    this.transition(nextPhase);
  }

  private void transition(BattlePhase nextPhase) {
    BattlePhase previousPhase = currentPhase;
    this.setCurrentPhase(nextPhase);

    this.notifyPhaseChange(previousPhase, this.getCurrentPhase());
    this.phaseChange(nextPhase);
  }

  /**
   * The dispatch function for the action branches.
   *
   * @param phase The phase to dispatch.
   */
  private void phaseChange(BattlePhase phase) {
    switch (phase) {
      // Setup States
      case SETUP -> enterSetup();
      case REVEAL_INTENTS -> enterRevealIntents();

      // Player States
      case PLAYER_START -> enterPlayerStart();
      case PLAYER_TURN -> enterPlayerTurn();
      case PLAYER_ATTACK -> enterPlayerAttack();
      case PLAYER_DEFEND -> enterPlayerDefend();
      case PLAYER_OTHER -> enterPlayerOther();
      case PLAYER_END -> enterPlayerEnd();
      case PLAYER_RESOLVED -> enterPlayerResolved();

      // Enemy States
      case ENEMY_TURN -> enterEnemyTurn();
      case ENEMY_ATTACK -> enterEnemyAttack();
      case ENEMY_DEFEND -> enterEnemyDefend();
      case ENEMY_OTHER -> enterEnemyOther();
      case ENEMY_RESOLVED -> enterEnemyResolved();
      case NEXT_ENEMY -> enterNextEnemy();

      // Terminal States
      case VICTORY -> enterVictory();
      case DEFEAT -> enterDefeat();
    }
  }

  /*------------------------- Getters & Setters ----------------------------*/
  public BattlePhase getCurrentPhase() {
    return this.currentPhase;
  }

  private void setCurrentPhase(BattlePhase nextPhase) {
    // TODO: defensive check here
    this.currentPhase = nextPhase;
  }

  private void setCurrentEnemyIndex(int currentEnemyIndex) {
    this.currentEnemyIndex = currentEnemyIndex;
  }

  public int getCurrentEnemyIndex() {
    return currentEnemyIndex;
  }

  /*------------------------- Helper functions ----------------------------*/

  /**
   *  A helper function that validates whether a transition is allowed.
   *
   * @param event The currently executing event.
   * @param nextPhase The speculative next phase to transition to.
   * @throws IllegalStateException Throws when the state transition is deemed illegal.
   */
  private void validateEventTransition(BattleEvent event, BattlePhase nextPhase)
          throws IllegalStateException {
    if (Objects.isNull(nextPhase)) {
      throw new IllegalStateException(
              "Invalid battle transition: "
                      + this.currentPhase
                      + "-->"
                      + event
      );
    }
  }

  /**
   * Convenience function for returning if a given event can
   * be handled within a state.
   *
   * @param event The event to check.
   * @return True if the event is valid to be applied. False if not.
   */
  public boolean canHandle(BattleEvent event) {
    return this.battleTransitions
            .getNextPhase(this.currentPhase, event)
            != null;
  }

  /*------------------------- Stub functions ----------------------------*/

  /**
   * Stub function for notifying UI or any listeners about a phase change.
   *
   * @param previousPhase The phase that is being left.
   * @param nextPhase The phase that is being entered.
   */
  private void notifyPhaseChange(BattlePhase previousPhase, BattlePhase nextPhase) {
    ;
  }

  private void enterSetup() {
    // Coordinate battle setup.
  }

  private void enterRevealIntents() {
    // Ask the enemy system to reveal intents.
  }

  private void enterPlayerStart() {
    // Coordinate start-of-turn operations.
  }

  private void enterPlayerTurn() {
    // Enable or accept player actions.
  }

  private void enterPlayerAttack() {
    // Ask the relevant system to execute the submitted attack.
  }

  private void enterPlayerDefend() {
    // Ask the relevant system to execute the submitted defence.
  }

  private void enterPlayerOther() {
    // Ask the relevant system to execute the submitted action.
  }

  private void enterPlayerEnd() {
    // Coordinate end-of-turn operations.
  }

  private void enterPlayerResolved() {
    // Check battle outcome before allowing another action.
  }

  private void enterEnemyTurn() {
    // Begin the current enemy's action.
  }

  private void enterEnemyAttack() {
    // Ask the enemy system to execute its attack intent.
  }

  private void enterEnemyDefend() {
    // Ask the enemy system to execute its defend intent.
  }

  private void enterEnemyOther() {
    // Ask the enemy system to execute its other intent.
  }

  private void enterEnemyResolved() {
    // Check the outcome after the enemy action.
  }

  private void enterNextEnemy() {
    // Advance to the next eligible enemy.
  }

  private void enterVictory() {
    // Notify other systems that the battle was won.
  }

  private void enterDefeat() {
    // Notify other systems that the battle was lost.
  }
}
