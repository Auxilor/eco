# bStats Custom Charts Spec — eco (core)

## Goal

Inject a baseline `integrations_active` chart into **every** plugin built on eco automatically, sourced from `EcoPlugin.loadedIntegrations`. Removes the need for each downstream plugin to declare an integrations chart of its own.

## File to Edit

`eco-core/core-plugin/src/main/kotlin/com/willfp/eco/internal/spigot/integrations/bstats/MetricHandler.kt`

## Class / Object

`MetricHandler` (package `com.willfp.eco.internal.spigot.integrations.bstats`)

## Required Changes

### 1. Replace `createMetrics` body

Currently:

```kotlin
fun createMetrics(plugin: EcoPlugin) {
    val metrics = Metrics(plugin, plugin.bStatsId)
    for (chart in plugin.customCharts) {
        metrics.addCustomChart(chart.toBStatsChart())
    }
}
```

Replace with:

```kotlin
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
```

No other code in `MetricHandler.kt` changes. The existing `AdvancedPie` import (line 7) is reused — no new imports needed.

## Behaviour

- Chart `integrations_active` is added to every eco plugin's bStats payload automatically.
- When a plugin has no loaded integrations, the supplier returns `null` and bStats skips submission for that interval (no empty data point).
- Downstream plugins do not need to add this chart to their own `getCustomCharts()` — and must not, to avoid duplicate chart IDs.

## Verification

After edit, `MetricHandler.kt` must:
- Still iterate `plugin.customCharts` and add each via `toBStatsChart()` unchanged.
- Additionally call `metrics.addCustomChart(...)` once with an `AdvancedPie("integrations_active")` whose supplier reads `plugin.loadedIntegrations`.
- Compile (no other changes).

## bstats.org Setup (manual, not for Haiku)

For **every** eco-based plugin on bstats.org, add a custom chart:
- Chart ID: `integrations_active`
- Type: Advanced Pie
