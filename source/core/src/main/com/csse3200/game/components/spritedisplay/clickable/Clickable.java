// Clickable.java
package com.csse3200.game.components.spritedisplay.clickable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.components.Component;

public abstract class Clickable extends Component {
  // Shared default skin, only loaded if the record doesn't provide one.
  private static Skin defaultSkin;

  float x;
  float y;
  Button btn;
  Skin btnSkin;
  String trigger;
  float width;
  float height;

  private static Skin getDefaultSkin() {
    if (defaultSkin == null) {
      defaultSkin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));
    }
    return defaultSkin;
  }

  public Clickable(ClickableRecord record) {
    this.x = record.x();
    this.y = record.y();
    this.width = record.width();
    this.height = record.height();
    this.btnSkin = (record.btnSkin() != null) ? record.btnSkin() : getDefaultSkin();

    String text = record.text();
    String styleName = record.styleName();

    this.btn =
        switch (record.type()) {
          case TEXT ->
              (styleName != null)
                  ? new TextButton(text, btnSkin, styleName)
                  : new TextButton(text, btnSkin);
          case IMAGE ->
              (styleName != null) ? new ImageButton(btnSkin, styleName) : new ImageButton(btnSkin);
          case IMAGE_TEXT ->
              (styleName != null)
                  ? new ImageTextButton(text, btnSkin, styleName)
                  : new ImageTextButton(text, btnSkin);
        };

    init(record.trigger());
  }

  private void init(String trigger) {
    this.trigger = trigger;
    btn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger(trigger);
          }
        });
  }

  public Button getBtn() {
    return btn;
  }

  public Skin getBtnSkin() {
    return btnSkin;
  }

  public String getTrigger() {
    return trigger;
  }

  public float getY() {
    return y;
  }

  public float getX() {
    return x;
  }

  public float getWidth() {
    return width;
  }

  public float getHeight() {
    return height;
  }
}
