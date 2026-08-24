package com.csse3200.game.cards;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for a single card, loaded from {@code configs/cards.json}. A card's category is
 * kept separate from its effects, so that any card type may carry any combination of effects. For
 * example, an ATTACK card may deal damage and also apply a debuff.
 */
public class CardConfig {
  /** Unique key used to look this card up. */
  public String id = "";

  /** Name shown to the player. */
  public String name = "";

  /** Rules text shown to the player. */
  public String description = "";

  /** Energy required to play this card. */
  public int cost = 0;

  /** Category of the card, kept separate from what the card actually does. */
  public CardType type = CardType.ATTACK;

  /** How rare the card is, used when offering cards as rewards. */
  public Rarity rarity = Rarity.COMMON;

  /** Who the card's effects are applied to. */
  public TargetType target = TargetType.SINGLE_ENEMY;

  /** Effects applied in order when the card is played. */
  public EffectConfig[] effects = new EffectConfig[0];

  /** Path to the card artwork, relative to the assets directory. */
  public String texturePath = "";

  /** Required by the JSON deserialiser. */
  public CardConfig() {}

  /**
   * Checks that this card is internally consistent and safe for gameplay systems to use. File level
   * checks such as duplicate ids belong to the card loading task, since a single card cannot see
   * the rest of the collection.
   *
   * @return a list of human-readable problems, empty if the card is valid
   */
  public List<String> validate() {
    List<String> errors = new ArrayList<>();

    if (id == null || id.isBlank()) {
      errors.add("id must be set to a unique value");
    }
    if (name == null || name.isBlank()) {
      errors.add("name must not be blank");
    }
    if (cost < 0) {
      errors.add("cost must not be negative, was " + cost);
    }
    if (type == null) {
      errors.add("type must not be null");
    }
    if (rarity == null) {
      errors.add("rarity must not be null");
    }
    if (target == null) {
      errors.add("target must not be null");
    }
    if (texturePath == null || texturePath.isBlank()) {
      errors.add("texturePath must not be blank");
    }
    if (effects == null || effects.length == 0) {
      errors.add("a card must define at least one effect");
      return errors;
    }

    for (int i = 0; i < effects.length; i++) {
      validateEffect(effects[i], i, errors);
    }
    return errors;
  }

  /**
   * Checks a single effect and appends any problems to the given list.
   *
   * @param effect the effect to check, may be null
   * @param index position of the effect, used to make messages easier to trace
   * @param errors list that problems are appended to
   */
  private void validateEffect(EffectConfig effect, int index, List<String> errors) {
    String prefix = "effect " + index + ": ";
    if (effect == null) {
      errors.add(prefix + "must not be null");
      return;
    }
    if (effect.type == null) {
      errors.add(prefix + "type must not be null");
      return;
    }
    if (effect.value <= 0) {
      errors.add(prefix + effect.type + " value must be positive, was " + effect.value);
    }
    if (effect.type.usesDuration()) {
      if (effect.duration <= 0) {
        errors.add(prefix + effect.type + " requires a positive duration, was " + effect.duration);
      }
    } else if (effect.duration != 0) {
      errors.add(prefix + effect.type + " must not set a duration, was " + effect.duration);
    }
  }

  /**
   * @return true if this card has no validation problems
   */
  public boolean isValid() {
    return validate().isEmpty();
  }
}
