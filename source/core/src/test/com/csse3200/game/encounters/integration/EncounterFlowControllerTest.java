package com.csse3200.game.encounters.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.csse3200.game.chance.ChanceChoice;
import com.csse3200.game.chance.ChanceEncounter;
import com.csse3200.game.chance.ChanceOutcome;
import com.csse3200.game.encounters.integration.mocks.MockCardCatalogGateway;
import com.csse3200.game.encounters.integration.mocks.MockDeckGateway;
import com.csse3200.game.encounters.integration.mocks.MockPlayerStateGateway;
import com.csse3200.game.maps.MapGraph;
import com.csse3200.game.maps.MapNode;
import com.csse3200.game.maps.NodeState;
import com.csse3200.game.maps.RoomType;
import com.csse3200.game.shop.PurchaseResult;
import com.csse3200.game.shop.ShopEncounter;
import com.csse3200.game.shop.ShopItem;
import com.csse3200.game.shop.ShopService;
import java.util.List;
import org.junit.jupiter.api.Test;

class EncounterFlowControllerTest {
  @Test
  void shouldCompleteChanceAndShopRoundTrip() {
    MapGraph map = createMap();
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    MockDeckGateway deck = new MockDeckGateway();
    IntegratedShopTransactionGateway shopTransactions =
        new IntegratedShopTransactionGateway(player, new MockCardCatalogGateway("card_heal"), deck);
    EncounterFlowController flow = new EncounterFlowController(player, shopTransactions, map);

    ChanceEncounterSession chance = flow.startChance("chance", createChanceEncounter());
    ChanceResolution chanceResult = chance.resolveChoice("risk");
    assertTrue(chanceResult.isSuccess());
    assertTrue(chance.complete());

    assertEquals(90, player.getHealth());
    assertEquals(125, player.getCurrency());
    assertEquals(NodeState.COMPLETED, map.getNode("chance").getState());
    assertEquals(NodeState.AVAILABLE, map.getNode("shop").getState());
    assertFalse(flow.isEncounterActive());

    map.getNode("shop").setState(NodeState.CURRENT);
    ShopService shopService =
        new ShopService(new ShopItem[] {new ShopItem("heal-offer", "card_heal", "Heal", 30, 1)});
    ShopEncounter shop = flow.startShop("shop", shopService);
    PurchaseResult purchase = shop.purchase("heal-offer");
    shop.complete(true);

    assertTrue(purchase.isSuccess());
    assertEquals(95, player.getCurrency());
    assertEquals(1, deck.getCardCount("card_heal"));
    assertEquals(0, shopService.getItem("heal-offer").stock);
    assertEquals(NodeState.COMPLETED, map.getNode("chance").getState());
    assertEquals(NodeState.COMPLETED, map.getNode("shop").getState());
    assertEquals(NodeState.AVAILABLE, map.getNode("return").getState());
    assertFalse(flow.isEncounterActive());
  }

  @Test
  void shouldPreventTwoConcurrentEncounters() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    IntegratedShopTransactionGateway transactions =
        new IntegratedShopTransactionGateway(
            player, new MockCardCatalogGateway("card_heal"), new MockDeckGateway());
    EncounterFlowController flow =
        new EncounterFlowController(player, transactions, (nodeId, success) -> {});

    flow.startChance("chance", createChanceEncounter());

    assertThrows(
        IllegalStateException.class,
        () -> flow.startShop("shop", new ShopService(new ShopItem[0])));
  }

  @Test
  void shouldNotAdvanceMapWhenChanceIsCancelled() {
    MapGraph map = createMap();
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    IntegratedShopTransactionGateway transactions =
        new IntegratedShopTransactionGateway(
            player, new MockCardCatalogGateway("card_heal"), new MockDeckGateway());
    EncounterFlowController flow = new EncounterFlowController(player, transactions, map);

    ChanceEncounterSession chance = flow.startChance("chance", createChanceEncounter());
    assertTrue(chance.cancel());

    assertEquals(NodeState.CURRENT, map.getNode("chance").getState());
    assertEquals(NodeState.LOCKED, map.getNode("shop").getState());
    assertFalse(flow.isEncounterActive());
  }

  @Test
  void shouldRejectInvalidNodeIdsWithoutActivatingEncounter() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    IntegratedShopTransactionGateway transactions =
        new IntegratedShopTransactionGateway(
            player, new MockCardCatalogGateway("card_heal"), new MockDeckGateway());
    EncounterFlowController flow =
        new EncounterFlowController(player, transactions, (nodeId, success) -> {});

    assertThrows(
        IllegalArgumentException.class, () -> flow.startChance(null, createChanceEncounter()));
    assertThrows(
        IllegalArgumentException.class,
        () -> flow.startShop("   ", new ShopService(new ShopItem[0])));

    assertFalse(flow.isEncounterActive());
    assertNull(flow.getActiveNodeId());
    assertNull(flow.getActiveType());
  }

  @Test
  void shouldRecoverWhenChanceSessionConstructionFails() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    IntegratedShopTransactionGateway transactions =
        new IntegratedShopTransactionGateway(
            player, new MockCardCatalogGateway("card_heal"), new MockDeckGateway());
    EncounterFlowController flow =
        new EncounterFlowController(player, transactions, (nodeId, success) -> {});

    assertThrows(NullPointerException.class, () -> flow.startChance("broken", null));
    assertFalse(flow.isEncounterActive());

    ShopEncounter shop = flow.startShop("shop", new ShopService(new ShopItem[0]));
    assertTrue(flow.isEncounterActive());
    assertEquals("shop", flow.getActiveNodeId());
    shop.complete(true);
    assertFalse(flow.isEncounterActive());
  }

  @Test
  void shouldIgnoreStaleAndDuplicateCompletionCallbacks() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    IntegratedShopTransactionGateway transactions =
        new IntegratedShopTransactionGateway(
            player, new MockCardCatalogGateway("card_heal"), new MockDeckGateway());
    RecordingCallback callback = new RecordingCallback();
    EncounterFlowController flow = new EncounterFlowController(player, transactions, callback);

    flow.startShop("shop", new ShopService(new ShopItem[0]));
    flow.onEncounterComplete("stale-node", true);

    assertTrue(flow.isEncounterActive());
    assertEquals(0, callback.count);

    flow.onEncounterComplete("shop", true);
    flow.onEncounterComplete("shop", true);

    assertFalse(flow.isEncounterActive());
    assertEquals(1, callback.count);
    assertEquals("shop", callback.nodeId);
    assertTrue(callback.success);
  }

  @Test
  void shouldClearCurrentEncounterBeforeMapCallbackStartsNextEncounter() {
    MockPlayerStateGateway player = new MockPlayerStateGateway(100, 100);
    IntegratedShopTransactionGateway transactions =
        new IntegratedShopTransactionGateway(
            player, new MockCardCatalogGateway("card_heal"), new MockDeckGateway());
    EncounterFlowController[] flowHolder = new EncounterFlowController[1];
    flowHolder[0] =
        new EncounterFlowController(
            player,
            transactions,
            (nodeId, success) ->
                flowHolder[0].startShop("next-shop", new ShopService(new ShopItem[0])));

    ShopEncounter firstShop =
        flowHolder[0].startShop("first-shop", new ShopService(new ShopItem[0]));
    firstShop.complete(true);

    assertTrue(flowHolder[0].isEncounterActive());
    assertEquals("next-shop", flowHolder[0].getActiveNodeId());
    assertEquals(EncounterFlowController.EncounterType.SHOP, flowHolder[0].getActiveType());
  }

  private MapGraph createMap() {
    MapGraph map = new MapGraph();
    MapNode chance = new MapNode("chance", RoomType.EVENT);
    MapNode shop = new MapNode("shop", RoomType.SHOP);
    MapNode returnNode = new MapNode("return", RoomType.EVENT);
    chance.setState(NodeState.CURRENT);
    map.addNode(chance);
    map.addNode(shop);
    map.addNode(returnNode);
    map.connectNodes("chance", "shop");
    map.connectNodes("shop", "return");
    return map;
  }

  private ChanceEncounter createChanceEncounter() {
    return new ChanceEncounter(
        "shrine",
        "A shrine offers a risky bargain.",
        List.of(new ChanceChoice("risk", "Lose health for gold.", new ChanceOutcome(-10, 25))));
  }

  private static final class RecordingCallback implements com.csse3200.game.maps.EncounterCallback {
    private int count;
    private String nodeId;
    private boolean success;

    @Override
    public void onEncounterComplete(String nodeId, boolean success) {
      count++;
      this.nodeId = nodeId;
      this.success = success;
    }
  }
}
