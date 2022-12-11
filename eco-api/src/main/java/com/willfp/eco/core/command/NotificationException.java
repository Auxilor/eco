package com.willfp.eco.core.command;

/**
 * An Exception class used for notifying a sender
 * with a langYml message
 */
public class NotificationException extends Exception {

    /**
     * The langYml key of the notification string.
     */
    private final String key;

    /**
     * Creates a notification exception.
     *
     * @param key the lang key of the notification.
     */
    public NotificationException(String key) {
        super(key);
        this.key = key;
    }

    /**
     * Get the lang key.
     *
     * @return the lang key
     */
    public String getKey() {
        return key;
    }
}
