package com.csse3200.game.components.enemy;

import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.components.CombatStatsComponent;
/**
 * Decides and telegraphs an enemy's action each round, then resolves it.
 *
 * <p>Placeholder implementation: always rolls a fixed attack. Behaviour patterns are added in #20.
 */
public class EnemyBehaviourComponent extends Component {
  private static final int PLACEHOLDER_DAMAGE = 5;
  private final String behaviourId;
  private EnemyIntent currentIntent = EnemyIntent.unknown();

  public EnemyBehaviourComponent(String behaviourId) {
    this.behaviourId = behaviourId;
  }

  public String getBehaviourId() {
    return behaviourId;
  }

  /**
   * @return the intent telegraphed for the coming round
   */
  public EnemyIntent getCurrentIntent() {
    return currentIntent;
  }

  /**
   * Decides the action for the coming round and telegraphs it.
   *
   * @return the newly decided intent
   */
  public EnemyIntent rollIntent() {
    entity.getEvents().trigger("intentChanged", currentIntent);
    return currentIntent;
  }

  /**
   * Resolves the current intent against the given target.
   *
   * @param target the entity the intent acts on, typically the player
   */
  public void executeIntent(Entity target) {
    switch (currentIntent.getType()) {
      case ATTACK -> attack(target);
      case DEFEND -> defend();
      default -> {
        // BUFF, DEBUFF and UNKNOWN are handled in #20.
      }
    }
  }

  private void attack(Entity target) {
    if (target == null) return;

    CombatStatsComponent targetStats = target.getComponent(CombatStatsComponent.class);
    if (targetStats != null) {
      targetStats.takeDamage(currentIntent.getValue());
    }
  }

  private void defend() {
    EnemyStatsComponent stats = entity.getComponent(EnemyStatsComponent.class);
    if (stats != null) {
      stats.addArmour(currentIntent.getValue());
    }
  }
}
