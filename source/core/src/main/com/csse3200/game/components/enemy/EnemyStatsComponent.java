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
  private int lastHealth;

  public EnemyStatsComponent(int health, int baseAttack, int armor) {
    this(health, baseAttack, armor, DEFAULT_DISPLAY_NAME);
  }

  public EnemyStatsComponent(int health, int baseAttack, int armor, String displayName) {
    super(health, baseAttack);
    super.setArmor(armor);
    this.displayName =
            displayName == null || displayName.isBlank() ? DEFAULT_DISPLAY_NAME : displayName;
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

  public void setArmor(int armor) {
    super.setArmor(armor);
  }

  public void addArmor(int armor) {
    super.addArmor(armor);
  }

  public boolean isAlive() {
    return getHealth() > 0;
  }

  @Override
  public void create() {
    super.create();
    lastHealth = getHealth();
    entity.getEvents().addListener("updateHealth", this::onHealthUpdated);
  }

  private void onHealthUpdated(int health) {
    if (health < lastHealth) {
      entity.getEvents().trigger("enemyDamaged", lastHealth - health);
    }
    if (lastHealth > 0 && health == 0) {
      entity.getEvents().trigger("enemyDefeated");
    }
    lastHealth = health;
  }
}

