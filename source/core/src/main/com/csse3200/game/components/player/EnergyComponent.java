package com.csse3200.game.components.player;

import com.csse3200.game.components.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Component for managing player energy and handling turn lifecycle triggers.
 */
public class EnergyComponent {
    private static final Logger logger = LoggerFactory.getLogger(EnergyComponent.class);
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

    public void setCurrentEnergy(int currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

}

