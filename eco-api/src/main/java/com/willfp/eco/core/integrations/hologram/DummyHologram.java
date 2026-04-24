package com.willfp.eco.core.integrations.hologram;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Dummy hologram, created if no integrations are present on the server.
 */
class DummyHologram implements Hologram {
    @Override
    public void remove() {
        // Do nothing.
    }

    @Override
    public void setContents(@NotNull final List<String> contents) {
        // Do nothing.
    }
}
