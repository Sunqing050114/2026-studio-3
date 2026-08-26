// ClickableFactory.java
package com.csse3200.game.components.spritedisplay.clickable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.ui.UIComponent;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickableFactory extends UIComponent {

  private final List<ClickableRecord> records = new ArrayList<>();
  private final List<Clickable> clickables = new ArrayList<>();
  private final Map<String, Skin> skinCache = new HashMap<>();

  public ClickableFactory(Path file) {
    JsonValue root = new JsonReader().parse(Gdx.files.internal(file.toString()));
    JsonValue clickableArray = root.get("Clickable");

    for (JsonValue entry : clickableArray) {
      String text = entry.getString("text", null);
      String skinFile = entry.getString("skinFile", null);
      float x = entry.getFloat("x");
      float y = entry.getFloat("y");
      String styleName = entry.getString("styleName", null);
      String trigger = entry.getString("trigger");
      ClickableRecord.ButtonType type = parseType(entry.getString("type", "imageText"));

      Skin skin = getOrLoadSkin(skinFile);
      records.add(new ClickableRecord(text, skin, x, y, styleName, trigger, type));
    }
  }

  public ClickableFactory(List<ClickableRecord> records) {
    this.records.addAll(records);
  }

  private ClickableRecord.ButtonType parseType(String type) {
    return switch (type) {
      case "text" -> ClickableRecord.ButtonType.TEXT;
      case "image" -> ClickableRecord.ButtonType.IMAGE;
      default -> ClickableRecord.ButtonType.IMAGE_TEXT;
    };
  }

  private Skin getOrLoadSkin(String skinFile) {
    if (skinFile == null) {
      return null;
    }
    return skinCache.computeIfAbsent(skinFile, f -> new Skin(Gdx.files.internal(f)));
  }

  @Override
  public void create() {
    super.create();
    for (ClickableRecord record : records) {
      Clickable clickable = new Clickable(record) {};
      clickable.setEntity(this.entity);

      clickables.add(clickable);
      stage.addActor(clickable.getBtn());
    }
  }

  @Override
  protected void draw(SpriteBatch batch) {
    int screenHeight = Gdx.graphics.getHeight();
    for (Clickable clickable : clickables) {
      Button btn = clickable.getBtn();
      btn.setPosition(clickable.getX(), screenHeight - clickable.getY());
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    for (Clickable clickable : clickables) {
      clickable.getBtn().remove();
    }
  }
}
