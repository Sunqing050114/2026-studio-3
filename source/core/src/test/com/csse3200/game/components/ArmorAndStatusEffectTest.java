package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class ArmorAndStatusEffectTest {

    // -----------------------------------------------------------------
    // StatusEffect class itself
    // -----------------------------------------------------------------

    @Test
    void shouldStoreInitialStatusEffectValues() {
        StatusEffect effect = new StatusEffect("vulnerable", 0.5f, 2);
        assertEquals("vulnerable", effect.getEffectId());
        assertEquals(0.5f, effect.getEffectValue());
        assertEquals(2, effect.getDuration());
    }

    @Test
    void statusEffectShouldExpireAfterDurationReachesZero() {
        StatusEffect effect = new StatusEffect("vulnerable", 0.5f, 2);
        assertFalse(effect.tickAndCheckExpired());
        assertTrue(effect.tickAndCheckExpired());
    }

    // -----------------------------------------------------------------
    // Armor on CombatStatsComponent
    // -----------------------------------------------------------------

    @Test
    void shouldSetGetArmor() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        assertEquals(0, combat.getArmor());
        combat.setArmor(10);
        assertEquals(10, combat.getArmor());
        combat.setArmor(-5);
        assertEquals(0, combat.getArmor());
    }

    @Test
    void shouldAddArmor() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.addArmor(5);
        assertEquals(5, combat.getArmor());
        combat.addArmor(-100);
        assertEquals(5, combat.getArmor());
    }

    @Test
    void shouldClearArmor() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.addArmor(15);
        combat.clearArmor();
        assertEquals(0, combat.getArmor());
    }

    @Test
    void armorShouldAbsorbDamageBeforeHealth() {
        // Example from the issue: 5 armor, then 8 damage -> armor 0, health -3 from 100 = 97
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.addArmor(5);
        combat.takeDamage(8);
        assertEquals(0, combat.getArmor());
        assertEquals(97, combat.getHealth());
    }

    @Test
    void armorShouldFullyAbsorbDamageWhenSufficient() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.addArmor(10);
        combat.takeDamage(6);
        assertEquals(4, combat.getArmor());
        assertEquals(100, combat.getHealth());
    }

    @Test
    void damageShouldHitHealthDirectlyWhenNoArmor() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.takeDamage(30);
        assertEquals(0, combat.getArmor());
        assertEquals(70, combat.getHealth());
    }

    // -----------------------------------------------------------------
    // Status effect management on CombatStatsComponent
    // -----------------------------------------------------------------

    @Test
    void shouldApplyAndQueryStatusEffect() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        assertFalse(combat.hasStatusEffect("vulnerable"));

        StatusEffect vulnerable = new StatusEffect("vulnerable", 0.5f, 2);
        combat.applyStatusEffect(vulnerable);

        assertTrue(combat.hasStatusEffect("vulnerable"));
        assertEquals(vulnerable, combat.getStatusEffect("vulnerable"));
    }

    @Test
    void applyingSameEffectIdShouldOverwritePrevious() {
        // Design decision: overwrite, not stack. See applyStatusEffect() javadoc -
        // pending confirmation with Team 5/6 if card design expects stacking instead.
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.applyStatusEffect(new StatusEffect("vulnerable", 0.5f, 2));
        combat.applyStatusEffect(new StatusEffect("vulnerable", 0.5f, 5));

        assertEquals(5, combat.getStatusEffect("vulnerable").getDuration());
    }

    @Test
    void shouldExplicitlyRemoveStatusEffect() {
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.applyStatusEffect(new StatusEffect("vulnerable", 0.5f, 2));
        combat.removeStatusEffect("vulnerable");

        assertFalse(combat.hasStatusEffect("vulnerable"));
        assertNull(combat.getStatusEffect("vulnerable"));
    }

    @Test
    void statusEffectShouldBeAutoRemovedAfterDurationExpires() {
        // Simulates updateStatusEffects() being called once per turn.
        // NOTE: actually wiring this to a real turn event depends on Team 3 (not done yet) -
        // this test only verifies the tick/expire mechanism itself works correctly.
        CombatStatsComponent combat = new CombatStatsComponent(100, 20);
        combat.applyStatusEffect(new StatusEffect("vulnerable", 0.5f, 2));

        combat.updateStatusEffects();
        assertTrue(combat.hasStatusEffect("vulnerable"));

        combat.updateStatusEffects();
        assertFalse(combat.hasStatusEffect("vulnerable"));
    }

    // NOTE: tests that previously verified a specific effect (e.g. "vulnerable" modifying
    // incoming damage by a hard-coded formula) have been removed. takeDamage() no longer applies
    // any status-effect-based damage modification - that calculation is owned by whichever caller
    // needs it (e.g. Team 5), not by CombatStatsComponent. See takeDamage() javadoc.
}