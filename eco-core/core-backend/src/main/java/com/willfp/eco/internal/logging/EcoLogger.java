package com.willfp.eco.internal.logging;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EcoLogger extends Logger {
    public EcoLogger(@NotNull final EcoPlugin plugin) {
        super(plugin.getName(), (String) null);
        String prefix = plugin.getDescription().getPrefix();
        this.setParent(plugin.getServer().getLogger());
        this.setLevel(Level.ALL);
    }

    @Override
    public void info(@NotNull final String msg) {
        super.info(StringUtils.translate(msg));
    }

    @Override
    public void warning(@NotNull final String msg) {
        super.warning(StringUtils.translate(msg));
    }

    @Override
    public void severe(@NotNull final String msg) {
        super.severe(StringUtils.translate(msg));
    }
}
