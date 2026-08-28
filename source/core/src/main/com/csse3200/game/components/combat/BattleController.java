package com.csse3200.game.components.combat;

import com.csse3200.game.components.enemy.EnemyBehaviourComponent;
import com.csse3200.game.components.enemy.EnemyIntent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.components.enemy.IntentType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.EventHandler;
import com.csse3200.game.events.listeners.EventListener2;
import java.util.List;
import java.util.Objects;

/**
 * The central controller of the current state of the battle loop. Controls what phase the battle is
 * currently in, what actions are currently allowed and what the illegal and legal transitions are
 * This is functionally the Finite State Machine.
 */
public class BattleController {
  private BattlePhase currentPhase;
  private int currentEnemyIndex;
  private final Entity player;
  private final List<Entity> enemies;
  private final BattleTransitions battleTransitions;
  private final EventHandler eventHandler;
  private static final String PHASE_CHANGED_EVENT = "battlePhaseChanged";

  public BattleController(Entity player, List<Entity> enemies) throws IllegalArgumentException {
    this.player = player;
    // Guard against a null player entity.
    if (this.player == null) {
      throw new IllegalArgumentException("Player cannot be null.");
    }

    // Protect against a null or empty list and null entries.
    if (enemies == null || enemies.isEmpty()) {
      throw new IllegalArgumentException("Enemy list cannot be null or empty.");
    } else if (!enemies.stream().allMatch(Objects::nonNull)) {
      throw new IllegalArgumentException("One or more enemies are null.");
    }
    this.enemies = List.copyOf(enemies);

    this.battleTransitions = new BattleTransitions();
    this.currentPhase = BattlePhase.SETUP;
    this.currentEnemyIndex = -1;
    this.eventHandler = new EventHandler();
  }

  /**
   * Handles an individual event that occurs within a battle loop.
   *
   * @param event The event to be handled.
   * @throws IllegalStateException When the given transition isn't allowed.
   */
  public void handle(BattleEvent event) {
    Objects.requireNonNull(event, "event cannot be null");
    BattlePhase nextPhase = battleTransitions.getNextPhase(this.getCurrentPhase(), event);
    this.validateEventTransition(event, nextPhase);
    this.transition(nextPhase);
  }

  /**
   * Transitions from the current phase to the next phase.
   *
   * @param nextPhase The phase to transition to.
   */
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

      // Terminal States
      case VICTORY -> enterVictory();
      case DEFEAT -> enterDefeat();
    }
  }

  /*--------------------------- Public Methods -----------------------------*/

  /*------------------------- Getters & Setters ----------------------------*/

  public BattlePhase getCurrentPhase() {
    return this.currentPhase;
  }

  private void setCurrentPhase(BattlePhase nextPhase) {
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
   * A helper function that validates whether a transition is allowed.
   *
   * @param event The currently executing event.
   * @param nextPhase The speculative next phase to transition to.
   * @throws IllegalStateException Throws when the state transition is deemed illegal.
   */
  private void validateEventTransition(BattleEvent event, BattlePhase nextPhase)
      throws IllegalStateException {
    if (Objects.isNull(nextPhase)) {
      throw new IllegalStateException(
          "Invalid battle transition: " + this.currentPhase + "-->" + event);
    }
  }

  /**
   * Stub function for notifying UI or any listeners about a phase change.
   *
   * @param previousPhase The phase that is being left.
   * @param nextPhase The phase that is being entered.
   */
  private void notifyPhaseChange(BattlePhase previousPhase, BattlePhase nextPhase) {
    eventHandler.trigger(PHASE_CHANGED_EVENT, previousPhase, nextPhase);
  }

  /**
   * Adds a listener to the event handler, which ultimately informs external teams about a phase
   * change.
   *
   * @param listener The instantiated external listener.
   */
  public void addPhaseChangeListener(EventListener2<BattlePhase, BattlePhase> listener) {
    Objects.requireNonNull(listener, "Listener must not be null.");
    eventHandler.addListener(PHASE_CHANGED_EVENT, listener);
  }

  /**
   * Convenience function for returning if a given event can be handled within a state.
   *
   * @param event The event to check.
   * @return True if the event is valid to be applied. False if not.
   */
  public boolean canHandle(BattleEvent event) {
    return this.battleTransitions.getNextPhase(this.currentPhase, event) != null;
  }

  /*------------------------- Stub functions ----------------------------*/

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
    if (currentEnemyIndex < 0) {
      currentEnemyIndex = 0;
    }

    EnemyIntent intent = getCurrentEnemyBehaviour().rollIntent();
    if (intent.getType() == IntentType.ATTACK) {
      handle(BattleEvent.ENEMY_ATTACK_SELECTED);
    } else if (intent.getType() == IntentType.DEFEND) {
      handle(BattleEvent.ENEMY_DEFEND_SELECTED);
    } else {
      handle(BattleEvent.ENEMY_OTHER_SELECTED);
    }
  }

  private void enterEnemyAttack() {
    executeCurrentEnemyIntent();
  }

  private void enterEnemyDefend() {
    executeCurrentEnemyIntent();
  }

  private void enterEnemyOther() {
    executeCurrentEnemyIntent();
  }

  private void enterEnemyResolved() {
    currentEnemyIndex++;
    while (currentEnemyIndex < enemies.size() && !isEnemyAlive(enemies.get(currentEnemyIndex))) {
      currentEnemyIndex++;
    }

    if (currentEnemyIndex < enemies.size()) {
      handle(BattleEvent.MORE_ENEMIES);
    } else {
      currentEnemyIndex = -1;
      handle(BattleEvent.ENEMY_PHASE_COMPLETE);
    }
  }

  private Entity getCurrentEnemy() {
    if (currentEnemyIndex < 0 || currentEnemyIndex >= enemies.size()) {
      throw new IllegalStateException("No active enemy.");
    }
    return enemies.get(currentEnemyIndex);
  }

  private EnemyBehaviourComponent getCurrentEnemyBehaviour() {
    EnemyBehaviourComponent behaviour =
        getCurrentEnemy().getComponent(EnemyBehaviourComponent.class);
    if (behaviour == null) {
      throw new IllegalStateException("Current enemy is missing EnemyBehaviourComponent.");
    }
    return behaviour;
  }

  private void executeCurrentEnemyIntent() {
    getCurrentEnemyBehaviour().executeIntent(player);
    handle(BattleEvent.ENEMY_ACTION_RESOLVED);
  }

  private boolean isEnemyAlive(Entity enemy) {
    EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);
    return stats != null && stats.isAlive();
  }

  private void enterVictory() {
    // Notify other systems that the battle was won.
  }

  private void enterDefeat() {
    // Notify other systems that the battle was lost.
  }
}
