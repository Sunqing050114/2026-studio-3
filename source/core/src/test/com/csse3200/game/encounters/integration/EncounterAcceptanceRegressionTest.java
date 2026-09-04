package com.csse3200.game.encounters.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.encounters.integration.mocks.MockCardCatalogGateway;
import com.csse3200.game.encounters.integration.mocks.MockDeckGateway;
import com.csse3200.game.encounters.integration.mocks.MockPlayerStateGateway;
import com.csse3200.game.maps.EncounterCallback;
import com.csse3200.game.shop.PurchaseResult;
import com.csse3200.game.shop.ShopEncounter;
import com.csse3200.game.shop.ShopItem;
import com.csse3200.game.shop.ShopService;
import org.junit.jupiter.api.Test;

/** Sprint 2 regression coverage for the final encounter lifecycle and shop transaction boundary. */
class EncounterAcceptanceRegressionTest {
  @Test
  void duplicateShopCompletionShouldNotifyMapOnlyOnce() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 50);
    MockDeckGateway deck = new MockDeckGateway();
    RecordingCallback mapCallback = new RecordingCallback();
    EncounterFlowController flow = createFlow(player, deck, mapCallback);
    ShopEncounter shop = flow.startShop(7, new ShopService(new ShopItem[0]));

    shop.complete(true);
    shop.complete(false);
    flow.onEncounterComplete(7, true);

    assertEquals(1, mapCallback.completionCount);
    assertEquals(7, mapCallback.nodeId);
    assertTrue(mapCallback.success);
    assertFalse(flow.isEncounterActive());
  }

  @Test
  void insufficientFundsShouldNotChangeMoneyDeckOrStock() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 10);
    MockDeckGateway deck = new MockDeckGateway();
    ShopService shop = createShop(2);
    IntegratedShopTransactionGateway transactions = createTransactions(player, deck);

    PurchaseResult result = shop.purchaseWithGateway("heal-offer", transactions);

    assertFalse(result.isSuccess());
    assertEquals(PurchaseResult.Status.INSUFFICIENT_GOLD, result.getStatus());
    assertEquals(10, player.getCurrency());
    assertEquals(0, deck.getCardIds().size());
    assertEquals(2, shop.getItem("heal-offer").stock);
  }

  @Test
  void rejectedDeckUpdateShouldNotChangeMoneyDeckOrStock() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 50);
    MockDeckGateway deck = new MockDeckGateway();
    deck.setFailAdd(true);
    ShopService shop = createShop(2);
    IntegratedShopTransactionGateway transactions = createTransactions(player, deck);

    PurchaseResult result = shop.purchaseWithGateway("heal-offer", transactions);

    assertFalse(result.isSuccess());
    assertEquals(PurchaseResult.Status.TRANSACTION_FAILED, result.getStatus());
    assertEquals(50, player.getCurrency());
    assertEquals(0, deck.getCardIds().size());
    assertEquals(2, shop.getItem("heal-offer").stock);
  }

  private EncounterFlowController createFlow(
      MockPlayerStateGateway player, MockDeckGateway deck, EncounterCallback mapCallback) {
    return new EncounterFlowController(player, createTransactions(player, deck), mapCallback);
  }

  private IntegratedShopTransactionGateway createTransactions(
      MockPlayerStateGateway player, MockDeckGateway deck) {
    return new IntegratedShopTransactionGateway(
        player, new MockCardCatalogGateway("card_heal"), deck);
  }

  private ShopService createShop(int stock) {
    return new ShopService(
        new ShopItem[] {new ShopItem("heal-offer", "card_heal", "Heal", 20, stock)});
  }

  private static final class RecordingCallback implements EncounterCallback {
    private int completionCount;
    private Integer nodeId;
    private boolean success;

    @Override
    public void onEncounterComplete(Integer nodeId, boolean success) {
      completionCount++;
      this.nodeId = nodeId;
      this.success = success;
    }
  }
}
