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
    validateEventTransition(event, nextPhase);
  }

  private void transition() {
    ;
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
    if (!Objects.isNull(nextPhase)) {
      setCurrentPhase(nextPhase);
    } else {
      throw new IllegalStateException(
              "Invalid battle transition: "
                      + this.currentPhase
                      + "-->"
                      + event
      );
    }
  }
}
