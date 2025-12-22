package ru.mipt.bit.platformer.view;

public class RenderableCreation {
    public enum Kind { PLAYER_TANK, AI_TANK, OBSTACLE, BULLET, UNKNOWN }

    private final Renderable main;
    private final Renderable decorated;
    private final Kind kind;

    public RenderableCreation(Renderable main, Renderable decorated, Kind kind) {
        this.main = main;
        this.decorated = decorated;
        this.kind = kind;
    }

    public Renderable main() {
        return main;
    }

    public Renderable decorated() {
        return decorated;
    }

    public Kind kind() {
        return kind;
    }
}