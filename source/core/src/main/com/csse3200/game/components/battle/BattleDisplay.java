package com.csse3200.game.components.battle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.components.combat.BattlePhase;
import com.csse3200.game.ui.UIComponent;

/** Displays the current battle phase and placeholder controls for player actions. */
public class BattleDisplay extends UIComponent {
  private Table root;
  private TextButton attackButton;
  private TextButton defendButton;
  private TextButton endTurnButton;
  private Label phaseLabel;

  @Override
  public void create() {
    super.create();
    createActors();
    registerEvents();
  }

  private void createActors() {
    root = new Table();
    root.setFillParent(true);
    phaseLabel = new Label("Phase: SETUP", skin);
    attackButton = new TextButton("Attack Card", skin);
    defendButton = new TextButton("Defend Card", skin);
    endTurnButton = new TextButton("End Turn", skin);
    Label hpLabel = new Label("HP", skin);

    root.add(phaseLabel);
    root.add(defendButton);
    root.add(endTurnButton);
    root.add(hpLabel);
    root.row();
    root.row();
    root.add(attackButton);
    stage.addActor(root);
  }

  private void registerEvents() {
    entity
        .getEvents()
        .addListener(BattleActions.PHASE_CHANGED_EVENT, (BattlePhase phase) -> updatePhase(phase));
    registerButtonEvent(attackButton, BattleActions.ATTACK_SELECTED_EVENT);
    registerButtonEvent(defendButton, BattleActions.DEFEND_SELECTED_EVENT);
    registerButtonEvent(endTurnButton, BattleActions.END_TURN_SELECTED_EVENT);
  }

  private void registerButtonEvent(TextButton button, String eventName) {
    button.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            entity.getEvents().trigger(eventName);
          }
        });
  }

  private void updatePhase(BattlePhase phase) {
    phaseLabel.setText("Phase: " + phase);
  }

  @Override
  public void draw(SpriteBatch batch) {}

  @Override
  public void dispose() {
    root.remove();
    super.dispose();
  }
}
