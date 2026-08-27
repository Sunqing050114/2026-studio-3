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
    float height,
    String variant) {

  public enum ButtonType {
    TEXT,
    IMAGE,
    IMAGE_TEXT
  }

  private static final float NO_SIZE = -1;
  private static final String DEFAULT_VARIANT = "Clickable";

  public ClickableRecord {
    if (variant == null) {
      variant = DEFAULT_VARIANT;
    }
  }

  public boolean hasSize() {
    return width != NO_SIZE && height != NO_SIZE;
  }

  public static Builder builder(String trigger) {
    return new Builder(trigger);
  }

  public static final class Builder {
    private String text;
    private Skin btnSkin;
    private float x;
    private float y;
    private String styleName;
    private final String trigger;
    private float width = NO_SIZE;
    private float height = NO_SIZE;
    private String variant = DEFAULT_VARIANT;

    private Builder(String trigger) {
      this.trigger = trigger;
    }

    public Builder text(String text) {
      this.text = text;
      return this;
    }

    public Builder skin(Skin btnSkin) {
      this.btnSkin = btnSkin;
      return this;
    }

    public Builder position(float x, float y) {
      this.x = x;
      this.y = y;
      return this;
    }

    public Builder styleName(String styleName) {
      this.styleName = styleName;
      return this;
    }

    public Builder size(float width, float height) {
      this.width = width;
      this.height = height;
      return this;
    }

    public Builder variant(String variant) {
      this.variant = variant;
      return this;
    }

    public ClickableRecord build() {
      ButtonType type = inferType(text, btnSkin);
      return new ClickableRecord(
          text, btnSkin, x, y, styleName, trigger, type, width, height, variant);
    }

    private static ButtonType inferType(String text, Skin btnSkin) {
      if (text == null) {
        return ButtonType.IMAGE;
      }
      if (btnSkin != null) {
        return ButtonType.IMAGE_TEXT;
      }
      return ButtonType.TEXT;
    }
  }
}
