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
    StatusEffect effect = new StatusEffect("VULNERABLE", 2, 2);
    assertEquals("VULNERABLE", effect.getType());
    assertEquals(2, effect.getValue());
    assertEquals(2, effect.getDuration());
  }

  @Test
  void statusEffectShouldExpireAfterDurationReachesZero() {
    StatusEffect effect = new StatusEffect("VULNERABLE", 2, 2);
    assertFalse(effect.tickAndCheckExpired());
    assertTrue(effect.tickAndCheckExpired());
  }

  @Test
  void statusEffectWithZeroDurationShouldBePermanent() {
    // e.g. STRENGTH, which Team 6 defines with duration=0 and lasts the whole combat.
    StatusEffect effect = new StatusEffect("STRENGTH", 2, 0);
    assertFalse(effect.tickAndCheckExpired());
    assertFalse(effect.tickAndCheckExpired());
    assertEquals(0, effect.getDuration());
  }

  // -----------------------------------------------------------------
  // Armor on CombatStatsComponent (permanent pool)
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
  void damageShouldHitHealthDirectlyWhenNoArmorOrBlock() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.takeDamage(30);
    assertEquals(0, combat.getArmor());
    assertEquals(70, combat.getHealth());
  }

  // -----------------------------------------------------------------
  // Block on CombatStatsComponent (per-turn pool)
  // -----------------------------------------------------------------

  @Test
  void shouldSetGetBlock() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    assertEquals(0, combat.getBlock());
    combat.setBlock(10);
    assertEquals(10, combat.getBlock());
    combat.setBlock(-5);
    assertEquals(0, combat.getBlock());
  }

  @Test
  void shouldAddBlock() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addBlock(5);
    assertEquals(5, combat.getBlock());
    combat.addBlock(-100);
    assertEquals(5, combat.getBlock());
  }

  @Test
  void resetBlockShouldClearBlockRegardlessOfValue() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addBlock(15);
    combat.resetBlock();
    assertEquals(0, combat.getBlock());
  }

  @Test
  void blockShouldAbsorbDamageBeforeHealthWhenNoArmor() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addBlock(5);
    combat.takeDamage(8);
    assertEquals(0, combat.getBlock());
    assertEquals(97, combat.getHealth());
  }

  @Test
  void blockShouldFullyAbsorbDamageWhenSufficient() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addBlock(10);
    combat.takeDamage(6);
    assertEquals(4, combat.getBlock());
    assertEquals(100, combat.getHealth());
  }

  // -----------------------------------------------------------------
  // Block + Armor combined in takeDamage()
  // -----------------------------------------------------------------

  @Test
  void blockShouldAbsorbBeforeArmor() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addBlock(3);
    combat.addArmor(4);
    combat.takeDamage(10);
    // 10 damage -> block absorbs 3, remaining 7 -> armor absorbs 4, remaining 3 -> health -3
    assertEquals(0, combat.getBlock());
    assertEquals(0, combat.getArmor());
    assertEquals(97, combat.getHealth());
  }

  @Test
  void blockAndArmorTogetherShouldFullyAbsorbDamageWhenSufficient() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.addBlock(5);
    combat.addArmor(10);
    combat.takeDamage(8);
    // 8 damage -> block absorbs all 8, armor untouched
    assertEquals(0, combat.getBlock());
    assertEquals(7, combat.getArmor());
    assertEquals(100, combat.getHealth());
  }

  // -----------------------------------------------------------------
  // Status effect management on CombatStatsComponent
  // -----------------------------------------------------------------

  @Test
  void shouldApplyAndQueryStatusEffect() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    assertFalse(combat.hasStatusEffect("VULNERABLE"));

    StatusEffect vulnerable = new StatusEffect("VULNERABLE", 2, 2);
    combat.applyStatusEffect(vulnerable);

    assertTrue(combat.hasStatusEffect("VULNERABLE"));
    assertEquals(vulnerable, combat.getStatusEffect("VULNERABLE"));
  }

  @Test
  void shouldApplyStatusEffectUsingThreeArgOverload() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.applyStatusEffect("VULNERABLE", 2, 2);

    assertTrue(combat.hasStatusEffect("VULNERABLE"));
    assertEquals(2, combat.getStatusEffect("VULNERABLE").getValue());
    assertEquals(2, combat.getStatusEffect("VULNERABLE").getDuration());
  }

  @Test
  void applyingSameTypeShouldOverwritePrevious() {
    // Design decision: overwrite, not stack. See applyStatusEffect() javadoc -
    // pending confirmation with Team 5/6 if card design expects stacking behaviour instead.
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.applyStatusEffect(new StatusEffect("VULNERABLE", 2, 2));
    combat.applyStatusEffect(new StatusEffect("VULNERABLE", 2, 5));

    assertEquals(5, combat.getStatusEffect("VULNERABLE").getDuration());
  }

  @Test
  void shouldExplicitlyRemoveStatusEffect() {
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.applyStatusEffect(new StatusEffect("VULNERABLE", 2, 2));
    combat.removeStatusEffect("VULNERABLE");

    assertFalse(combat.hasStatusEffect("VULNERABLE"));
    assertNull(combat.getStatusEffect("VULNERABLE"));
  }

  @Test
  void statusEffectShouldBeAutoRemovedAfterDurationExpires() {
    // Simulates updateStatusEffects() being called once per turn.
    // NOTE: actually wiring this to a real turn event depends on Team 3 (not done yet) -
    // this test only verifies the tick/expire mechanism itself works correctly.
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.applyStatusEffect(new StatusEffect("VULNERABLE", 2, 2));

    combat.updateStatusEffects();
    assertTrue(combat.hasStatusEffect("VULNERABLE"));

    combat.updateStatusEffects();
    assertFalse(combat.hasStatusEffect("VULNERABLE"));
  }

  @Test
  void permanentStatusEffectShouldNotBeRemovedByUpdateStatusEffects() {
    // e.g. STRENGTH, which Team 6 defines with duration=0 and lasts the whole combat.
    CombatStatsComponent combat = new CombatStatsComponent(100, 20);
    combat.applyStatusEffect(new StatusEffect("STRENGTH", 2, 0));

    combat.updateStatusEffects();
    assertTrue(combat.hasStatusEffect("STRENGTH"));

    combat.updateStatusEffects();
    assertTrue(combat.hasStatusEffect("STRENGTH"));
  }

  // NOTE: tests that previously verified a specific effect (e.g. "vulnerable" modifying
  // incoming damage by a hard-coded formula) have been removed. takeDamage() no longer applies
  // any status-effect-based damage modification - that calculation is owned by whichever caller
  // needs it (e.g. Team 5), not by CombatStatsComponent. See takeDamage() javadoc.
}
