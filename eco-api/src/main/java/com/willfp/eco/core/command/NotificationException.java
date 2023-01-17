package com.willfp.eco.core.command;

/**
 * A notification exception is thrown when {@link org.bukkit.command.CommandSender}s don't
 * specify valid arguments in commands.
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
    public NotificationException(String key) {
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
