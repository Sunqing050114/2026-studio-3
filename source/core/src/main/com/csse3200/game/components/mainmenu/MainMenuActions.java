package com.csse3200.game.components.mainmenu;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class listens to events relevant to the Main Menu Screen and does something when one of the
 * events is triggered.
 */
public class MainMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuActions.class);
  private GdxGame game;

  public MainMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("start", this::onStart);
    entity.getEvents().addListener("load", this::onLoad);
    entity.getEvents().addListener("exit", this::onExit);
    entity.getEvents().addListener("settings", this::onSettings);
  }

  /** Starts a new run, which begins on the map. */
  private void onStart() {
    logger.info("Start game");
    game.getRunState().endRun();
    game.setScreen(GdxGame.ScreenType.MAP);
  }

  /** Returns to a run that is still in progress. Saving to disk is not implemented. */
  private void onLoad() {
    logger.info("Load game");

    if (game.getRunState().isRunActive()) {
      game.setScreen(GdxGame.ScreenType.MAP);
    }
  }

  /** Exits the game. */
  private void onExit() {
    logger.info("Exit game");
    game.exit();
  }

  /** Swaps to the Settings screen. */
  private void onSettings() {
    logger.info("Launching settings screen");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }
}
