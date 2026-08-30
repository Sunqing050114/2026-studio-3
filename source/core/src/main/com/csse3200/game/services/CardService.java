package com.csse3200.game.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.csse3200.game.cards.Card;
import com.csse3200.game.components.spritedisplay.clickable.ClickableRecord;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Owns the pool of every card that exists, and which cards are actually "in hand" for the
 * current round.
 *
 * <p>This is a first working stub, not a full deck-builder: {@link #startNewRound(int)} just
 * shuffles the whole pool and deals {@code handSize} cards, with no discard pile, no per-card
 * cooldowns, and no persistence between rounds. That's deliberately left as a TODO — extend
 * {@link #playCard(Card)} / {@link #startNewRound(int)} once the round/turn structure in
 * BattleController is fleshed out (e.g. move played cards to a discard list and reshuffle the
 * discard back into the pool once it's empty, like a typical deckbuilder).
 *
 * <p>Registered like any other service via {@link ServiceLocator#registerCardService}.
 */
public class CardService {
  private static final Logger logger = LoggerFactory.getLogger(CardService.class);

  private final List<Card> pool = new ArrayList<>();
  private final List<Card> hand = new ArrayList<>();
  private final Map<String, Skin> skinCache = new HashMap<>();

  /**
   * @param cardConfigFile path to a JSON file shaped like:
   *     <pre>{@code
   * {
   *   "cards": [
   *     {
   *       "id": "strike",
   *       "name": "Strike",
   *       "description": "Deal 10 damage",
   *       "trigger": "damage",
   *       "args": [10],
   *       "skinFile": "sprites/cards/cardExample.json",
   *       "skinAtlas": "sprites/cards/cardExample.atlas",
   *       "styleName": "cardStyle",
   *       "size": [300, 456]
   *     }
   *   ]
   * }
   * }</pre>
   */
  public CardService(Path cardConfigFile) {
    loadPool(cardConfigFile);
  }

  private void loadPool(Path cardConfigFile) {
    JsonValue root = new JsonReader().parse(Gdx.files.internal(cardConfigFile.toString()));
    JsonValue cardsArray = root.get("cards");

    if (cardsArray == null) {
      logger.error("Card config {} has no \"cards\" array", cardConfigFile);
      return;
    }

    for (JsonValue entry : cardsArray) {
      JsonValue sizeArray = entry.get("size");
      float width = sizeArray != null ? sizeArray.getFloat(0) : 300f;
      float height = sizeArray != null ? sizeArray.getFloat(1) : 456f;

      pool.add(
          new Card(
              entry.getString("id"),
              entry.getString("name", entry.getString("id")),
              entry.getString("description", ""),
              entry.getString("trigger"),
              jsonArrayToObjects(entry.get("args")),
              entry.getString("skinFile", null),
              entry.getString("skinAtlas", null),
              entry.getString("styleName", null),
              width,
              height));
    }

    logger.debug("Loaded {} card definitions from {}", pool.size(), cardConfigFile);
  }

  private static Object[] jsonArrayToObjects(JsonValue array) {
    if (array == null) {
      return new Object[0];
    }
    Object[] result = new Object[array.size];
    int i = 0;
    for (JsonValue child = array.child; child != null; child = child.next) {
      if (child.isNumber()) {
        double value = child.asDouble();
        result[i] = (value == Math.rint(value)) ? (Object) (int) value : (Object) value;
      } else if (child.isBoolean()) {
        result[i] = child.asBoolean();
      } else {
        result[i] = child.asString();
      }
      i++;
    }
    return result;
  }

  /**
   * Deals a fresh hand for the round. Currently just shuffles the whole pool and takes the
   * first {@code handSize} — replace with real draw-pile/discard-pile logic later.
   */
  public void startNewRound(int handSize) {
    hand.clear();
    List<Card> shuffled = new ArrayList<>(pool);
    Collections.shuffle(shuffled);
    hand.addAll(shuffled.subList(0, Math.min(handSize, shuffled.size())));
    logger.debug("Dealt new hand: {}", hand.stream().map(Card::id).toList());
  }

  /** The cards currently available to drag onto the battlefield. */
  public List<Card> getHand() {
    return Collections.unmodifiableList(hand);
  }

  /**
   * Marks a card as played, removing it from the hand. Called once whatever consumes the
   * card's trigger (e.g. PlayerActions) has applied its effect. Currently just discards it
   * for the rest of the round — no discard pile to reshuffle from yet.
   */
  public void playCard(Card card) {
    if (hand.remove(card)) {
      logger.debug("Played card {}", card.id());
    } else {
      logger.warn("Tried to play card {} that isn't in hand", card.id());
    }
  }

  /**
   * Builds ready-to-render ClickableRecords for the current hand, laid out left-to-right
   * starting at {@code startX}, spaced {@code spacing} pixels apart, all at height {@code y}.
   * Hand it to BattleScreen to merge with any JSON-defined static buttons before constructing
   * a single ClickableFactory.
   */
  public List<ClickableRecord> buildHandRecords(float startX, float y, float spacing) {
    List<ClickableRecord> records = new ArrayList<>();
    float x = startX;
    for (Card card : hand) {
      Skin skin = getOrLoadSkin(card.skinFile(), card.skinAtlas());
      records.add(card.toClickableRecord(x, y, skin));
      x += spacing;
    }
    return records;
  }

  private Skin getOrLoadSkin(String skinFile, String skinAtlas) {
    if (skinFile == null && skinAtlas == null) {
      return null;
    }
    String cacheKey = skinFile + "|" + skinAtlas;
    return skinCache.computeIfAbsent(
        cacheKey,
        key -> {
          if (skinFile != null && skinAtlas != null) {
            TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(skinAtlas));
            return new Skin(Gdx.files.internal(skinFile), atlas);
          } else if (skinFile != null) {
            return new Skin(Gdx.files.internal(skinFile));
          } else {
            return new Skin(new TextureAtlas(Gdx.files.internal(skinAtlas)));
          }
        });
  }
}
