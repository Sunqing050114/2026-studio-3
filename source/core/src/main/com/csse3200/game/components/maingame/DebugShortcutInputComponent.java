// shortcut test input to trigger battle scene

package com.csse3200.game.components.maingame;
import com.csse3200.game.GdxGame;
import com.csse3200.game.input.InputComponent;
import com.badlogic.gdx.Input.Keys;
public class DebugShortcutInputComponent extends InputComponent {
    private final GdxGame game;
    private boolean control_pressed;
    private boolean shift_pressed;

    public DebugShortcutInputComponent(GdxGame game) {
        super(20);
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Keys.CONTROL_LEFT:
                control_pressed = true;
                return false;
            case Keys.SHIFT_LEFT:
                shift_pressed = true;
                return false;
            case Keys.B:
                if (control_pressed &&  shift_pressed) {
                    game.startBattle();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }
}