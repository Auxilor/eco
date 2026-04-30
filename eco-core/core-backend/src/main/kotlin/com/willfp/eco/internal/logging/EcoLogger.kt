package com.willfp.eco.internal.logging

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.StringUtils
import java.util.logging.Level
import java.util.logging.Logger
import org.bukkit.Bukkit

class EcoLogger(private val plugin: EcoPlugin) : Logger(plugin.name, null as String?) {
    override fun info(msg: String) {
        Bukkit.getConsoleSender().sendMessage("[${plugin.name}] ${StringUtils.format(msg, StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)}")
    }

    init {
        parent = plugin.server.logger
        this.level = Level.ALL
    }
}
