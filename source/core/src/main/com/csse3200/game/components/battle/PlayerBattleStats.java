package com.csse3200.game.components.battle;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.badlogic.gdx.graphics.Color;

/** A UI component that displays the player's stats in the Battle.*/
public class PlayerBattleStats extends UIComponent {

    private Table table;
    private Image heartImage;
    private Label healthLabel;

    private void addActors() {
        table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.padTop(45f).padLeft(5f);
//
//        // Heart image
//        float heartSideLength = 30f;
//        heartImage =
//                new Image(ServiceLocator.getResourceService().getAsset("images/heart.png", Texture.class));

        // Health text
        CharSequence healthText = "Label here";
        healthLabel = new Label(healthText, skin, "large");

//        table.add(heartImage).size(heartSideLength).pad(5);
        table.add(healthLabel);
        stage.addActor(table);
    }

    public void updatePlayerHealthUI(int health) {
        CharSequence text = String.format("Health: %d", health);
        healthLabel.setText(text);
    }

    @Override
    public void create() {
        super.create();
        addActors();
//        table = new Table();
//        table.setFillParent(true);
    }

    @Override
    protected void draw(SpriteBatch batch) {
        //Draw
    }

    @Override
    public void dispose() {
        table.remove();
        super.dispose();
    }
}
