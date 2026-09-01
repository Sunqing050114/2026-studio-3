package com.csse3200.game.cards;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
//Use this class
public class CardPlayRequestTest  {
    @Test
    void shouldRejectBlankCardID() {
        assertThrows(IllegalArgumentException.class, () -> new CardPlayRequest("", "test_enemy"));
    }
    @Test
    void shouldRejectBlankTargetId() {
        assertThrows(IllegalArgumentException.class, () -> new CardPlayRequest("strike", ""));
    }
}
