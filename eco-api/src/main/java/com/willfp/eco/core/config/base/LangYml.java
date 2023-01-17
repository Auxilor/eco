package com.willfp.eco.core.config.base;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.BaseConfig;
import com.willfp.eco.core.config.ConfigType;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Default plugin lang.yml.
 */
public class LangYml extends BaseConfig {
    /**
     * The messages key.
     */
    public static final String KEY_MESSAGES = "messages";

    /**
     * The prefix key.
     */
    public static final String KEY_PREFIX = "messages.prefix";

    /**
     * The no permission key.
     */
    public static final String KEY_NO_PERMISSION = "messages.no-permission";

    /**
     * The not player key.
     */
    public static final String KEY_NOT_PLAYER = "messages.not-player";

    /**
     * Lang.yml.
     *
     * @param plugin The plugin.
     */
    public LangYml(@NotNull final EcoPlugin plugin) {
        super("lang", plugin, false, ConfigType.YAML);
    }

    /**
     * lang.yml requires certain keys to be present.
     * <p>
     * If the lang.yml does not contain these keys, it is considered to be
     * invalid and thus will show a warning in console.
     *
     * @return If valid.
     */
    public boolean isValid() {
        for (String key : List.of(KEY_MESSAGES, KEY_PREFIX, KEY_NO_PERMISSION, KEY_NOT_PLAYER)) {
            if (!this.has(key)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the prefix for messages in chat.
     *
     * @return The prefix.
     */
    public String getPrefix() {
        return this.getFormattedString(KEY_PREFIX);
    }

    /**
     * Get the no permission message.
     *
     * @return The message.
     */
    public String getNoPermission() {
        return getPrefix() + this.getFormattedString(KEY_NO_PERMISSION);
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
        return getPrefix() + this.getFormattedString(KEY_MESSAGES + "." + message, option);
    }
}
