package com.csse3200.game.components.battle;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;

public class BattleActions extends Component {
  private GdxGame game;

  public BattleActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("battle", this::onStart);
    entity.getEvents().addListener("exit", this::onExit);
  }

  private void onStart() {
    game.setScreen(GdxGame.ScreenType.BATTLE_SCREEN);
  }

  private void onExit() {
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }
}
