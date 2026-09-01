package com.csse3200.game.cards.play.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.csse3200.game.cards.EffectType;
import com.csse3200.game.cards.TargetType;
import com.csse3200.game.cards.effects.CardEffectResolution;
import com.csse3200.game.cards.effects.ResolvedCardEffect;
import com.csse3200.game.cards.play.CardPlayFailureReason;
import com.csse3200.game.cards.play.CardPlayResult;
import com.csse3200.game.cards.play.CardPlayTarget;
import com.csse3200.game.cards.play.DeckSnapshot;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class CardPlayResultDispatcherTest {
  @Test
  void shouldNotDispatchFailedCardPlayResults() {
    AtomicInteger calls = new AtomicInteger();
    CardPlayResultDispatcher dispatcher =
        new CardPlayResultDispatcher(
            (target, effects) -> calls.incrementAndGet(), effects -> calls.incrementAndGet());
    CardPlayResult failed =
        CardPlayResult.failure(
            "strike",
            CardPlayTarget.singleEnemy("enemy-1"),
            1,
            CardPlayFailureReason.NOT_ENOUGH_ENERGY,
            DeckSnapshot.empty());

    dispatcher.dispatch(failed);

    assertEquals(0, calls.get());
  }

  @Test
  void shouldDispatchEnemyAndPlayerPartsOfSuccessfulResultOnce() {
    AtomicInteger enemyCalls = new AtomicInteger();
    AtomicInteger playerCalls = new AtomicInteger();
    CardPlayResultDispatcher dispatcher =
        new CardPlayResultDispatcher(
            (target, effects) -> enemyCalls.incrementAndGet(),
            effects -> playerCalls.incrementAndGet());
    CardEffectResolution resolution =
        new CardEffectResolution(
            "mixed",
            List.of(
                new ResolvedCardEffect(
                    "mixed", EffectType.DAMAGE, TargetType.SINGLE_ENEMY, 4, 0, 0),
                new ResolvedCardEffect("mixed", EffectType.BLOCK, TargetType.SELF, 3, 0, 1)));
    CardPlayResult success =
        CardPlayResult.success(
            "mixed", CardPlayTarget.singleEnemy("enemy-1"), 1, resolution, DeckSnapshot.empty());

    dispatcher.dispatch(success);

    assertEquals(1, enemyCalls.get());
    assertEquals(1, playerCalls.get());
  }
}
