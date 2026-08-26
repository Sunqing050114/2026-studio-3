package com.csse3200.game.components.player;

import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Component for managing player energy and handling turn lifecycle triggers. */
public class EnergyComponent extends Component {
  private static final Logger logger = LoggerFactory.getLogger(EnergyComponent.class);

  private static final String EVT_UPDATE_ENERGY = "updateEnergy";
  private static final String EVT_UPDATE_MAX_ENERGY = "updateMaxEnergy";

  private int currentEnergy;
  private int maxEnergy;

  public EnergyComponent(int maxEnergy) {
    this.maxEnergy = maxEnergy;
    this.currentEnergy = maxEnergy;
  } // 初始化默认能量上限

  // --- Getters & Setters ---
  public int getCurrentEnergy() {
    return currentEnergy;
  }

  private void notifyEnergyChange() {
    if (entity != null) {
      entity.getEvents().trigger(EVT_UPDATE_ENERGY, this.currentEnergy);
    }
  }

  public void setCurrentEnergy(int currentEnergy) {
    if (currentEnergy >= 0) {
      this.currentEnergy = Math.min(currentEnergy, this.maxEnergy);
    } else {
      this.currentEnergy = 0;
    }
    notifyEnergyChange();
  }

  public int getMaxEnergy() {
    return maxEnergy;
  }

  public void setMaxEnergy(int maxEnergy) {
    this.maxEnergy = Math.max(maxEnergy, 1);
    if (entity != null) {
      entity.getEvents().trigger(EVT_UPDATE_MAX_ENERGY, this.maxEnergy);
    }
  }

  // --- Team 5 (Card System) integration stubs ---

  public boolean canAfford(int amount) {
    return currentEnergy >= amount;
  }

  /** 供卡牌 UI 判断某张牌当前能不能点 */
  public boolean spendEnergy(int amount) {
    if (amount < 0) {
      logger.warn("Attempted to spend negative energy: {}", amount);
      return false;
    }
    if (!canAfford(amount)) {
      logger.debug("Not enough energy: have {}, need {}", currentEnergy, amount);
      return false;
    }
    this.currentEnergy -= amount;
    notifyEnergyChange();
    return true;
  }

  /** 真正扣能量的方法 */
  public void restoreEnergy(int amount) {
    if (amount < 0) {
      logger.warn("Attempted to restore negative energy: {}", amount);
      return;
    }
    this.currentEnergy = Math.min(currentEnergy + amount, maxEnergy);
    notifyEnergyChange();
  }

  /** 给能量加值但不超过上限，用于打出某张牌返还1点能量等效果，和"回合重置到满"不同 */

  // --- Team 3 (Battle/Turn System) lifecycle hooks ---

  public void onTurnStart() {
    this.currentEnergy = maxEnergy;
    notifyEnergyChange();
  }

  /** 回合开始时把能量重置为满 */
  public void onTurnEnd() {
    // Intentionally left as a stub pending Team 3 alignment on
    // whether any end-of-turn energy behavior is needed.
  }
}
