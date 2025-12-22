package ru.mipt.bit.platformer.view;

import com.badlogic.gdx.maps.tiled.TiledMap;

public interface MapProvider {
    TiledMap load();
}