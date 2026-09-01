package com.csse3200.game.shop;

import com.csse3200.game.encounters.integration.EncounterCompletionCallback;
import com.csse3200.game.encounters.integration.ShopTransactionGateway;
import java.util.Collection;

/** Bridges shop purchase logic with the encounter completion callback. */
public class ShopEncounter {
  private final Integer nodeId;
  private final ShopTransactionGateway transactions;
  private final ShopService shopService;
  private final EncounterCompletionCallback callback;
  private boolean completed;
  private boolean completedSuccessfully;

  public ShopEncounter(
      Integer nodeId,
      ShopService shopService,
      ShopTransactionGateway transactions,
      EncounterCompletionCallback callback) {
    if (nodeId == null) {
      throw new IllegalArgumentException("nodeId cannot be null");
    }
    this.nodeId = nodeId;
    this.transactions = transactions;
    this.shopService = shopService == null ? new ShopService((ShopConfig) null) : shopService;
    this.callback = callback;
  }

  public PurchaseResult purchase(String itemId) {
    if (completed) {
      return PurchaseResult.failure(PurchaseResult.Status.SHOP_CLOSED, null);
    }
    return shopService.purchaseWithGateway(itemId, transactions);
  }

  public PurchaseResult canPurchase(String itemId) {
    if (completed) {
      return PurchaseResult.failure(PurchaseResult.Status.SHOP_CLOSED, null);
    }
    return shopService.canPurchaseWithGateway(itemId, transactions);
  }

  public Collection<ShopItem> getItems() {
    return shopService.getItems();
  }

  public Integer getNodeId() {
    return nodeId;
  }

  public Integer getCurrency() {
    return transactions == null ? null : transactions.getCurrency();
  }

  public boolean isCompleted() {
    return completed;
  }

  public boolean completedSuccessfully() {
    return completedSuccessfully;
  }

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
