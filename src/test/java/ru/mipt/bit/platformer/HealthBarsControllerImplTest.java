package ru.mipt.bit.platformer;

import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.view.HealthBarsController;
import ru.mipt.bit.platformer.view.HealthBarsControllerImpl;
import static org.junit.jupiter.api.Assertions.*;

class HealthBarsControllerImplTest {
    @Test
    void toggleChangesEnabledFlag() {
        HealthBarsController controller = new HealthBarsControllerImpl();
        assertFalse(controller.isEnabled(), "Initially disabled");
        controller.toggle();
        assertTrue(controller.isEnabled(), "Enabled after first toggle");
        controller.toggle();
        assertFalse(controller.isEnabled(), "Disabled after second toggle");
    }
}
