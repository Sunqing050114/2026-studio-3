package  com.csse3200.game.Systems;

import com.csse3200.game.components.enemy.EnemyIntent;
import com.csse3200.game.components.enemy.EnemyStatsComponent;
import com.csse3200.game.entities.Entity;

//Just make simple decisions about an enemy`s next intent
// and this is  an initial implementation . An enemy attacks normally and defends.
//when its health is low.

public  class  EnemyAIDecisionSystem{
        private  static final float LOW_HEALTH_RATIO = 0.3f;
        private  static final int DEFEND_ARMOUR =3;
        // Since we don't have much info to go on at this stage, I've just made up some numbers for now.
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

 //It's clear that this doesn't fully align with the ECS architecture yet, so I'll be making some adjustments.