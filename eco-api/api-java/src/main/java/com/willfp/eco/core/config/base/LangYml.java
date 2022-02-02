package com.willfp.eco.core.config.base;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Default plugin lang.yml.
 */
public class LangYml extends BaseConfig {
    /**
     * Lang.yml.
     *
     * @param plugin The plugin.
     */
    public LangYml(@NotNull final EcoPlugin plugin) {
        super("lang", plugin, false, ConfigType.YAML);
    }

    /**
     * Get the prefix for messages in chat.
     *
     * @return The prefix.
     */
    public String getPrefix() {
        return this.getFormattedString("messages.prefix");
    }

    /**
     * Get the no permission message.
     *
     * @return The message.
     */
    public String getNoPermission() {
        return getPrefix() + this.getFormattedString("messages.no-permission");
    }

    /**
     * Get a chat message.
     *
     * @param message The key of the message.
     * @return The message with a prefix appended.
     */
    public String getMessage(@NotNull final String message) {
        return getMessage(message, StringUtils.FormatOption.WITH_PLACEHOLDERS);
    }

    /**
     * Get a chat message.
     *
     * @param message The key of the message.
     * @param option  The format options.
     * @return The message with a prefix appended.
     */
    public String getMessage(@NotNull final String message,
                             @NotNull final StringUtils.FormatOption option) {
        return getPrefix() + this.getFormattedString("messages." + message, option);
    }
}
