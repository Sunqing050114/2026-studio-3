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
    ButtonType type,
    float width,
    float height) {

  public enum ButtonType {
    TEXT,
    IMAGE,
    IMAGE_TEXT
  }

  private static final float NO_SIZE = -1;

  // ImageTextButton, custom style, with size
  public ClickableRecord(
      String text,
      Skin btnSkin,
      float x,
      float y,
      String styleName,
      String trigger,
      float width,
      float height) {
    this(text, btnSkin, x, y, styleName, trigger, ButtonType.IMAGE_TEXT, width, height);
  }

  // ImageTextButton, custom style, no size
  public ClickableRecord(
      String text, Skin btnSkin, float x, float y, String styleName, String trigger) {
    this(text, btnSkin, x, y, styleName, trigger, ButtonType.IMAGE_TEXT, NO_SIZE, NO_SIZE);
  }

  // ImageTextButton, default style, with size
  public ClickableRecord(
      String text, Skin btnSkin, float x, float y, String trigger, float width, float height) {
    this(text, btnSkin, x, y, null, trigger, ButtonType.IMAGE_TEXT, width, height);
  }

  // ImageTextButton, default style, no size
  public ClickableRecord(String text, Skin btnSkin, float x, float y, String trigger) {
    this(text, btnSkin, x, y, null, trigger, ButtonType.IMAGE_TEXT, NO_SIZE, NO_SIZE);
  }

  // ImageButton, custom style, with size
  public ClickableRecord(
      Skin btnSkin, float x, float y, String styleName, String trigger, float width, float height) {
    this(null, btnSkin, x, y, styleName, trigger, ButtonType.IMAGE, width, height);
  }

  // ImageButton, custom style, no size
  public ClickableRecord(Skin btnSkin, float x, float y, String styleName, String trigger) {
    this(null, btnSkin, x, y, styleName, trigger, ButtonType.IMAGE, NO_SIZE, NO_SIZE);
  }

  // ImageButton, default style, with size
  public ClickableRecord(
      Skin btnSkin, float x, float y, String trigger, float width, float height) {
    this(null, btnSkin, x, y, null, trigger, ButtonType.IMAGE, width, height);
  }

  // ImageButton, default style, no size
  public ClickableRecord(Skin btnSkin, float x, float y, String trigger) {
    this(null, btnSkin, x, y, null, trigger, ButtonType.IMAGE, NO_SIZE, NO_SIZE);
  }

  // TextButton, custom style, default skin, with size
  public ClickableRecord(
      String text, float x, float y, String styleName, String trigger, float width, float height) {
    this(text, null, x, y, styleName, trigger, ButtonType.TEXT, width, height);
  }

  // TextButton, custom style, default skin, no size
  public ClickableRecord(String text, float x, float y, String styleName, String trigger) {
    this(text, null, x, y, styleName, trigger, ButtonType.TEXT, NO_SIZE, NO_SIZE);
  }

  // TextButton, default style, default skin, with size
  public ClickableRecord(String text, float x, float y, String trigger, float width, float height) {
    this(text, null, x, y, null, trigger, ButtonType.TEXT, width, height);
  }

  // TextButton, default style, default skin, no size
  public ClickableRecord(String text, float x, float y, String trigger) {
    this(text, null, x, y, null, trigger, ButtonType.TEXT, NO_SIZE, NO_SIZE);
  }

  public boolean hasSize() {
    return width != NO_SIZE && height != NO_SIZE;
  }
}
