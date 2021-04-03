package com.willfp.eco.internal.logging;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.util.StringUtils;
import org.bukkit.plugin.PluginLogger;
import org.jetbrains.annotations.NotNull;

public class EcoLogger extends PluginLogger {
    public EcoLogger(@NotNull final EcoPlugin context) {
        super(context);
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
