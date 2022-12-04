package com.willfp.eco.core.command;

public class NotificationException extends Exception {

    private final String key;

    public NotificationException(String key) {
        super(key);
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
