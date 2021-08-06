package com.willfp.eco.internal.logging

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.StringUtils
import java.util.logging.Level
import java.util.logging.Logger

class EcoLogger(plugin: EcoPlugin) : Logger(plugin.name, null as String?) {
    override fun info(msg: String) {
        super.info(StringUtils.format(msg))
    }

    override fun warning(msg: String) {
        super.warning(StringUtils.format(msg))
    }

    override fun severe(msg: String) {
        super.severe(StringUtils.format(msg))
    }

    init {
        parent = plugin.server.logger
        this.level = Level.ALL
    }
}