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

  /**
   * Drag cards fire their "playCard" trigger on drop (see EnemyDropTargetComponent), once the
   * target is known. The base Clickable.onClick() fires on the button's ChangeEvent, which libGDX
   * raises on click AND on drag release — for a drag card that trigger only has the cardId baked
   * in (no target yet), so firing it here duplicates the drop-time trigger with the wrong arity
   * and crashes. Suppress it entirely for this variant.
   */
  @Override
  protected void onClick() {
    // Intentionally empty — see javadoc above.
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
                    // Describe *what* the card was dropped on, not the raw trigger args — args are
                    // for the game-effect event (see EnemyDropTargetComponent.fireTrigger), and
                    // often duplicate the label (e.g. card.id == card.name), which is confusing here.
                    Object userObject = target.getActor().getUserObject();
                    String targetLabel = (userObject instanceof String s) ? s : "the target";

                    entity.getEvents().trigger("cardPlayed", getLabel(), targetLabel);
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
