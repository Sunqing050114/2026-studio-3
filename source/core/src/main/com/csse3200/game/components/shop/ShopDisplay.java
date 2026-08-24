package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.components.player.InventoryComponent;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.maps.EncounterCallback;
import com.csse3200.game.shop.PurchaseResult;
import com.csse3200.game.shop.ShopConfig;
import com.csse3200.game.shop.ShopEncounter;
import com.csse3200.game.shop.ShopItem;
import com.csse3200.game.shop.ShopService;
import com.csse3200.game.ui.UIComponent;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Prototype shop UI for buying card-linked shop items. */
public class ShopDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopDisplay.class);
  private static final String SHOP_CONFIG = "configs/shopItems.json";
  private static final float Z_INDEX = 2f;

  private final InventoryComponent inventory;
  private final ShopEncounter shopEncounter;
  private final Map<String, Label> itemLabels = new HashMap<>();

  private Table table;
  private Label goldLabel;
  private Label statusLabel;

  public ShopDisplay(InventoryComponent inventory) {
    this(inventory, null, ShopEncounter.DEFAULT_NODE_ID);
  }

  public ShopDisplay(InventoryComponent inventory, EncounterCallback callback, String nodeId) {
    this(
        inventory,
        new ShopEncounter(
            nodeId,
            inventory,
            new ShopService(FileLoader.readClass(ShopConfig.class, SHOP_CONFIG)),
            callback));
  }

  public ShopDisplay(InventoryComponent inventory, ShopService shopService) {
    this(inventory, new ShopEncounter(inventory, shopService));
  }

  public ShopDisplay(InventoryComponent inventory, ShopEncounter shopEncounter) {
    this.inventory = inventory;
    this.shopEncounter =
        shopEncounter == null
            ? new ShopEncounter(inventory, new ShopService((ShopConfig) null))
            : shopEncounter;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    table = new Table();
    table.top().right();
    table.setFillParent(true);
    table.padTop(45f).padRight(15f);

    Label title = new Label("Shop", skin, "large");
    goldLabel = new Label("", skin);
    statusLabel = new Label("Select an item to buy.", skin);

    table.add(title).left().colspan(2);
    table.row();
    table.add(goldLabel).left().padTop(8f).colspan(2);
    table.row();

    if (shopEncounter.getItems().isEmpty()) {
      table.add(new Label("No shop items available.", skin)).left().padTop(10f).colspan(2);
    } else {
      addShopItems();
    }

    table.row();
    table.add(statusLabel).left().padTop(10f).colspan(2);
    table.row();
    TextButton leaveButton = new TextButton("Leave Shop", skin);
    leaveButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            leaveShop();
          }
        });
    table.add(leaveButton).left().padTop(10f).colspan(2);

    refresh();
    stage.addActor(table);
  }

  private void addShopItems() {
    for (ShopItem item : shopEncounter.getItems()) {
      Label itemLabel = new Label("", skin);
      TextButton buyButton = new TextButton("Buy", skin);
      itemLabels.put(item.id, itemLabel);

      buyButton.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
              buyItem(item.id);
            }
          });

      table.row();
      table.add(itemLabel).left().padTop(8f).padRight(10f);
      table.add(buyButton).right().padTop(8f);
    }
  }

  private void buyItem(String itemId) {
    PurchaseResult result = shopEncounter.purchase(itemId);
    logger.debug("Purchase result for {}: {}", itemId, result.getStatus());
    statusLabel.setText(getStatusMessage(result));
    refresh();
  }

  private void leaveShop() {
    logger.debug("Shop encounter completed for node {}", shopEncounter.getNodeId());
    shopEncounter.complete(true);
    table.remove();
  }

  private void refresh() {
    goldLabel.setText(
        inventory == null ? "Gold: unavailable" : String.format("Gold: %d", inventory.getGold()));
    for (ShopItem item : shopEncounter.getItems()) {
      Label label = itemLabels.get(item.id);
      if (label != null) {
        label.setText(getItemText(item));
      }
    }
  }

  private String getItemText(ShopItem item) {
    return String.format(
        "%s [%s] - %d gold - Stock: %d",
        item.getDisplayName(), item.cardId, item.price, item.stock);
  }

  private String getStatusMessage(PurchaseResult result) {
    return result.getMessage();
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    if (table != null) {
      table.remove();
    }
    super.dispose();
  }
}
