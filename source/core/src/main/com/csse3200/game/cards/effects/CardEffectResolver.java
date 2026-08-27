package com.csse3200.game.cards.effects;

import com.csse3200.game.cards.config.CardConfig;
import com.csse3200.game.cards.config.EffectConfig;
import java.util.Collections;
import java.util.List;

/** Resolves card effect target declarations into concrete runtime targets and executes them. */
public class CardEffectResolver {
  private final EffectExecutor effectExecutor;

  public CardEffectResolver() {
    this(new EffectExecutor());
  }

  public CardEffectResolver(EffectExecutor effectExecutor) {
    if (effectExecutor == null) {
      throw new IllegalArgumentException("Effect executor cannot be null");
    }
    this.effectExecutor = effectExecutor;
  }

  public void resolve(
      CardConfig card,
      CharacterEffectGateway self,
      CharacterEffectGateway selectedEnemy,
      List<CharacterEffectGateway> allEnemies) {
    if (card == null) {
      throw new IllegalArgumentException("Card cannot be null");
    }
    if (card.effects == null) {
      return;
    }

    for (EffectConfig effect : card.effects) {
      for (CharacterEffectGateway target :
          resolveTargets(effect, self, selectedEnemy, safeEnemies(allEnemies))) {
        effectExecutor.execute(effect, target);
      }
    }
  }

  private List<CharacterEffectGateway> resolveTargets(
      EffectConfig effect,
      CharacterEffectGateway self,
      CharacterEffectGateway selectedEnemy,
      List<CharacterEffectGateway> allEnemies) {
    if (effect == null) {
      throw new IllegalArgumentException("Effect cannot be null");
    }
    if (effect.target == null) {
      throw new IllegalArgumentException("Effect target type cannot be null");
    }

    switch (effect.target) {
      case SELF:
        if (self == null) {
          throw new IllegalArgumentException("Self target cannot be null");
        }
        return Collections.singletonList(self);
      case SINGLE_ENEMY:
        if (selectedEnemy == null) {
          throw new IllegalArgumentException("Selected enemy target cannot be null");
        }
        return Collections.singletonList(selectedEnemy);
      case ALL_ENEMIES:
        return allEnemies;
      default:
        throw new IllegalArgumentException("Unsupported target type: " + effect.target);
    }
  }

  private List<CharacterEffectGateway> safeEnemies(List<CharacterEffectGateway> allEnemies) {
    return allEnemies == null ? Collections.emptyList() : allEnemies;
  }
}
