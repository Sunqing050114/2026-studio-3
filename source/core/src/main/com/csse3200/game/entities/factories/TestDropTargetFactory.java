package com.csse3200.game.entities.factories;

import com.csse3200.game.components.spritedisplay.reactive.EnemyDropTargetComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * Minimal, non-moving test entity used purely to validate that
 * EnemyDropTargetComponent's drop zone actually lines up with where you drag
 * a card on screen. No physics, no health display, no movement — just a
 * sprite + the drop target component, so drag-and-drop can be debugged in
 * isolation from the real player entity.
 *
 * Delete this factory once EnemyDropTargetComponent is confirmed working.
 */
public class TestDropTargetFactory {

    public static Entity createTestTarget() {
        Entity target =
                new Entity()
                        .addComponent(new TextureRenderComponent("images/heart.png"))
                        .addComponent(
                                new EnemyDropTargetComponent(
                                        ServiceLocator.getDragAndDropService().getDragAndDrop(),
                                        ServiceLocator.getCamera()));

        target.getComponent(TextureRenderComponent.class).scaleEntity();
        return target;
    }

    private TestDropTargetFactory() {
        throw new IllegalStateException("Instantiating static util class");
    }
}