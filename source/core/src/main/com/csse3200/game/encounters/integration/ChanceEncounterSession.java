package com.csse3200.game.encounters.integration;

import com.csse3200.game.chance.ChanceEncounter;
import com.csse3200.game.chance.ChanceOutcome;
import java.util.Objects;

/** Coordinates choice resolution, player updates, and map completion for one Chance Encounter. */
public final class ChanceEncounterSession {
  private final Integer nodeId;
  private final ChanceEncounter encounter;
  private final ChanceOutcomeApplier outcomeApplier;
  private final EncounterCompletionCallback completionCallback;

  private ChanceResolution resolution;
  private boolean completed;

  public ChanceEncounterSession(
      Integer nodeId,
      ChanceEncounter encounter,
      ChanceOutcomeApplier outcomeApplier,
      EncounterCompletionCallback completionCallback) {
    if (nodeId == null) {
      throw new IllegalArgumentException("nodeId cannot be null");
    }
    this.nodeId = nodeId;
    this.encounter = Objects.requireNonNull(encounter, "encounter cannot be null");
    this.outcomeApplier = Objects.requireNonNull(outcomeApplier, "outcomeApplier cannot be null");
    this.completionCallback = completionCallback;
  }

  public ChanceResolution resolveChoice(String choiceId) {
    if (completed) {
      return outcomeApplier.failure(
          ChanceResolution.Status.ENCOUNTER_CLOSED, null, "This encounter has already ended.");
    }
    if (resolution != null && resolution.isSuccess()) {
      return outcomeApplier.failure(
          ChanceResolution.Status.ALREADY_RESOLVED,
          resolution.getOutcome(),
          "A choice has already been resolved.");
    }

    ChanceOutcome outcome = encounter.resolveChoice(choiceId);
    if (outcome == null) {
      return outcomeApplier.failure(
          ChanceResolution.Status.INVALID_CHOICE, null, "The selected choice does not exist.");
    }

    ChanceResolution attempt = outcomeApplier.apply(outcome);
    if (attempt.isSuccess()) {
      resolution = attempt;
    }
    return attempt;
  }

  public boolean complete() {
    if (completed || resolution == null || !resolution.isSuccess()) {
      return false;
    }
    completed = true;
    if (completionCallback != null) {
      completionCallback.onEncounterComplete(nodeId, true);
    }
    return true;
  }

  public boolean cancel() {
    if (completed) {
      return false;
    }
    completed = true;
    if (completionCallback != null) {
      completionCallback.onEncounterComplete(nodeId, false);
    }
    return true;
  }

  public Integer getNodeId() {
    return nodeId;
  }

  public ChanceEncounter getEncounter() {
    return encounter;
  }

  public ChanceResolution getResolution() {
    return resolution;
  }

  public boolean isResolved() {
    return resolution != null && resolution.isSuccess();
  }

  public boolean isCompleted() {
    return completed;
  }
}
