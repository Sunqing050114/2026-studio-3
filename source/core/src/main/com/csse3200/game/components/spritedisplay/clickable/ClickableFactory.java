// ClickableFactory.java
package com.csse3200.game.components.spritedisplay.clickable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
      String skinAtlas = entry.getString("skinAtlas", null);
      JsonValue sizeArray = entry.get("size");
      float width = -1;
      float height = -1;
      if (sizeArray != null) {
        width = sizeArray.getFloat(0);
        height = sizeArray.getFloat(1);
      }
      float x = entry.getFloat("x");
      float y = entry.getFloat("y");
      String styleName = entry.getString("styleName", null);
      String trigger = entry.getString("trigger");
      ClickableRecord.ButtonType type = inferType(text, skinFile);

      Skin skin = getOrLoadSkin(skinFile, skinAtlas);
      records.add(new ClickableRecord(text, skin, x, y, styleName, trigger, type, width, height));
    }
  }

  public ClickableFactory(List<ClickableRecord> records) {
    this.records.addAll(records);
  }

  private ClickableRecord.ButtonType inferType(String text, String skinFile) {
    if (text == null) {
      return ClickableRecord.ButtonType.IMAGE;
    }
    if (skinFile != null) {
      return ClickableRecord.ButtonType.IMAGE_TEXT;
    }
    return ClickableRecord.ButtonType.TEXT;
  }

  private Skin getOrLoadSkin(String skinFile, String skinAtlas) {
    if (skinFile == null && skinAtlas == null) {
      return null;
    }

    String cacheKey = skinFile + "|" + skinAtlas;
    return skinCache.computeIfAbsent(
        cacheKey,
        key -> {
          if (skinFile != null && skinAtlas != null) {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(skinAtlas));
            return new Skin(Gdx.files.internal(skinFile), atlas);
          } else if (skinFile != null) {
            return new Skin(Gdx.files.internal(skinFile));
          } else {
            return new Skin(new TextureAtlas(Gdx.files.internal(skinAtlas)));
          }
        });
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

      if (clickable.getWidth() > 0 && clickable.getHeight() > 0) {
        btn.setSize(clickable.getWidth(), clickable.getHeight());
      }
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
