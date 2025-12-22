package ru.mipt.bit.platformer.input;

import ru.mipt.bit.platformer.input.CommandQueue;

public interface InputCommand {
    void enqueueIfTriggered(InputSource input, CommandQueue queue);
}
