package com.csse3200.game.components.player;

import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Component for managing player energy and handling turn lifecycle triggers.
 */
public class EnergyComponent extends Component {
    private static final Logger logger = LoggerFactory.getLogger(EnergyComponent.class);

    private static final String EVT_UPDATE_ENERGY = "updateEnergy";
    private static final String EVT_UPDATE_MAX_ENERGY = "updateMaxEnergy";

    private int currentEnergy;
    private int maxEnergy;

    public EnergyComponent(int maxEnergy) {
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
    }

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
}

// --- API Stubs for  ---
