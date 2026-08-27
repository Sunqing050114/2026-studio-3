package com.csse3200.game.cards.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class CardLibraryTest {
  @Test
  void shouldLoadCardsFromJson() {
    CardLibrary library = CardLibrary.load();

    assertEquals(6, library.getCards().size());

    CardConfig strike = library.getById("strike");
    assertNotNull(strike);
    assertEquals("Strike", strike.name);
    assertEquals(1, strike.cost);
    assertEquals(EffectType.DAMAGE, strike.effects[0].type);
    assertEquals(TargetType.SINGLE_ENEMY, strike.effects[0].target);
    assertEquals(6, strike.effects[0].amount);
  }
}
