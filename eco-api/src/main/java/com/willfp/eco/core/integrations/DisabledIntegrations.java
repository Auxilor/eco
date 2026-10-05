package com.willfp.eco.core.integrations;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.EcoPlugin;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

/**
 * Plugins that server owners have chosen not to integrate with, set in eco's
 * {@code disabled-integrations} config option.
 * <p>
 * Eco plugins can never be disabled this way. The list is read once and needs a restart to change,
 * because integrations are only loaded on startup.
 */
public final class DisabledIntegrations {
    /**
     * The lowercase names of the disabled plugins.
     */
    private static Set<String> disabledPlugins = null;

    /**
     * Get if integrations with a plugin are disabled.
     *
     * @param pluginName The plugin name.
     * @return If disabled.
     */
    public static boolean isDisabled(@NotNull final String pluginName) {
        return getDisabledPlugins().contains(pluginName.toLowerCase())
                && !(Bukkit.getPluginManager().getPlugin(pluginName) instanceof EcoPlugin);
    }

    /**
     * Get if a plugin is enabled on the server and integrations with it are not disabled.
     *
     * @param pluginName The plugin name.
     * @return If enabled and not disabled.
     */
    public static boolean isEnabled(@NotNull final String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName) && !isDisabled(pluginName);
    }

    private static Set<String> getDisabledPlugins() {
        if (disabledPlugins == null) {
            disabledPlugins = Eco.get().getEcoPlugin().getConfigYml().getStrings("disabled-integrations").stream()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }

        return disabledPlugins;
    }

    private DisabledIntegrations() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
