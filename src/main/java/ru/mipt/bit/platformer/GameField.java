// Реализована абстракция игрового поля
package ru.mipt.bit.platformer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import ru.mipt.bit.platformer.util.GdxGameUtils;
import ru.mipt.bit.platformer.util.TileMovement;

public class GameField {
    private final TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;
    private final TiledMapTileLayer groundLayer;
    private final TileMovement movement;

    public GameField(Batch batch) {
        map = new TmxMapLoader().load("level.tmx");
        renderer = (OrthogonalTiledMapRenderer) GdxGameUtils.createSingleLayerMapRenderer(map, batch);
        groundLayer = GdxGameUtils.getSingleLayer(map);
        movement = new TileMovement(groundLayer, Interpolation.smooth);
    }

    public TileMovement movement() {
        return movement;
    }

    public void render() {
        renderer.render();
    }

    public void dispose() {
        renderer.dispose();
        map.dispose();
    }
}
