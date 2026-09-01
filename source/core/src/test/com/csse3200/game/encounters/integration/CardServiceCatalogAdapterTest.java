package com.csse3200.game.encounters.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.cards.CardService;
import com.csse3200.game.cards.configs.CardConfig;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class CardServiceCatalogAdapterTest {
  @Test
  void shouldDelegateCardExistenceToTeam6CardService() {
    CardConfig strike = new CardConfig();
    strike.id = "strike";
    CardService service =
        new CardService() {
          @Override
          public Optional<CardConfig> getCard(String cardId) {
            return "strike".equals(cardId) ? Optional.of(strike) : Optional.empty();
          }

          @Override
          public List<CardConfig> getAllCards() {
            return List.of(strike);
          }
        };

    CardServiceCatalogAdapter adapter = new CardServiceCatalogAdapter(service);

    assertTrue(adapter.containsCard("strike"));
    assertFalse(adapter.containsCard("missing"));
    assertFalse(adapter.containsCard(""));
    assertFalse(adapter.containsCard(null));
  }
}
