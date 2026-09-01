package com.csse3200.game.cards.play.integration;

import com.csse3200.game.cards.play.CardPlayResult;

/** Routes a successful Team 5 result to Team 1 and Team 7 state owners. */
public final class CardPlayResultDispatcher {
  private final EnemyEffectConsumer enemyEffects;
  private final PlayerEffectConsumer playerEffects;

  public CardPlayResultDispatcher(
      EnemyEffectConsumer enemyEffects, PlayerEffectConsumer playerEffects) {
    if (enemyEffects == null) {
      throw new IllegalArgumentException("Enemy effect consumer cannot be null");
    }
    if (playerEffects == null) {
      throw new IllegalArgumentException("Player effect consumer cannot be null");
    }
    this.enemyEffects = enemyEffects;
    this.playerEffects = playerEffects;
  }

  /** Failed results are intentionally ignored, so no real combat state is mutated. */
  public void dispatch(CardPlayResult result) {
    if (result == null) {
      throw new IllegalArgumentException("Card play result cannot be null");
    }
    if (!result.success()) {
      return;
    }
    if (!result.enemyEffects().isEmpty()) {
      enemyEffects.applyEnemyEffects(result.target(), result.enemyEffects());
    }
    if (!result.playerEffects().isEmpty()) {
      playerEffects.applyPlayerEffects(result.playerEffects());
    }
  }
}
