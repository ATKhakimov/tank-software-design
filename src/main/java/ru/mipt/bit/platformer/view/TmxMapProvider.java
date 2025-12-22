package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class TmxMapProvider implements MapProvider {
    private final String resourcePath;

    public TmxMapProvider(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public TiledMap load() {
        return new TmxMapLoader().load(resourcePath);
    }
}