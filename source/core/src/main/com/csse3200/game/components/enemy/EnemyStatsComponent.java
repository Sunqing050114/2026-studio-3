package com.csse3200.game.components.enemy;

import com.csse3200.game.components.CombatStatsComponent;

/**
 * Health, attack and armour for an enemy, and the resolution of incoming damage.
 *
 * <p>Damage is currently untyped; typed damage can be added as an overload without breaking
 * callers.
 */
public class EnemyStatsComponent extends CombatStatsComponent {
  private final int maxHealth;
  private int armour;

  public EnemyStatsComponent(int health, int baseAttack) {
    this(health, baseAttack, 0);
  }

  public EnemyStatsComponent(int health, int baseAttack, int armour) {
    super(health, baseAttack);
    this.maxHealth = health;
    this.armour = Math.max(armour, 0);
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
    if (damage <= 0) {
      return;
    }
    int absorbed = Math.min(armour, damage);
    armour -= absorbed;
    int remaining = damage - absorbed;
    if (remaining > 0) {
      setHealth(getHealth() - remaining);
      entity.getEvents().trigger("enemyDamaged", remaining);
    }
    if (!isAlive()) {
      entity.getEvents().trigger("enemyDefeated");
    }
  }
}
