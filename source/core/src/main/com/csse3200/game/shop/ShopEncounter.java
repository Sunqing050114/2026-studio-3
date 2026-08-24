package com.csse3200.game.shop;

import com.csse3200.game.components.player.InventoryComponent;
import com.csse3200.game.maps.EncounterCallback;
import java.util.Collection;

/** Bridges shop purchase logic with the map encounter completion callback. */
public class ShopEncounter {
  public static final String DEFAULT_NODE_ID = "shop-encounter";

  private final String nodeId;
  private final InventoryComponent inventory;
  private final ShopService shopService;
  private final EncounterCallback callback;
  private boolean completed;
  private boolean completedSuccessfully;

  public ShopEncounter(InventoryComponent inventory, ShopService shopService) {
    this(DEFAULT_NODE_ID, inventory, shopService, null);
  }

  public ShopEncounter(
      String nodeId,
      InventoryComponent inventory,
      ShopService shopService,
      EncounterCallback callback) {
    this.nodeId = nodeId == null || nodeId.isBlank() ? DEFAULT_NODE_ID : nodeId;
    this.inventory = inventory;
    this.shopService = shopService == null ? new ShopService((ShopConfig) null) : shopService;
    this.callback = callback;
  }

  public PurchaseResult purchase(String itemId) {
    if (completed) {
      return PurchaseResult.failure(PurchaseResult.Status.SHOP_CLOSED, null);
    }

    return shopService.purchase(itemId, inventory);
  }

  public PurchaseResult canPurchase(String itemId) {
    if (completed) {
      return PurchaseResult.failure(PurchaseResult.Status.SHOP_CLOSED, null);
    }

    return shopService.canPurchase(itemId, inventory);
  }

  public Collection<ShopItem> getItems() {
    return shopService.getItems();
  }

  public String getNodeId() {
    return nodeId;
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean completedSuccessfully() {
    return completedSuccessfully;
  }

  /** Completes the shop encounter and reports completion to the map framework. */
  public void complete(boolean success) {
    if (completed) {
      return;
    }
    completed = true;
    completedSuccessfully = success;
    if (callback != null) {
      callback.onEncounterComplete(nodeId, success);
    }
  }
}
