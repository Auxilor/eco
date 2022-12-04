package com.willfp.eco.core.command;

public interface RegistrableCommandBase extends CommandBase {
    void register();

    void unregister();
}
