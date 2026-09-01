package com.csse3200.game.encounters.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.chance.ChanceChoice;
import com.csse3200.game.chance.ChanceEncounter;
import com.csse3200.game.chance.ChanceOutcome;
import java.util.List;
import org.junit.jupiter.api.Test;

class EncounterFlowControllerIntegerIdTest {
  @Test
  void shouldCompleteExactlyOnceUsingIntegerMapNodeId() {
    TestPlayer player = new TestPlayer(80, 25);
    CountingCallback callback = new CountingCallback();
    EncounterFlowController controller =
        new EncounterFlowController(player, new NoopShopTransactions(player), callback);
    ChanceEncounter encounter =
        new ChanceEncounter(
            "well",
            "A quiet well",
            List.of(new ChanceChoice("drink", "Drink", new ChanceOutcome(5, 0))));

    ChanceEncounterSession session = controller.startChance(42, encounter);
    assertTrue(session.resolveChoice("drink").isSuccess());
    assertTrue(session.complete());
    assertFalse(session.complete());

    assertEquals(85, player.getHealth());
    assertEquals(1, callback.count);
    assertEquals(42, callback.nodeId);
    assertTrue(callback.success);
    assertFalse(controller.isEncounterActive());
  }

  @Test
  void shouldIgnoreStaleCompletionForDifferentNode() {
    TestPlayer player = new TestPlayer(80, 25);
    CountingCallback callback = new CountingCallback();
    EncounterFlowController controller =
        new EncounterFlowController(player, new NoopShopTransactions(player), callback);
    ChanceEncounter encounter =
        new ChanceEncounter(
            "well",
            "A quiet well",
            List.of(new ChanceChoice("leave", "Leave", new ChanceOutcome(0, 0))));

    controller.startChance(7, encounter);
    controller.onEncounterComplete(8, true);

    assertTrue(controller.isEncounterActive());
    assertEquals(0, callback.count);
  }

  private static final class CountingCallback implements EncounterCompletionCallback {
    private int count;
    private Integer nodeId;
    private boolean success;

    @Override
    public void onEncounterComplete(Integer nodeId, boolean success) {
      count++;
      this.nodeId = nodeId;
      this.success = success;
    }
  }

  private static final class TestPlayer implements PlayerStateGateway {
    private int health;
    private int currency;

    private TestPlayer(int health, int currency) {
      this.health = health;
      this.currency = currency;
    }

    @Override
    public int getHealth() {
      return health;
    }

    @Override
    public void setHealth(int health) {
      this.health = health;
    }

    @Override
    public int getCurrency() {
      return currency;
    }

    @Override
    public void setCurrency(int currency) {
      this.currency = currency;
    }
  }

  private static final class NoopShopTransactions implements ShopTransactionGateway {
    private final TestPlayer player;

    private NoopShopTransactions(TestPlayer player) {
      this.player = player;
    }

    @Override
    public int getCurrency() {
      return player.getCurrency();
    }

    @Override
    public ShopTransactionStatus validatePurchase(String cardId, int price) {
      return ShopTransactionStatus.READY;
    }

    @Override
    public ShopTransactionStatus purchaseCard(String cardId, int price) {
      return ShopTransactionStatus.SUCCESS;
    }
  }
}
