package com.willfp.eco.internal.spigot.integrations.bstats

import com.willfp.eco.core.EcoPlugin

object MetricHandler {
    fun createMetrics(plugin: EcoPlugin) {
        Metrics(plugin)
    }
}