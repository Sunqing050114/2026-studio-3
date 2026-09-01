package com.csse3200.game.encounters.integration;

/** Callback used by Team 2 encounters to report completion with Team 4 integer node identifiers. */
@FunctionalInterface
public interface EncounterCompletionCallback {
  /**
   * Reports the result of one completed encounter.
   *
   * @param nodeId Team 4 map node identifier
   * @param success whether the encounter completed successfully
   */
  void onEncounterComplete(Integer nodeId, boolean success);
}
