package com.csse3200.game.components.spritedisplay.clickable;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.csse3200.game.services.ServiceLocator;

public class DragNDrop extends InOutOnTrigger {

  public DragNDrop(ClickableRecord record) {
    super(record);
  }

  @Override
  protected void init(String trigger) {
    super.init(trigger);

    DragAndDrop dragAndDrop = ServiceLocator.getDragAndDropService().getDragAndDrop();

    dragAndDrop.addSource(
        new DragAndDrop.Source(this.getBtn()) {
          private boolean actuallyHidden = false;

          @Override
          public DragAndDrop.Payload dragStart(InputEvent event, float x, float y, int pointer) {
            actuallyHidden = false;

            DragAndDrop.Payload payload = new DragAndDrop.Payload();
            // Carry trigger + args + label together so the drop target can fire the
            // right event with the right arguments, without needing to know what a
            // "card" is or hardcode any specific event name.
            payload.setObject(new TriggerPayload(trigger, getArgs(), getLabel()));

            Button original = DragNDrop.this.getBtn();
            Button dragVisual = createDragVisual(original);
            dragVisual.setSize(original.getWidth(), original.getHeight());
            payload.setDragActor(dragVisual);

            dragAndDrop.setDragActorPosition(x, -y);

            return payload;
          }

          @Override
          public void drag(InputEvent event, float x, float y, int pointer) {
            Button original = DragNDrop.this.getBtn();
            boolean outsideBounds =
                x < 0 || y < 0 || x > original.getWidth() || y > original.getHeight();

            if (outsideBounds && original.isVisible()) {
              original.clearActions();
              original.setVisible(false);
              actuallyHidden = true;
            }
          }

          @Override
          public void dragStop(
              InputEvent event,
              float x,
              float y,
              int pointer,
              DragAndDrop.Payload payload,
              DragAndDrop.Target target) {
            if (actuallyHidden) {
              Button btn = DragNDrop.this.getBtn();
              btn.clearActions();
              btn.setVisible(true);
              btn.addAction(Actions.fadeIn(0.15f));
              btn.addAction(Actions.moveTo(targetX, targetY, 0.3f, Interpolation.sineIn));
            }

            if (target != null) {
              // UI feedback belongs here, not on the drop target: EventHandler is
              // per-entity, and this card's entity (battleUi) is what CardDisplay is
              // listening on — the drop target (e.g. the player) is a different entity
              // entirely and wouldn't reach CardDisplay's listener.
              entity.getEvents().trigger("cardPlayed", getLabel());
            }
            // The actual game-effect event (e.g. "damage"/"heal") is fired by the drop
            // target itself — see EnemyDropTargetComponent.drop().
          }
        });
  }

  private Button createDragVisual(Button original) {
    if (original instanceof ImageButton ib) {
      return new ImageButton((ImageButton.ImageButtonStyle) ib.getStyle());
    } else if (original instanceof ImageTextButton itb) {
      return new ImageTextButton(
          itb.getText().toString(), (ImageTextButton.ImageTextButtonStyle) itb.getStyle());
    } else if (original instanceof TextButton tb) {
      return new TextButton(tb.getText().toString(), (TextButton.TextButtonStyle) tb.getStyle());
    }
    return new Button(original.getStyle());
  }
}
