package com.willfp.eco.core.command;

import org.jetbrains.annotations.NotNull;

/**
 * A notification exception is thrown when {@link org.bukkit.command.CommandSender}s don't
 * specify valid arguments in commands.
 * <p>
 * Methods in eco that throw this will contain automatic handling and thus
 * should not be surrounded by try / catch blocks.
 */
public class NotificationException extends Exception {
    /**
     * The key for the lang.yml message to be sent.
     */
    private final String key;

    /**
     * Creates a notification exception.
     *
     * @param key The lang key of the notification.
     */
    public NotificationException(@NotNull final String key) {
        super(key);
        this.key = key;
    }

    /**
     * Get the lang key.
     *
     * @return The lang key.
     */
    public String getKey() {
        return key;
    }
}
