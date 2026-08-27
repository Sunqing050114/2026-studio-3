package com.csse3200.game.components.player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/** A ui component for displaying player stats, e.g. health. */
public class PlayerStatsDisplay extends UIComponent {
  Table table;
  private Image heartImage;
  private Label healthLabel;
  private Image energyImage;
  private Label energyLabel;
  private Image pietyImage;
  private Label pietyLabel;

  /** Creates reusable ui styles and adds actors to the stage. */
  @Override
  public void create() {
    super.create();
    addActors();

    entity.getEvents().addListener("updateHealth", this::updatePlayerHealthUI);
    entity.getEvents().addListener("updateEnergy", this::updatePlayerEnergyUI);
    entity.getEvents().addListener("updatePiety", this::updatePlayerPietyUI);
  }

  /**
   * Creates actors and positions them on the stage using a table.
   *
   * @see Table for positioning options
   */
  private void addActors() {
    table = new Table();
    table.top().left();
    table.setFillParent(true);
    table.padTop(45f).padLeft(5f);

    // Image size
    float imageSideLength = 30f;

    // Heart image
    heartImage =
        new Image(ServiceLocator.getResourceService().getAsset("images/heart.png", Texture.class));

    // Health text
    int health = entity.getComponent(CombatStatsComponent.class).getHealth();
    CharSequence healthText = String.format("Health: %d", health);
    healthLabel = new Label(healthText, skin, "large");

    // Energy image
    energyImage =
        new Image(ServiceLocator.getResourceService().getAsset("images/energy.png", Texture.class));

    //Energy text
    int energy = 100;
    EnergyComponent component = entity.getComponent(EnergyComponent.class);
    if (energy != 100) {
      energy = component.getCurrentEnergy();
    }
    CharSequence energyText = String.format("Energy: %d", energy);
    energyLabel = new Label(energyText, skin, "large");

    //Piety image
    pietyImage =
        new Image(ServiceLocator.getResourceService().getAsset("images/piety.png", Texture.class));

    //Piety text
    pietyLabel = new Label("Piety: ", skin, "large");

    table.add(heartImage).size(imageSideLength).pad(5);
    table.add(healthLabel);
    table.row();

    table.add(energyImage).size(imageSideLength).pad(5);
    table.add(energyLabel).left();
    table.row();
    table.add(pietyImage).size(imageSideLength).pad(5);
    table.add(pietyLabel).left();
    stage.addActor(table);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  /**
   * Updates the player's health on the ui.
   *
   * @param health player health
   */
  public void updatePlayerHealthUI(int health) {
    CharSequence text = String.format("Health: %d", health);
    healthLabel.setText(text);
  }

  /**
   * Updates the player's energy on the ui.
   *
   * @param energy player energy
   */
  public void updatePlayerEnergyUI(int energy) {
    CharSequence text = String.format("Energy: %d", energy);
    energyLabel.setText(text);
  }

  /**
   * Updates the player's piety on the ui.
   *
   * @param piety player piety
   */
  public void updatePlayerPietyUI(int piety) {
    CharSequence text = String.format("Piety: %d", piety);
    pietyLabel.setText(text);
  }


  @Override
  public void dispose() {
    super.dispose();
    heartImage.remove();
    healthLabel.remove();
    energyImage.remove();
    energyLabel.remove();
    pietyImage.remove();
    pietyLabel.remove();
  }
}
