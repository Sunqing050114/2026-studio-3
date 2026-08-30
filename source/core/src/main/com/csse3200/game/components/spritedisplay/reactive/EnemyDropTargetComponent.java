package com.csse3200.game.components.spritedisplay.reactive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.csse3200.game.components.Component;
import com.csse3200.game.components.spritedisplay.clickable.TriggerPayload;
import com.csse3200.game.services.ServiceLocator;

/**
 * Makes any entity a valid drop target for card drag-and-drop, purely from its
 * world-space position/scale. No dependency on the entity having any UI/display
 * component — the drop hitbox is computed by projecting the entity's world
 * position into stage coordinates every frame.
 *
 * <p>This component is intentionally game-agnostic: it doesn't know what "damage" or
 * "heal" or "updateHealth" mean. It just unpacks whatever {@link TriggerPayload} the
 * dragged card carried and fires that event on the drop target, with whatever args
 * the card was configured with (see Card / CardService / ClickableRecord.args()).
 * Whatever component cares about that event (e.g. PlayerActions) listens for it and
 * decides what it means. UI feedback ("Strike played") is handled separately by
 * DragNDrop, on the card's own entity — see DragNDrop.dragStop().
 *
 * <p>DEBUG_VISUAL draws a translucent red rectangle over the actual hitbox so you
 * can visually confirm the drop zone lines up with where you're dragging. Leave off
 * unless you're actively debugging alignment — it allocates a Texture per instance.
 */
public class EnemyDropTargetComponent extends Component {
    private static final boolean DEBUG_VISUAL = false;

    private final DragAndDrop dragAndDrop; // shared instance, passed in
    private final Camera worldCamera;      // camera used to render the game world
    private Actor hitboxActor;
    private Image debugOverlay;
    private Texture debugTexture;

    // Tune these to roughly match the entity's on-screen sprite size.
    private static final float HITBOX_WIDTH_PX = 100f;
    private static final float HITBOX_HEIGHT_PX = 100f;

    public EnemyDropTargetComponent(DragAndDrop dragAndDrop, Camera worldCamera) {
        this.dragAndDrop = dragAndDrop;
        this.worldCamera = worldCamera;
    }

    @Override
    public void create() {
        super.create();

        hitboxActor = new Actor();

        if (DEBUG_VISUAL) {
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(1f, 0f, 0f, 0.35f);
            pixmap.fill();
            debugTexture = new Texture(pixmap);
            pixmap.dispose();

            debugOverlay = new Image(debugTexture);
            debugOverlay.setTouchable(Touchable.disabled); // must not intercept drag/drop itself
            ServiceLocator.getRenderService().getStage().addActor(debugOverlay);
        }

        syncBounds(); // set initial position/size from entity

        // Critical: DragAndDrop skips any target whose actor.getStage() == null.
        // An Actor with no parent is never "on" the stage, so it must be added here
        // even though it's invisible and only used for hit-testing.
        ServiceLocator.getRenderService().getStage().addActor(hitboxActor);

        dragAndDrop.addTarget(new DragAndDrop.Target(hitboxActor) {
            @Override
            public boolean drag(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                return true; // add validity checks against payload.getObject() if needed
            }

            @Override
            public void drop(DragAndDrop.Source source, DragAndDrop.Payload payload, float x, float y, int pointer) {
                Object obj = payload.getObject();
                if (!(obj instanceof TriggerPayload triggerPayload)) {
                    return;
                }

                fireTrigger(triggerPayload);
            }
        });
    }

    /**
     * Dispatches the payload's trigger with its args, matching EventHandler's supported
     * arities (0–3). Add a case here if a card ever needs more than 3 arguments — though
     * consider bundling extra data into a single object instead (see EventHandler's own
     * javadoc recommendation).
     */
    private void fireTrigger(TriggerPayload payload) {
        Object[] args = payload.args();
        switch (args.length) {
            case 0 -> entity.getEvents().trigger(payload.trigger());
            case 1 -> entity.getEvents().trigger(payload.trigger(), args[0]);
            case 2 -> entity.getEvents().trigger(payload.trigger(), args[0], args[1]);
            case 3 -> entity.getEvents().trigger(payload.trigger(), args[0], args[1], args[2]);
            default -> Gdx.app.error(
                    "DropTarget",
                    "Trigger '" + payload.trigger() + "' has " + args.length
                            + " args; only 0-3 are supported.");
        }
    }

    @Override
    public void update() {
        syncBounds(); // keep hitbox glued to entity's current position every frame
    }

    @Override
    public void dispose() {
        super.dispose();
        if (hitboxActor != null) {
            hitboxActor.remove();
        }
        if (debugOverlay != null) {
            debugOverlay.remove();
        }
        if (debugTexture != null) {
            debugTexture.dispose();
        }
    }

    /**
     * Projects the entity's world position into stage coordinates and sizes the
     * hitbox actor accordingly.
     *
     * IMPORTANT: Camera.project(Vector3) does NOT flip Y to screen-down
     * convention — it's a pure NDC-to-viewport-pixel mapping, so its output is
     * already Y-up, bottom-left origin. Since this Stage uses a plain
     * ScreenViewport spanning the whole window 1:1 (see Renderer), that output
     * is already directly usable as stage coordinates. Do NOT run it through
     * screenToStageCoordinates() — that applies an extra unwanted flip.
     */
    private void syncBounds() {
        Vector3 worldPos = new Vector3(
                entity.getCenterPosition().x,
                entity.getCenterPosition().y,
                0);

        worldCamera.project(worldPos); // already Y-up, bottom-left origin pixels

        float stageX = worldPos.x - HITBOX_WIDTH_PX / 2f;
        float stageY = worldPos.y - HITBOX_HEIGHT_PX / 2f;

        hitboxActor.setBounds(stageX, stageY, HITBOX_WIDTH_PX, HITBOX_HEIGHT_PX);

        if (DEBUG_VISUAL && debugOverlay != null) {
            debugOverlay.setBounds(stageX, stageY, HITBOX_WIDTH_PX, HITBOX_HEIGHT_PX);
        }
    }
}
