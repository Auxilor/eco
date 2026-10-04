package com.willfp.eco.core.display;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * The registered display modules, ordered by weight.
 * <p>
 * Displays run on netty threads while modules are registered on the main thread, so the list is
 * replaced rather than modified, and readers always see a complete snapshot.
 */
final class DisplayModuleRegistry {
    /**
     * The current snapshot.
     */
    private volatile List<DisplayModule> modules = List.of();

    /**
     * Register a module.
     *
     * @param module The module.
     */
    synchronized void register(@NotNull final DisplayModule module) {
        List<DisplayModule> updated = new ArrayList<>(this.modules);
        updated.add(module);
        updated.sort(Comparator.comparingInt(DisplayModule::getWeight));
        this.modules = List.copyOf(updated);
    }

    /**
     * Unregister a module.
     *
     * @param module The module.
     */
    synchronized void unregister(@NotNull final DisplayModule module) {
        List<DisplayModule> updated = new ArrayList<>(this.modules);
        updated.remove(module);
        this.modules = List.copyOf(updated);
    }

    /**
     * The modules, lowest weight first.
     *
     * @return The modules.
     */
    @NotNull
    List<DisplayModule> getModules() {
        return this.modules;
    }
}
