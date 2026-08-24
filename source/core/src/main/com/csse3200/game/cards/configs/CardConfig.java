package com.csse3200.game.cards.configs;

/**
 * Temporary card definition used by {@link com.csse3200.game.cards.CardLibrary}.
 *
 * <p>Only {@code id} is required here so cards can be registered and retrieved. The complete card
 * data model belongs to the Card Data Model task and should replace this stub.
 */
public class CardConfig {
  private String id;

  public CardConfig() {}

  public CardConfig(String id) {
    this.id = id;
  }

  /**
   * Returns the unique identifier for this card.
   *
   * @return card ID, which may be null if it has not been set
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the unique identifier for this card.
   *
   * @param id card ID
   */
  public void setId(String id) {
    this.id = id;
  }
}
