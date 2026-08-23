package com.csse3200.game.components.combat;

import java.util.EnumMap;
import java.util.Map;

/**
 * Helper class for deciding which phase transitions are allowed.
 */
public class BattleTransitions {
    private final Map<BattlePhase, Map<BattleEvent, BattlePhase>> allowedTransitions =
            new EnumMap<>(BattlePhase.class);

    public BattleTransitions() {
        this.addTransition(
                BattlePhase.SETUP,
                BattleEvent.INTENTS_REVEALED,
                BattlePhase.PLAYER_START
        );

        this.addTransition(
                BattlePhase.PLAYER_START,
                BattleEvent.ENEMIES_DEFEATED,
                BattlePhase.VICTORY
        );

        this.addTransition(
                BattlePhase.PLAYER_START,
                BattleEvent.PLAYER_DEFEATED,
                BattlePhase.DEFEAT
        );

        this.addTransition(
                BattlePhase.PLAYER_TURN,
                BattleEvent.PLAYER_ATTACK_SELECTED,
                BattlePhase.PLAYER_RESOLVED
        );

        this.addTransition(
                BattlePhase.PLAYER_TURN,
                BattleEvent.PLAYER_DEFEND_SELECTED,
                BattlePhase.PLAYER_RESOLVED
        );

        this.addTransition(
                BattlePhase.PLAYER_TURN,
                BattleEvent.PLAYER_OTHER_SELECTED,
                BattlePhase.PLAYER_RESOLVED
        );

        this.addTransition(
                BattlePhase.PLAYER_TURN,
                BattleEvent.PLAYER_TURN_ENDED,
                BattlePhase.PLAYER_RESOLVED
        );

        this.addTransition(
                BattlePhase.PLAYER_TURN,
                BattleEvent.PLAYER_END_REQUESTED,
                BattlePhase.PLAYER_END
        );

        this.addTransition(
                BattlePhase.PLAYER_END,
                BattleEvent.PLAYER_DEFEATED,
                BattlePhase.DEFEAT
        );

        this.addTransition(
                BattlePhase.PLAYER_END,
                BattleEvent.ENEMIES_DEFEATED,
                BattlePhase.VICTORY
        );

        this.addTransition(
                BattlePhase.PLAYER_END,
                BattleEvent.PLAYER_TURN_ENDED,
                BattlePhase.ENEMY_TURN
        );

        this.addTransition(
                BattlePhase.ENEMY_TURN,
                BattleEvent.ENEMY_ATTACK_SELECTED,
                BattlePhase.ENEMY_RESOLVED
        );
    }

    /**
     * Helper function that adds allowed transitions to the transition table.
     */
    private void addTransition(BattlePhase currentPhase, BattleEvent incomingEvent,
                               BattlePhase resultingPhase) {
        Map<BattleEvent, BattlePhase> transitions = allowedTransitions.get(currentPhase);

        allowedTransitions
                .computeIfAbsent(
                        currentPhase,
                        ignored -> new EnumMap<>(BattleEvent.class))
                .put(incomingEvent, resultingPhase);
        transitions.put(incomingEvent, resultingPhase);
    }
}
