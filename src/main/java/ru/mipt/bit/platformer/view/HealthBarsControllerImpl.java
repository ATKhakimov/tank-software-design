package ru.mipt.bit.platformer.view;

public class HealthBarsControllerImpl implements HealthBarsController {
    private boolean enabled = false;

    @Override
    public void toggle() {
        enabled = !enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
