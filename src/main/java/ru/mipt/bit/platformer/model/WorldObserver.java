package ru.mipt.bit.platformer.model;

public interface WorldObserver {
    void objectAdded(GameObject object);
    void objectRemoved(GameObject object);
}
