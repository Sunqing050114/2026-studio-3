package com.csse3200.game.cards.play.integration;

import com.csse3200.game.cards.CardService;
import com.csse3200.game.cards.TargetType;
import com.csse3200.game.cards.configs.CardConfig;
import com.csse3200.game.cards.play.CardPlayRequest;
import com.csse3200.game.cards.play.CardPlayResult;
import com.csse3200.game.cards.play.CardPlayService;
import com.csse3200.game.components.Component;

/**
 * Connects Team 3's existing {@code playCard(cardId, targetId)} event to Team 5's unified API.
 *
 * <p>Attach this component to the same battle-flow entity that receives Team 3's card UI events.
 * The component emits one {@code cardPlayResult} event for UI feedback after every attempt.
 */
public final class Team3CardPlayAdapter extends Component {
  public static final String PLAY_CARD_EVENT = "playCard";
  public static final String CARD_PLAY_RESULT_EVENT = "cardPlayResult";

  private final CardService cardService;
  private final CardPlayService cardPlayService;
  private final CardPlayResultDispatcher resultDispatcher;

  /** Creates an adapter that returns results without applying them to Team 1 or Team 7. */
  public Team3CardPlayAdapter(CardService cardService, CardPlayService cardPlayService) {
    this(cardService, cardPlayService, null);
  }

  /** Creates an adapter that also forwards successful effects to Team 1 and Team 7. */
  public Team3CardPlayAdapter(
      CardService cardService,
      CardPlayService cardPlayService,
      CardPlayResultDispatcher resultDispatcher) {
    if (cardService == null) {
      throw new IllegalArgumentException("Card service cannot be null");
    }
    if (cardPlayService == null) {
      throw new IllegalArgumentException("Card play service cannot be null");
    }
    this.cardService = cardService;
    this.cardPlayService = cardPlayService;
    this.resultDispatcher = resultDispatcher;
  }

  @Override
  public void create() {
    entity.getEvents().addListener(PLAY_CARD_EVENT, this::onCardPlayed);
  }

  private void onCardPlayed(String cardId, String targetId) {
    CardPlayResult result = cardPlayService.playCard(toRequest(cardId, targetId));
    if (resultDispatcher != null) {
      resultDispatcher.dispatch(result);
    }
    entity.getEvents().trigger(CARD_PLAY_RESULT_EVENT, result);
  }

  private CardPlayRequest toRequest(String cardId, String targetId) {
    CardConfig card = cardService.getCard(cardId).orElse(null);
    if (card == null) {
      return CardPlayRequest.self(cardId);
    }
    if (card.target == TargetType.SELF) {
      return CardPlayRequest.self(cardId);
    }
    if (card.target == TargetType.ALL_ENEMIES) {
      return CardPlayRequest.allEnemies(cardId);
    }
    return CardPlayRequest.singleEnemy(cardId, targetId);
  }
}
