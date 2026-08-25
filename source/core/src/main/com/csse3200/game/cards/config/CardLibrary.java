package com.csse3200.game.cards.config;

import com.csse3200.game.files.FileLoader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Loads and indexes card configuration data from JSON. */
public class CardLibrary {
  public static final String DEFAULT_CARD_CONFIG_PATH = "configs/cards.json";

  private final List<CardConfig> cards;
  private final Map<String, CardConfig> cardsById = new HashMap<>();

  private CardLibrary(List<CardConfig> cards) {
    this.cards = Collections.unmodifiableList(cards);
    for (CardConfig card : cards) {
      if (card != null && card.id != null && !card.id.isBlank()) {
        cardsById.put(card.id, card);
      }
    }
  }

  public static CardLibrary load() {
    return load(DEFAULT_CARD_CONFIG_PATH);
  }

  public static CardLibrary load(String filename) {
    CardLibraryConfig config = FileLoader.readClass(CardLibraryConfig.class, filename);
    if (config == null || config.cards == null) {
      return new CardLibrary(Collections.emptyList());
    }
    return new CardLibrary(Arrays.asList(config.cards));
  }

  public List<CardConfig> getCards() {
    return cards;
  }

  public CardConfig getById(String id) {
    return cardsById.get(id);
  }
}
