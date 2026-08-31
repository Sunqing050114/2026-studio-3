package com.csse3200.game.maps;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;


public class MapConnectionGroup extends Group{
    private final float length;
    private final float angle;
    private Image mapConnection;

    public MapConnectionGroup (Vector2 start, Vector2 end) {

        Vector2 difference = end.cpy().sub(start);
        this.length = difference.len();
        this.angle = difference.angleDeg();

        loadNodeAssets();
        mapConnection = new Image(
            ServiceLocator.getResourceService().getAsset("nodeLine.png",
            Texture.class));

        mapConnection.setSize(8, length);
        mapConnection.setRotation(angle);
        mapConnection.setPosition(start.x, start.y);

        addActor(mapConnection);


    }

    public double getAngle() {
        return this.angle;
    }

    private void loadNodeAssets() {
        String[] nodeAssets = {"nodeLine.png"};
        ResourceService resourceService = ServiceLocator.getResourceService();
        resourceService.loadTextures(nodeAssets);
        ServiceLocator.getResourceService().loadAll();
    }
}
