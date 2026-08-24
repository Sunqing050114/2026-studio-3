package com.csse3200.game.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Component used to store information related to combat such as health, attack, etc. Any entities
 * which engage it combat should have an instance of this class registered. This class can be
 * extended for more specific combat needs.
 */
public class CombatStatsComponent extends Component {

  private static final Logger logger = LoggerFactory.getLogger(CombatStatsComponent.class);
  private static final String EVT_IS_DEAD = "entityIsDead";
  private static final String EVT_MAX_HEALTH = "updateMaxHealth";
  private int health;
  private int baseAttack;
  private int maxHealth;

  public CombatStatsComponent(int health, int baseAttack) {
    setMaxHealth(health);
    setHealth(health);
    setBaseAttack(baseAttack);
  }

  private void updateHealth() {
    if (entity != null) {
      entity.getEvents().trigger("updateHealth", this.health);
    }
  }

  /**
   * Returns true if the entity's has 0 health, otherwise false.
   *
   * @return is player dead
   */
  public Boolean isDead() {
    return this.health == 0;
  }

  /**
   * Returns the entity's health.
   *
   * @return entity's health
   */
  public int getHealth() {
    return this.health;
  }

  /**
   * Returns the entity's max health.
   *
   * @return entity's max health
   */
  public int getMaxHealth() {
    return this.maxHealth;
  }

  /**
   * Sets the entity's health. Health has a minimum bound of 0.
   *
   * @param health health
   */
  public void setHealth(int health) {
    if (health >= 0) {
      this.health = Math.min(health, this.maxHealth);
    } else {
      this.health = 0;
    }
    updateHealth();
  }

  /**
   * Heals. Health has a maximum bound of the max health
   *
   * @param health health
   */
  public void heal(int health) {
    if (health > 0) {
      setHealth(Math.min(this.health + health, this.maxHealth));
    }
  }

  /**
   * Damage the entity's health. If health reaches 0 the entity die. currently has a placeholder to
   * calculate shield before player's health. Unfinished
   *
   * @param damage damage
   */
  public void takeDamage(int damage) {
    // add function to calculate damage against shield
    if (damage >= 0 && !isDead()) {
      setHealth(Math.max(this.health - damage, 0));
      if (entity != null && isDead()) {
        entity.getEvents().trigger("entityIsDead");
      }
    }
  }

  /**
   * A setter function for maxHealth, contains a safegaurd to avoid MaxHealth going lower than 1
   * send an update to every listener is changed to ensure real time changes updated.
   *
   * @param healthAmount to be set as the MaxHealth
   */
  public void setMaxHealth(int healthAmount) {
    this.maxHealth =
        Math.max(healthAmount, 1); // Use math.max to restrict the maxhealth from going below 1
    if (entity != null) {
      entity
          .getEvents()
          .trigger(
              "updateMaxHealth",
              this.maxHealth); // this line (basically tells other that is listening to this that
      // the value is changed)
    }
  }

  /**
   * A function to increase the max health. It uses setMaxHealth function to avoid redundancy
   *
   * @param HealthAmount to be set as the MaxHealth
   */
  public void addMaxHealth(int healthAmount) {
    setMaxHealth(healthAmount + this.maxHealth);
  }

  /**
   * Returns the entity's base attack damage.
   *
   * @return base attack damage
   */
  public int getBaseAttack() {
    return baseAttack;
  }

  /**
   * Sets the entity's attack damage. Attack damage has a minimum bound of 0.
   *
   * @param attack Attack damage
   */
  public void setBaseAttack(int attack) {
    if (attack >= 0) {
      this.baseAttack = attack;
    } else {
      logger.error("Can not set base attack to a negative attack value");
    }
  }

  /**
   * Returns the entity's base attack damage. Unused will remove in later sprint
   *
   * @return base attack damage
   */
  public void addHealth(int health) {
    setHealth(this.health + health);
  }

  /**
   * Returns the entity's base attack damage. Unused will remove in later sprint
   *
   * @return base attack damage
   */
  public void hit(CombatStatsComponent attacker) {
    int newHealth = getHealth() - attacker.getBaseAttack();
    setHealth(newHealth);
  }
}
