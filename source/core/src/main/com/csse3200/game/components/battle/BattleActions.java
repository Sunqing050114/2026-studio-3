package com.csse3200.game.components.battle;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.combat.BattleController;
import com.csse3200.game.components.combat.BattleEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class BattleActions extends Component {
    private static final Logger logger = LoggerFactory.getLogger(BattleActions.class);
    public String actionRequested = "battleActionRequested";
    public String statusChanged =  "BattlestatusChanged";
    private BattleController controller;
    public BattleActions(BattleController game) {
        this.controller = game;
    }
    @Override
    public void create() {
        entity
                .getEvents()
                .addListener("attackCardSelected", this::selectAttack);

        entity
                .getEvents()
                .addListener("defendCardSelected", this::selectDefend);
    }

    private void selectAttack() {
        BattleEvent event = BattleEvent.PLAYER_ATTACK_SELECTED;

        if (controller.canHandle(event)) {
            controller.handle(event);
        }
    }

    private void selectDefend() {
        BattleEvent event = BattleEvent.PLAYER_DEFEND_SELECTED;

        if (controller.canHandle(event)) {
            controller.handle(event);
        }
    }
}

