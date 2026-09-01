package com.csse3200.game.components.enemy;

import com.csse3200.game.components.CombatStatsComponent;

/**
 * Health, attack and armor for an enemy, and the resolution of incoming damage.
 *
 * <p>Damage is currently untyped; typed damage can be added as an overload without breaking
 * callers.
 */
public class EnemyStatsComponent extends CombatStatsComponent {
  private static final String DEFAULT_DISPLAY_NAME = "Unknown Enemy";

  private final String displayName;

  public EnemyStatsComponent(int health, int baseAttack, int armor) {
    this(health, baseAttack, armor, DEFAULT_DISPLAY_NAME);
  }

  public EnemyStatsComponent(int health, int baseAttack, int armor, String displayName) {
    super(health, baseAttack);
    super.setArmor(armor);
    this.displayName = displayName == null || displayName.isBlank()
            ? DEFAULT_DISPLAY_NAME
            : displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getMaxHealth() {
    return super.getMaxHealth();
  }

  public int getArmor() {
    return super.getArmor();
  }

  public void setArmor(int armour) {
    super.setArmor(armour);
  }

  /** Adds armour, for example when a defend intent resolves. */
  public void addArmor(int armour) {
    super.addArmor(armour);
  }

  public boolean isAlive() {
    return getHealth() > 0;
  }

  /**
   * Applies damage, depleting armor before health.
   *
   * @param damage incoming damage, ignored if not positive
   */
  public void takeDamage(int damage) {
    if (damage <= 0 || !isAlive()) {
      return;
    }

    int healthBeforeDamage = getHealth();

    int absorbed = Math.min(armor, damage);
    setArmor(armor - absorbed);

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
