package com.csse3200.game.components.combat;

/**
 * The central controller of the current state of the battle loop.
 * Controls what phase the battle is currently in, what actions are currently
 * allowed and what the illegal and legal transitions are.
 */
public class BattleController {
    private BattlePhase phase = BattlePhase.SETUP;
    private int currentEnemyIndex = -1;

    private void handle(BattleEvent event) {
    }
}