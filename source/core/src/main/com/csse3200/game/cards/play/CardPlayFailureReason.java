package com.csse3200.game.cards.play;

/** Reason a card play request could not be completed. */
public enum CardPlayFailureReason {
  /** The card was played successfully. */
  NONE,

  /** The requested card is not currently in the battle deck hand. */
  CARD_NOT_IN_HAND,

  /** Team 7's energy component rejected the card cost. */
  INSUFFICIENT_ENERGY
}
