// ClickableRecord.java
package com.csse3200.game.components.spritedisplay.clickable;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public record ClickableRecord(
    String text,
    Skin btnSkin,
    float x,
    float y,
    String styleName,
    String trigger,
    ButtonType type) {

  public enum ButtonType {
    TEXT,
    IMAGE,
    IMAGE_TEXT
  }

  // ImageTextButton, custom style
  public ClickableRecord(
      String text, Skin btnSkin, float x, float y, String styleName, String trigger) {
    this(text, btnSkin, x, y, styleName, trigger, ButtonType.IMAGE_TEXT);
  }

  // ImageTextButton, default style
  public ClickableRecord(String text, Skin btnSkin, float x, float y, String trigger) {
    this(text, btnSkin, x, y, null, trigger, ButtonType.IMAGE_TEXT);
  }

  // ImageButton, custom style
  public ClickableRecord(Skin btnSkin, float x, float y, String styleName, String trigger) {
    this(null, btnSkin, x, y, styleName, trigger, ButtonType.IMAGE);
  }

  // ImageButton, default style
  public ClickableRecord(Skin btnSkin, float x, float y, String trigger) {
    this(null, btnSkin, x, y, null, trigger, ButtonType.IMAGE);
  }

  // TextButton, custom style, default skin
  public ClickableRecord(String text, float x, float y, String styleName, String trigger) {
    this(text, null, x, y, styleName, trigger, ButtonType.TEXT);
  }

  // TextButton, default style, default skin
  public ClickableRecord(String text, float x, float y, String trigger) {
    this(text, null, x, y, null, trigger, ButtonType.TEXT);
  }
}
