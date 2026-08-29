package com.csse3200.game.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.cards.configs.CardConfig;
import org.junit.jupiter.api.Test;

class CardConfigTest {
  @Test
  void shouldDefaultIdentifiersToEmptyStrings() {
    CardConfig card = new CardConfig();
    assertEquals("", card.id);
    assertEquals("", card.name);
  }
}
