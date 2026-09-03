package com.willfp.eco.core.web;

import com.willfp.eco.core.EcoPlugin;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

/**
 * Class to check for updates of a plugin on polymart.
 */
public class UpdateChecker {
    /**
     * The plugin.
     */
    private final EcoPlugin plugin;

    /**
     * Create an update checker for a plugin, using its polymart resource ID.
     *
     * @param plugin The plugin to check.
     */
    public UpdateChecker(@NotNull final EcoPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the latest version of the plugin from polymart.
     * <p>
     * The lookup runs asynchronously. The callback is invoked, off the main thread, with the latest
     * version string; if the request fails, a warning is logged and the callback is never invoked.
     *
     * @param callback The process to run after checking.
     */
    public void getVersion(@NotNull final Consumer<? super String> callback) {
        this.getPlugin().getScheduler().async().run(() -> {
            try (InputStream inputStream = new URI(
                    "https://api.polymart.org/v1/getResourceInfoSimple?key=version&resource_id=" + this.getPlugin().getResourceId()
            ).toURL().openStream();
                 Scanner scanner = new Scanner(inputStream)) {

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
