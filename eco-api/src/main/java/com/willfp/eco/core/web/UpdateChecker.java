package com.willfp.eco.core.web;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;

/**
 * Class to check for updates of a plugin on spigot.
 */
public class UpdateChecker {
    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

    /**
     * Create an update checker for the specified spigot resource id.
     *
     * @param plugin The plugin to check.
     */
    public UpdateChecker(@NotNull final EcoPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the latest version of the plugin.
     *
     * @param callback The process to run after checking.
     */
    public void getVersion(@NotNull final Consumer<? super String> callback) {
        this.getPlugin().getScheduler().runAsync(() -> {
            try {
                InputStream inputStream = new URI(
                        "https://api.polymart.org/v1/getResourceInfoSimple?key=version&resource_id=" + this.getPlugin().getResourceId()
                ).toURL().openStream();
                Scanner scanner = new Scanner(inputStream);

                if (scanner.hasNext()) {
                    callback.accept(scanner.next());
                }
            } catch (IOException | URISyntaxException e) {
                this.getPlugin().getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    /**
     * Get the plugin.
     *
     * @return The plugin.
     */
    public EcoPlugin getPlugin() {
        return plugin;
    }
}
