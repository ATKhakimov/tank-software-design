package ru.mipt.bit.platformer;

public interface InputCommand {
    void enqueueIfTriggered(InputSource input, CommandQueue queue);
}
