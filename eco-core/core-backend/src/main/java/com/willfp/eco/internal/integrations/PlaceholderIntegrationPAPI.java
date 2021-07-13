package com.willfp.eco.internal.integrations;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.integrations.placeholder.PlaceholderIntegration;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderIntegrationPAPI extends PlaceholderExpansion implements PlaceholderIntegration {
    /**
     * The linked plugin.
     */
    private final EcoPlugin plugin;

    /**
     * Create a new PlaceholderAPI integration.
     *
     * @param plugin The plugin to manage placeholders for.
     */
    public PlaceholderIntegrationPAPI(@NotNull final EcoPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(@Nullable final Player player,
                                       @NotNull final String identifier) {
        return PlaceholderManager.getResult(player, identifier);
    }

    @Override
    public void registerIntegration() {
        this.register();
    }

    @Override
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public String translate(@NotNull final String text,
                            @Nullable final Player player) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
