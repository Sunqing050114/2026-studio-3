package com.csse3200.game.components.battle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.ui.UIComponent;

/** Shows the win/lose result of a battle and a way back to the main menu. */
public class EndBattleDisplay extends UIComponent {
  private final GdxGame game;
  private final boolean won;
  private Table table;

  public EndBattleDisplay(GdxGame game, boolean won) {
    this.game = game;
    this.won = won;
  }

  @Override
  public void create() {
    super.create();
    table = new Table();
    table.setFillParent(true);

    Label heading = new Label(won ? "VICTORY" : "DEFEAT", skin);
    heading.setFontScale(2f);

    Label subheading = new Label(won ? "You won the battle." : "You were defeated.", skin);

    TextButton menuButton = new TextButton("Return to Menu", skin);
    menuButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            game.setScreen(GdxGame.ScreenType.MAIN_MENU);
          }
        });

    table.add(heading).padBottom(20f);
    table.row();
    table.add(subheading).padBottom(40f);
    table.row();
    table.add(menuButton);
    stage.addActor(table);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Rendered by the stage.
  }

  @Override
  public void dispose() {
    if (table != null) {
      table.clear();
    }
    super.dispose();
  }
}
