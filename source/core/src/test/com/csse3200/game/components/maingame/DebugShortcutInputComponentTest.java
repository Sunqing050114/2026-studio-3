package com.csse3200.game.components.maingame;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import com.badlogic.gdx.Input.Keys;
import com.csse3200.game.GdxGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// use mockito to check with unit test if a component in  a game is actually working
@ExtendWith(MockitoExtension.class)
class DebugShortcutInputComponentTest {
    @Mock GdxGame game;

    private DebugShortcutInputComponent input;

    @BeforeEach
    void setUp() {
        input = new DebugShortcutInputComponent(game);
    }

    @Test
    void shouldStartBattleWhenCtrlShiftBPressed() {
        assertFalse(input.keyDown(Keys.CONTROL_LEFT));
        assertFalse(input.keyDown(Keys.SHIFT_LEFT));

        assertTrue(input.keyDown(Keys.B));

        verify(game).startBattle();
    }
}