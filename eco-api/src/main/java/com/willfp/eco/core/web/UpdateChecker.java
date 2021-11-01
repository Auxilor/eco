package com.willfp.eco.core.web;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import org.bukkit.util.Consumer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Class to check for updates of a plugin on spigot.
 */
public class UpdateChecker extends PluginDependent<EcoPlugin> {
    /**
     * Create an update checker for the specified spigot resource id.
     *
     * @param plugin The plugin to check.
     */
    public UpdateChecker(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Get the latest version of the plugin.
     *
     * @param callback The process to run after checking.
     */
    public void getVersion(@NotNull final Consumer<? super String> callback) {
        this.getPlugin().getScheduler().runAsync(() -> {
            try {
                InputStream inputStream = new URL(
                        "https://api.polymart.org/v1/getResourceInfoSimple?key=version&resource_id=" + this.getPlugin().getResourceId()
                ).openStream();
                Scanner scanner = new Scanner(inputStream);

                if (scanner.hasNext()) {
                    callback.accept(scanner.next());
                }
            } catch (IOException e) {
                this.getPlugin().getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }
}
