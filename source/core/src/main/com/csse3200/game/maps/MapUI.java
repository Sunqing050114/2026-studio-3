import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csse3200.game.ui.UIComponent;

public abstract class MapUI extends UIComponent {

  @Override
  public void create() {
    super.create();
    addActors();
  }

  @Override
  public int getLayer() {
    return 1;
  }

  @Override
  public float getZIndex() {
    return 1f;
  }

  private void addActors() {
    // something

  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    super.dispose();
  }
}
