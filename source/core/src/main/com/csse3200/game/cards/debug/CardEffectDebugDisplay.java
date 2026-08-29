package com.csse3200.game.cards.debug;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.csse3200.game.cards.effects.CardEffectResolution;
import com.csse3200.game.cards.effects.ResolvedCardEffect;
import com.csse3200.game.ui.UIComponent;

/**
 * Displays a live table of this turn's resolved card effects — card, effect type, target, value,
 * duration. Toggled by {@link KeyboardCardEffectDebugInputComponent} (backtick).
 *
 * <p>This is read-only, same as the rest of Team 5's resolution pipeline: it shows what has
 * already been calculated, it never applies effects to any entity itself.
 */
public class CardEffectDebugDisplay extends UIComponent {
  private static final float Z_INDEX = 10f;
  private CardEffectDebugComponent debug;
  private Window window;
  private Table rows;

  @Override
  public void create() {
    super.create();
    debug = entity.getComponent(CardEffectDebugComponent.class);
    addActors();
  }

  private void addActors() {
    window = new Window("Card effect resolution", skin);
    window.setPosition(20f, 20f);
    window.setSize(520f, 260f);
    window.setVisible(false);
    window.top().left();

    Table header = new Table();
    header.add(new Label("Card", skin)).width(120f);
    header.add(new Label("Effect", skin)).width(90f);
    header.add(new Label("Target", skin)).width(110f);
    header.add(new Label("Value", skin)).width(60f);
    header.add(new Label("Duration", skin)).width(70f);
    window.add(header).left().row();

    rows = new Table();
    window.add(rows).expand().fill().top();

    stage.addActor(window);
  }

  @Override
  public void draw(SpriteBatch batch) {
    boolean open = debug.isOpen();
    window.setVisible(open);
    if (open) {
      refreshRows();
    }
  }

  private void refreshRows() {
    rows.clear();
    var resolutions = debug.getResolutions();

    if (resolutions.isEmpty()) {
      rows.add(new Label("No cards played yet this turn", skin)).colspan(5).left();
      return;
    }

    for (CardEffectResolution resolution : resolutions) {
      for (ResolvedCardEffect effect : resolution.effects()) {
        rows.add(new Label(resolution.cardId(), skin)).width(120f);
        rows.add(new Label(effect.type().name(), skin)).width(90f);
        rows.add(new Label(effect.target().name(), skin)).width(110f);
        rows.add(new Label(String.valueOf(effect.value()), skin)).width(60f);
        rows.add(new Label(String.valueOf(effect.duration()), skin)).width(70f);
        rows.row();
      }
    }
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
    window.remove();
  }
}
