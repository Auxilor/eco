package com.willfp.eco.core.config.base;

import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

public class LangYml extends BaseConfig {
    /**
     * Lang.yml.
     *
     * @param plugin The plugin.
     */
    public LangYml(@NotNull final EcoPlugin plugin) {
        super("lang", false, plugin);
    }

    /**
     * Get the prefix for messages in chat.
     *
     * @return The prefix.
     */
    public String getPrefix() {
        return this.getString("messages.prefix");
    }

    /**
     * Get the no permission message.
     *
     * @return The message.
     */
    public String getNoPermission() {
        return getPrefix() + this.getString("messages.no-permission");
    }

    /**
     * Get a chat message.
     *
     * @param message The key of the message.
     * @return The message with a prefix appended.
     */
    public String getMessage(@NotNull final String message) {
        return getPrefix() + this.getString("messages." + message);
    }
}
