package com.csse3200.game.cards.effects;

class RecordingCharacterEffectGateway implements CharacterEffectGateway {
  int damage;
  int block;
  int healing;
  int poison;
  int vulnerable;
  int strength;

  @Override
  public void damage(int amount) {
    damage += amount;
  }

  @Override
  public void gainBlock(int amount) {
    block += amount;
  }

  @Override
  public void heal(int amount) {
    healing += amount;
  }

  @Override
  public void applyPoison(int amount) {
    poison += amount;
  }

  @Override
  public void applyVulnerable(int amount) {
    vulnerable += amount;
  }

  @Override
  public void applyStrength(int amount) {
    strength += amount;
  }
}
