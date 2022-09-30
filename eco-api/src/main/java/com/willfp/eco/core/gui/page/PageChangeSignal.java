package com.willfp.eco.core.gui.page;

import com.willfp.eco.core.gui.menu.Signal;

/**
 * Represents a page change.
 *
 * @param newPage The new page.
 * @param oldPage The old page.
 */
public record PageChangeSignal(
        int newPage,
        int oldPage
) implements Signal {

}
