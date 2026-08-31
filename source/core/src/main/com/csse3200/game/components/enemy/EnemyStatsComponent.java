package com.csse3200.game.components.enemy;

import com.csse3200.game.components.CombatStatsComponent;

/**
 * Health, attack and armour for an enemy, and the resolution of incoming damage.
 *
 * <p>Damage is currently untyped; typed damage can be added as an overload without breaking
 * callers.
 */
public class EnemyStatsComponent extends CombatStatsComponent {
  private static final String DEFAULT_DISPLAY_NAME = "Unknown Enemy";

  private final int maxHealth;
  private final String displayName;
  private int armour;

  public EnemyStatsComponent(int health, int baseAttack, int armour) {
    this(health, baseAttack, armour, DEFAULT_DISPLAY_NAME);
  }

  public EnemyStatsComponent(int health, int baseAttack, int armour, String displayName) {
    super(health, baseAttack);
    this.maxHealth = health;
    this.armour = Math.max(armour, 0);
    this.displayName =
        displayName == null || displayName.isBlank() ? DEFAULT_DISPLAY_NAME : displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public int getArmour() {
    return armour;
  }

  public void setArmour(int armour) {
    this.armour = Math.max(armour, 0);
  }

  /** Adds armour, for example when a defend intent resolves. */
  public void addArmour(int armour) {
    setArmour(this.armour + armour);
  }

  public boolean isAlive() {
    return getHealth() > 0;
  }

  /**
   * Applies damage, depleting armour before health.
   *
   * @param damage incoming damage, ignored if not positive
   */
  public void takeDamage(int damage) {
    if (damage <= 0 || !isAlive()) {
      return;
    }

    int healthBeforeDamage = getHealth();

    int absorbed = Math.min(armour, damage);
    setArmour(armour - absorbed);

    int remaining = damage - absorbed;
    if (remaining > 0) {
      setHealth(getHealth() - remaining);
    }

    int actualHealthDamage = healthBeforeDamage - getHealth();
    if (actualHealthDamage > 0 && entity != null) {
      entity.getEvents().trigger("enemyDamaged", actualHealthDamage);
    }

    if (healthBeforeDamage > 0 && !isAlive() && entity != null) {
      entity.getEvents().trigger("enemyDefeated");
    }
  }
}
