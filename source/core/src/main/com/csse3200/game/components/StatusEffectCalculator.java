package com.csse3200.game.components;


public final class StatusEffectCalculator {
    private static final String FEEBLE="FEEBLE";
    private static final String VULNERABLE="VULNERABLE";
    private static final String POISON="POISON";

    private static final float FEEBLE_MULTIPLIER=0.75f;

    private static final float VULNERABLE_MULTIPLIER=1.5f;

    private StatusEffectCalculator() {}
        public static float getOutgoingDamageModifier (CombatStatsComponent stats){
            StatusEffect feeble = stats.getStatusEffect(FEEBLE);
            if (feeble != null) {
                return FEEBLE_MULTIPLIER;
            }
            return 1.0f;
        }

        public static float getIncomingDamageModifier (CombatStatsComponent stats){
            StatusEffect vulnerable = stats.getStatusEffect(VULNERABLE);
            if (vulnerable != null) {
                return VULNERABLE_MULTIPLIER;
            }
            return 1.0f;
        }


        public static int getPoisonDamage (CombatStatsComponent stats){
            StatusEffect poison = stats.getStatusEffect(POISON);
            if (poison == null) {
                return 0;
            }

        return Math.max(poison.getValue(), 0);
    }
}

