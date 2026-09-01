package com.csse3200.game.components.enemy;

import java.util.Objects;

/** An enemy's telegraphed action for the coming round. */
public class EnemyIntent {
  private final IntentType type;
  private final int value;

  public EnemyIntent(IntentType type, int value) {
    this.type = type;
    this.value = value;
  }

  public static EnemyIntent attack(int damage) {
    return new EnemyIntent(IntentType.ATTACK, damage);
  }

  public static EnemyIntent defend(int armour) {
    return new EnemyIntent(IntentType.DEFEND, armour);
  }

  public static EnemyIntent unknown() {
    return new EnemyIntent(IntentType.UNKNOWN, 0);
  }

  public IntentType getType() {
    return type;
  }

  public int getValue() {
    return value;
  }

  /** Two intents are equal when their type and value match. */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof EnemyIntent)) {
      return false;
    }
    EnemyIntent that = (EnemyIntent) other;
    return value == that.value && type == that.type;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, value);
  }

  @Override
  public String toString() {
    return type + "(" + value + ")";
  }
}
