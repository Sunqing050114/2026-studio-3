package com.csse3200.game.encounters.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class FunctionalDeckAdapterTest {
  @Test
  void shouldMatchTeam5VoidAddAndBooleanRemoveContract() {
    List<String> cards = new ArrayList<>();
    FunctionalDeckAdapter adapter =
        new FunctionalDeckAdapter(cardId -> cards.add(cardId), cards::remove);

    assertTrue(adapter.addCard("strike"));
    assertTrue(cards.contains("strike"));
    assertTrue(adapter.removeCard("strike"));
    assertFalse(cards.contains("strike"));
  }

  @Test
  void shouldRejectBlankIdsBeforeCallingDeck() {
    List<String> cards = new ArrayList<>();
    FunctionalDeckAdapter adapter =
        new FunctionalDeckAdapter(cardId -> cards.add(cardId), cards::remove);

    assertFalse(adapter.addCard(""));
    assertFalse(adapter.removeCard(null));
    assertTrue(cards.isEmpty());
  }
}
