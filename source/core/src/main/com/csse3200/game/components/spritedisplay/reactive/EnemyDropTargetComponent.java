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

import java.util.Arrays;

public class EnemyDropTargetComponent extends Component {
    private static final boolean DEBUG_VISUAL = false;

    private final DragAndDrop dragAndDrop; // shared instance, passed in
    private final Camera worldCamera; // camera used to render the game world
    private Actor hitboxActor;
    private Image debugOverlay;
    private Texture debugTexture;
    private String id;

    // Tune these to roughly match the entity's on-screen sprite size.
    private static final float HITBOX_WIDTH_PX = 100f;
    private static final float HITBOX_HEIGHT_PX = 100f;

    public EnemyDropTargetComponent(DragAndDrop dragAndDrop, Camera worldCamera, String id) {
        this.dragAndDrop = dragAndDrop;
        this.worldCamera = worldCamera;
        this.id = id;
    }

    @Override
    public void create() {
        super.create();

        hitboxActor = new Actor();

        hitboxActor.setUserObject(id);


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

        dragAndDrop.addTarget(
                new DragAndDrop.Target(hitboxActor) {
                    @Override
                    public boolean drag(
                            DragAndDrop.Source source,
                            DragAndDrop.Payload payload,
                            float x,
                            float y,
                            int pointer) {
                        return true; // add validity checks against payload.getObject() if needed
                    }

                    @Override
                    public void drop(
                            DragAndDrop.Source source,
                            DragAndDrop.Payload payload,
                            float x,
                            float y,
                            int pointer) {
                        Object obj = payload.getObject();
                        if (!(obj instanceof TriggerPayload triggerPayload)) {
                            return;
                        }

                        fireTrigger(triggerPayload);
                    }
                });
    }

    /**
     * Dispatches the payload's trigger with its args plus this drop target's own String id
     * appended as the final argument, matching EventHandler's supported arities (0–3 args means
     * the payload itself must carry at most 2, since one slot is reserved for the target id). Add
     * a case here if a card ever needs more than 3 total arguments — though consider bundling
     * extra data into a single object instead (see EventHandler's own javadoc recommendation).
     */
    private void fireTrigger(TriggerPayload payload) {
        Object[] args = withTarget(payload.args(), id);
        switch (args.length) {
            case 0 -> entity.getEvents().trigger(payload.trigger());
            case 1 -> entity.getEvents().trigger(payload.trigger(), args[0]);
            case 2 -> entity.getEvents().trigger(payload.trigger(), args[0], args[1]);
            case 3 -> entity.getEvents().trigger(payload.trigger(), args[0], args[1], args[2]);
            default ->
                    Gdx.app.error(
                            "DropTarget",
                            "Trigger '"
                                    + payload.trigger()
                                    + "' has "
                                    + args.length
                                    + " args (including target id); only 0-3 are supported.");
        }
    }

    /**
     * Appends the drop target's meaningful String id (e.g. "player" or the enemy's config id) as
     * the final arg, e.g. {cardID} -> {cardID, "player"} or {cardID, "goblin1"}. This is the same
     * id passed into this component's constructor, not the internal ECS entity id.
     */
    private Object[] withTarget(Object[] original, String targetId) {
        Object[] result = Arrays.copyOf(original, original.length + 1);
        result[original.length] = targetId;
        return result;
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
     * Projects the entity's world position into stage coordinates and sizes the hitbox actor
     * accordingly.
     *
     * <p>IMPORTANT: Camera.project(Vector3) does NOT flip Y to screen-down convention — it's a pure
     * NDC-to-viewport-pixel mapping, so its output is already Y-up, bottom-left origin. Since this
     * Stage uses a plain ScreenViewport spanning the whole window 1:1 (see Renderer), that output is
     * already directly usable as stage coordinates. Do NOT run it through screenToStageCoordinates()
     * — that applies an extra unwanted flip.
     */
    private void syncBounds() {
        Vector3 worldPos = new Vector3(entity.getCenterPosition().x, entity.getCenterPosition().y, 0);

        worldCamera.project(worldPos); // already Y-up, bottom-left origin pixels

        float stageX = worldPos.x - HITBOX_WIDTH_PX / 2f;
        float stageY = worldPos.y - HITBOX_HEIGHT_PX / 2f;

        hitboxActor.setBounds(stageX, stageY, HITBOX_WIDTH_PX, HITBOX_HEIGHT_PX);

        if (DEBUG_VISUAL && debugOverlay != null) {
            debugOverlay.setBounds(stageX, stageY, HITBOX_WIDTH_PX, HITBOX_HEIGHT_PX);
        }
    }
}