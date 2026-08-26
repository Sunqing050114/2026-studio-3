// HealthDisplay.java
package com.csse3200.game.components.spritedisplay.displaying;

public class HealthDisplay extends Displaying {

  public HealthDisplay(DisplayingRecord record) {
    super(record);
  }

  @Override
  public void onTrigger(Object payload) {
    if (payload instanceof Integer) {
      int health = (Integer) payload;
      label.setText("Health: " + health);
    } else {
      label.setText("Health: ?");
    }
  }
}
