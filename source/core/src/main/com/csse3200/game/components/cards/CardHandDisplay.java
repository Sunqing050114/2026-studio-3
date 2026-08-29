package com.csse3200.game.components.cards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.ui.UIComponent;
import java.util.List;

/** Displays a small toggle button and a demo hand of cards for Team 5 deck UI work. */
public class CardHandDisplay extends UIComponent {
  private static final float CARD_WIDTH = 112f;
  private static final float CARD_HEIGHT = 168f;
  private static final List<DemoCard> DEMO_HAND =
      List.of(
          new DemoCard("Strike", "1", "Deal 6 damage.", new Color(0.74f, 0.18f, 0.16f, 1f)),
          new DemoCard("Defend", "1", "Gain 5 block.", new Color(0.20f, 0.42f, 0.72f, 1f)),
          new DemoCard(
              "Poison Dagger",
              "1",
              "Deal 4 damage.\nApply 3 poison.",
              new Color(0.20f, 0.52f, 0.25f, 1f)),
          new DemoCard(
              "Expose",
              "1",
              "Apply 2 vulnerable\nto all enemies.",
              new Color(0.86f, 0.56f, 0.16f, 1f)),
          new DemoCard("Inner Focus", "2", "Gain 2 strength.", new Color(0.47f, 0.28f, 0.73f, 1f)),
          new DemoCard("Bandage", "1", "Heal 6 health.", new Color(0.78f, 0.32f, 0.48f, 1f)));

  private Table rootTable;
  private Table handPanel;
  private boolean handVisible;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.top().right();
    rootTable.padTop(56f).padRight(10f);

    TextButton cardsButton = new TextButton("Cards", skin);
    cardsButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            toggleHand();
          }
        });

    handPanel = createHandPanel();
    handPanel.setVisible(false);

    rootTable.add(cardsButton).width(96f).height(40f).right();
    rootTable.row();
    rootTable.add(handPanel).right().padTop(8f);

    stage.addActor(rootTable);
  }

  private Table createHandPanel() {
    Table panel = new Table();
    panel.defaults().pad(6f);
    panel.setBackground(skin.newDrawable("white", new Color(0.08f, 0.07f, 0.06f, 0.88f)));

    Label title = new Label("Current Hand", skin, "large");
    title.setColor(Color.WHITE);
    panel.add(title).left().colspan(DEMO_HAND.size());
    panel.row();

    for (DemoCard card : DEMO_HAND) {
      panel.add(createCard(card)).width(CARD_WIDTH).height(CARD_HEIGHT);
    }

    return panel;
  }

  private Table createCard(DemoCard card) {
    Table cardTable = new Table();
    cardTable.top();
    cardTable.pad(8f);
    cardTable.setBackground(skin.newDrawable("white", new Color(0.95f, 0.91f, 0.78f, 1f)));

    Label cost = new Label(card.cost, skin, "large");
    cost.setColor(Color.WHITE);
    Table costBadge = new Table();
    costBadge.setBackground(skin.newDrawable("white", card.accent));
    costBadge.add(cost).center();

    Label name = new Label(card.name, skin, "small");
    name.setColor(Color.BLACK);
    name.setWrap(true);

    Label type = new Label("DEMO CARD", skin, "small");
    type.setColor(card.accent);

    Label description = new Label(card.description, skin, "small");
    description.setColor(Color.BLACK);
    description.setWrap(true);

    cardTable.add(costBadge).size(34f).left();
    cardTable.row();
    cardTable.add(name).width(CARD_WIDTH - 18f).padTop(10f);
    cardTable.row();
    cardTable.add(type).width(CARD_WIDTH - 18f).padTop(6f);
    cardTable.row();
    cardTable.add(description).width(CARD_WIDTH - 18f).expandY().top().padTop(14f);

    return cardTable;
  }

  private void toggleHand() {
    handVisible = !handVisible;
    handPanel.setVisible(handVisible);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Stage draws this UI component.
  }

  @Override
  public float getZIndex() {
    return 3f;
  }

  @Override
  public void dispose() {
    if (rootTable != null) {
      rootTable.remove();
    }
    super.dispose();
  }

  private static class DemoCard {
    private final String name;
    private final String cost;
    private final String description;
    private final Color accent;

    private DemoCard(String name, String cost, String description, Color accent) {
      this.name = name;
      this.cost = cost;
      this.description = description;
      this.accent = accent;
    }
  }
}
