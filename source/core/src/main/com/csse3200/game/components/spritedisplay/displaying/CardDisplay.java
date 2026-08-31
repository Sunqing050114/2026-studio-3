package com.csse3200.game.components.spritedisplay.displaying;

import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class CardDisplay extends Displaying {

  private Label label;

  public CardDisplay(DisplayingRecord record) {
    super(record);
    this.label = getLabel(); // Get the label from the superclass
  }

  @Override
  public void create() {
    super.create();

    // Cards are now dynamic (see CardService), so we can't hardcode listeners per card
    // trigger. Instead, EnemyDropTargetComponent fires one generic "cardPlayed" event with
    // the card's display name whenever ANY card is successfully dropped — that works for
    // any card without this class needing to know what cards exist.
    entity.getEvents().addListener("cardPlayed", this::onCardPlayed);
  }

  private void onCardPlayed(String cardLabel) {
    updateLabel(cardLabel + " played!");
  }

  private void updateLabel(String text) {
    label.setText(text);
    label.setVisible(true);
  }
}
