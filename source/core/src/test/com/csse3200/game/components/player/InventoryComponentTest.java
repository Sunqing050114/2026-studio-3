package com.csse3200.game.components.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class InventoryComponentTest {
  @Test
  void shouldSetGetGold() {
    InventoryComponent inventory = new InventoryComponent(100);
    assertEquals(100, inventory.getGold());

    inventory.setGold(150);
    assertEquals(150, inventory.getGold());

    inventory.setGold(-50);
    assertEquals(0, inventory.getGold());
  }

  @Test
  void shouldCheckHasGold() {
    InventoryComponent inventory = new InventoryComponent(150);
    assertTrue(inventory.hasGold(100));
    assertFalse(inventory.hasGold(200));
  }

  @Test
  void shouldAddGold() {
    InventoryComponent inventory = new InventoryComponent(100);
    inventory.addGold(-500);
    assertEquals(0, inventory.getGold());

    inventory.addGold(100);
    inventory.addGold(-20);
    assertEquals(80, inventory.getGold());
  }

  @Test
  void shouldSpendGoldOnlyWhenAmountIsValidAndAffordable() {
    InventoryComponent inventory = new InventoryComponent(30);

    assertFalse(inventory.hasGold(-1));
    assertFalse(inventory.spendGold(-1));
    assertEquals(30, inventory.getGold());

    assertTrue(inventory.spendGold(30));
    assertEquals(0, inventory.getGold());
    assertFalse(inventory.spendGold(1));
    assertEquals(0, inventory.getGold());
  }

  @Test
  void shouldTrackCards() {
    InventoryComponent inventory = new InventoryComponent(100);

    assertFalse(inventory.hasCard("card_heal"));
    assertTrue(inventory.addCard("card_heal"));
    assertTrue(inventory.addCard("card_heal", 2));

    assertTrue(inventory.hasCard("card_heal"));
    assertEquals(3, inventory.getCardCount("card_heal"));
    assertThrows(UnsupportedOperationException.class, () -> inventory.getCards().put("card", 1));
  }

  @Test
  void shouldRemoveOneCardAtATime() {
    InventoryComponent inventory = new InventoryComponent(100);
    inventory.addCard("card_heal", 2);

    assertTrue(inventory.removeCard("card_heal"));
    assertEquals(1, inventory.getCardCount("card_heal"));
    assertTrue(inventory.removeCard("card_heal"));
    assertEquals(0, inventory.getCardCount("card_heal"));
    assertFalse(inventory.removeCard("card_heal"));
  }

  @Test
  void shouldRejectInvalidCardAdditions() {
    InventoryComponent inventory = new InventoryComponent(100);

    assertFalse(inventory.addCard(""));
    assertFalse(inventory.addCard("card_heal", 0));
    assertFalse(inventory.addCard("card_heal", -1));

    assertEquals(0, inventory.getCardCount("card_heal"));
  }

  @Test
  void shouldPurchaseCardOnlyWhenAffordableAndValid() {
    InventoryComponent inventory = new InventoryComponent(25);

    assertTrue(inventory.purchaseCard("card_heal", 20));
    assertEquals(5, inventory.getGold());
    assertEquals(1, inventory.getCardCount("card_heal"));

    assertFalse(inventory.purchaseCard("card_shield", 20));
    assertFalse(inventory.purchaseCard("", 1));
    assertEquals(5, inventory.getGold());
    assertEquals(0, inventory.getCardCount("card_shield"));
  }
}
