package com.csse3200.game.components.battle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.ui.UIComponent;

/**
 * Shows a short, self-dismissing line describing the most recent battle action, so the player sees
 * "you did X" between their turn and the enemy's, and "the enemy did Y" before their next turn.
 *
 * <p>Listens for {@link BattleActions#BATTLE_LOG_EVENT} and the win/lose events on its own entity.
 */
public class BattleLogDisplay extends UIComponent {
  private static final float FADE_IN = 0.15f;
  private static final float HOLD = 2.5f;
  private static final float FADE_OUT = 0.4f;

  private Table root;
  private Label line;

  @Override
  public void create() {
    super.create();

    root = new Table();
    root.setFillParent(true);
    root.top();

    line = new Label("", skin);
    line.setFontScale(1.3f);
    line.getColor().a = 0f;
    root.add(line).padTop(40f);
    stage.addActor(root);

    entity.getEvents().addListener(BattleActions.BATTLE_LOG_EVENT, this::show);
    entity.getEvents().addListener(BattleActions.BATTLE_WON_EVENT, () -> show("VICTORY!"));
    entity.getEvents().addListener(BattleActions.BATTLE_LOST_EVENT, () -> show("DEFEAT..."));
  }

  private void show(String message) {
    if (message == null || line == null) {
      return;
    }
    line.setText(message);
    line.clearActions();
    line.addAction(
        Actions.sequence(
            Actions.alpha(1f, FADE_IN), Actions.delay(HOLD), Actions.alpha(0f, FADE_OUT)));
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Rendered by the stage.
  }

  @Override
  public void dispose() {
    if (root != null) {
      root.remove();
    }
    super.dispose();
  }
}
