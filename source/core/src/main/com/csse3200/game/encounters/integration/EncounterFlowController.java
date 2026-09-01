package com.csse3200.game.encounters.integration;

import com.csse3200.game.chance.ChanceEncounter;
import com.csse3200.game.shop.ShopEncounter;
import com.csse3200.game.shop.ShopService;
import java.util.Objects;

/** Coordinates one active Map -> Encounter -> Map lifecycle using Team 4 integer node IDs. */
public final class EncounterFlowController implements EncounterCompletionCallback {
  public enum EncounterType {
    CHANCE,
    SHOP
  }

  private final ChanceOutcomeApplier chanceOutcomeApplier;
  private final ShopTransactionGateway shopTransactions;
  private final EncounterCompletionCallback mapCallback;

  private Integer activeNodeId;
  private EncounterType activeType;

  public EncounterFlowController(
      PlayerStateGateway player,
      ShopTransactionGateway shopTransactions,
      EncounterCompletionCallback mapCallback) {
    this.chanceOutcomeApplier = new ChanceOutcomeApplier(player);
    this.shopTransactions =
        Objects.requireNonNull(shopTransactions, "shopTransactions cannot be null");
    this.mapCallback = Objects.requireNonNull(mapCallback, "mapCallback cannot be null");
  }

  public ChanceEncounterSession startChance(Integer nodeId, ChanceEncounter encounter) {
    begin(nodeId, EncounterType.CHANCE);
    try {
      return new ChanceEncounterSession(nodeId, encounter, chanceOutcomeApplier, this);
    } catch (RuntimeException exception) {
      clearActiveEncounter();
      throw exception;
    }
  }

  public ShopEncounter startShop(Integer nodeId, ShopService shopService) {
    begin(nodeId, EncounterType.SHOP);
    try {
      return new ShopEncounter(nodeId, shopService, shopTransactions, this);
    } catch (RuntimeException exception) {
      clearActiveEncounter();
      throw exception;
    }
  }

  @Override
  public void onEncounterComplete(Integer nodeId, boolean success) {
    if (activeNodeId == null || !activeNodeId.equals(nodeId)) {
      return;
    }
    clearActiveEncounter();
    mapCallback.onEncounterComplete(nodeId, success);
  }

  public boolean isEncounterActive() {
    return activeNodeId != null;
  }

  public Integer getActiveNodeId() {
    return activeNodeId;
  }

  public EncounterType getActiveType() {
    return activeType;
  }

  private void begin(Integer nodeId, EncounterType type) {
    if (isEncounterActive()) {
      throw new IllegalStateException(
          String.format("Encounter for node %s is already active", activeNodeId));
    }
    if (nodeId == null) {
      throw new IllegalArgumentException("nodeId cannot be null");
    }
    activeNodeId = nodeId;
    activeType = type;
  }

  private void clearActiveEncounter() {
    activeNodeId = null;
    activeType = null;
  }
}
