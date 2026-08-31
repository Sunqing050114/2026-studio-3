package com.csse3200.game.components.enemy.EnemyAI;

/** Creates enemy AI implementations from behaviour identifiers. */
public final class EnemyAIFactory {
  public static final String LESSER_SHADE = "lesser_shade_ai";

  /**
   * Creates a new enemy AI from its behaviour identifier.
   *
   * @param behaviourId behaviour identifier loaded from enemy configuration
   * @return a new enemy AI instance
   * @throws IllegalArgumentException if the identifier is null, blank or unknown
   */
  public static EnemyAI create(String behaviourId) {
    if (behaviourId == null || behaviourId.isBlank()) {
      throw new IllegalArgumentException("Enemy behaviour id cannot be blank");
    }

    return switch (behaviourId) {
      case LESSER_SHADE -> new LesserShadeAI();

      default -> throw new IllegalArgumentException("Unknown enemy behaviour id: " + behaviourId);
    };
  }

  private EnemyAIFactory() {
    throw new IllegalStateException("Instantiating utility class");
  }
}
