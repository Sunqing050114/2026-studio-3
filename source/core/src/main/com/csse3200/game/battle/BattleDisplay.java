package com.csse3200.game.components.battle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.components.combat.BattlePhase;
import com.csse3200.game.ui.UIComponent;

public class BattleDisplay extends UIComponent{
    private Table root;
    private Label turn;
    private TextButton attackCard; /// to be changed to actual card later on
    private TextButton defendCard; /// same as above
    private TextButton endTurnButton;
    private  Label phaseLabel;

    @Override
    public void create() {
        super.create();
        createActors();
        registerEvents();
    }
    ///TODO implement the UI for actions and link event handlers and triggers for actions
    ///
    private void createActors() {
        root = new Table();
        root.setFillParent(true);
    }
    private void registerEvents() { ///Handle aiden's phase change
        entity.getEvents().addListener("phaseChange", (BattlePhase phase) -> updatePhase(phase));
    }
    /// Currently used as a method to check player turns to handle disabling cards and
    private void updatePhase(BattlePhase phase) {
        phaseLabel.setText("Phase: " + phase);
    }
    @Override
    public void draw(SpriteBatch batch) {

    }

    public void dispose() {
        root.remove();
        super.dispose();
    }
}

