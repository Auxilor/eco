package com.willfp.eco.internal.spigot.integrations.bstats

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.bstats.EcoMetricsChart
import com.willfp.eco.libs.bstats.bukkit.Metrics
import com.willfp.eco.libs.bstats.charts.AdvancedBarChart
import com.willfp.eco.libs.bstats.charts.AdvancedPie
import com.willfp.eco.libs.bstats.charts.CustomChart
import com.willfp.eco.libs.bstats.charts.DrilldownPie
import com.willfp.eco.libs.bstats.charts.MultiLineChart
import com.willfp.eco.libs.bstats.charts.SimpleBarChart
import com.willfp.eco.libs.bstats.charts.SimplePie
import com.willfp.eco.libs.bstats.charts.SingleLineChart

object MetricHandler {
    fun createMetrics(plugin: EcoPlugin) {
        val metrics = Metrics(plugin, plugin.bStatsId)

        for (chart in plugin.customCharts) {
            metrics.addCustomChart(chart.toBStatsChart())
        }

        metrics.addCustomChart(
            AdvancedPie("integrations_active") {
                plugin.loadedIntegrations
                    .associateWith { 1 }
                    .ifEmpty { null }
            }
        )
    }

    private fun EcoMetricsChart.toBStatsChart(): CustomChart = when (this) {
        is EcoMetricsChart.SimplePie -> SimplePie(id) { supplier() }
        is EcoMetricsChart.AdvancedPie -> AdvancedPie(id) { supplier() }
        is EcoMetricsChart.DrilldownPie -> DrilldownPie(id) { supplier() }
        is EcoMetricsChart.SingleLine -> SingleLineChart(id) { supplier() }
        is EcoMetricsChart.MultiLine -> MultiLineChart(id) { supplier() }
        is EcoMetricsChart.SimpleBar -> SimpleBarChart(id) { supplier() }
        is EcoMetricsChart.AdvancedBar -> AdvancedBarChart(id) { supplier() }
    }
}
