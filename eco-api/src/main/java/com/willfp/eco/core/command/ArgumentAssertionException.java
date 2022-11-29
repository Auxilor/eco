package com.willfp.eco.core.command;

public class ArgumentAssertionException extends Exception {

    private final String langTarget;

    public ArgumentAssertionException(String langTarget) {
        super(langTarget);
        this.langTarget = langTarget;
    }

    public String getLangTarget() {
        return langTarget;
    }
}
