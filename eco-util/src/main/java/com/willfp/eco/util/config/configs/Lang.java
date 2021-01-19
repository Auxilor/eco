package com.willfp.eco.util.config.configs;

import com.willfp.eco.util.StringUtils;
import com.willfp.eco.util.config.BaseConfig;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import org.jetbrains.annotations.NotNull;

public class Lang extends BaseConfig {
    /**
     * Lang.yml.
     *
     * @param plugin The plugin.
     */
    public Lang(@NotNull final AbstractEcoPlugin plugin) {
        super("lang", false, plugin);
    }

    /**
     * Get the prefix for messages in chat.
     *
     * @return The prefix.
     */
    public String getPrefix() {
        return StringUtils.translate(this.getConfig().getString("messages.prefix"));
    }

    /**
     * Get the no permission message.
     *
     * @return The message.
     */
    public String getNoPermission() {
        return getPrefix() + StringUtils.translate(this.getConfig().getString("messages.no-permission"));
    }

    /**
     * Get a chat message.
     *
     * @param message The key of the message.
     * @return The message with a prefix appended.
     */
    public String getMessage(@NotNull final String message) {
        return getPrefix() + StringUtils.translate(this.getConfig().getString("messages." + message));
    }
}
