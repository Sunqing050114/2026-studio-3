package com.csse3200.game.Systems;

import com.csse3200.game.components.enemy.EnemyIntent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;

/**
 * Makes simple decisions about an enemy's next intent.
 *
 * <p>This is an initial implementation. An enemy attacks normally and defends when its health is
 * low.
 */
public class EnemyAIDecisionSystem {
    private static final float LOW_HEALTH_RATIO = 0.3f;
    private static final int DEFEND_ARMOUR = 3;

    /**
     * Decides the next intent of an enemy.
     *
     * @param enemy enemy whose intent should be decided
     * @return the selected enemy intent
     */
    public EnemyIntent decideIntent(Entity enemy) {
        if (enemy == null) {
            return EnemyIntent.unknown();
        }

        EnemyStatsComponent stats = enemy.getComponent(EnemyStatsComponent.class);

        if (stats == null || !stats.isAlive()) {
            return EnemyIntent.unknown();
        }

        if (isLowHealth(stats)) {
            return EnemyIntent.defend(DEFEND_ARMOUR);
        }

        return EnemyIntent.attack(stats.getBaseAttack());
    }

    private boolean isLowHealth(EnemyStatsComponent stats) {
        return stats.getHealth() <= stats.getMaxHealth() * LOW_HEALTH_RATIO;
    }
}