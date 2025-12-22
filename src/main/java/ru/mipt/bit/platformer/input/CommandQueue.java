package ru.mipt.bit.platformer.input;

import java.util.ArrayList;
import java.util.List;

public class CommandQueue {
    private final List<Runnable> queue = new ArrayList<>();

    public void enqueue(Runnable action) {
        if (action != null) {
            queue.add(action);
        }
    }

    public void executeAll() {
        for (Runnable action : queue) {
            action.run();
        }
        queue.clear();
    }
}
