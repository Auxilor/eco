package com.willfp.eco.core.bstats

import java.util.function.Supplier

sealed class EcoMetricsChart(val id: String) {

    class SimplePie(id: String, val supplier: () -> String?) : EcoMetricsChart(id)

    class AdvancedPie(id: String, val supplier: () -> Map<String, Int>?) : EcoMetricsChart(id)

    class DrilldownPie(id: String, val supplier: () -> Map<String, Map<String, Int>>?) : EcoMetricsChart(id)

    class SingleLine(id: String, val supplier: () -> Int) : EcoMetricsChart(id)

    class MultiLine(id: String, val supplier: () -> Map<String, Int>?) : EcoMetricsChart(id)

    class SimpleBar(id: String, val supplier: () -> Map<String, Int>?) : EcoMetricsChart(id)

    class AdvancedBar(id: String, val supplier: () -> Map<String, IntArray>?) : EcoMetricsChart(id)

    companion object {

        @JvmStatic
        fun simplePie(id: String, supplier: Supplier<String?>) = SimplePie(id) { supplier.get() }

        @JvmStatic
        fun advancedPie(id: String, supplier: Supplier<Map<String, Int>?>) = AdvancedPie(id) { supplier.get() }

        @JvmStatic
        fun drilldownPie(id: String, supplier: Supplier<Map<String, Map<String, Int>>?>) = DrilldownPie(id) { supplier.get() }

        @JvmStatic
        fun singleLine(id: String, supplier: Supplier<Int>) = SingleLine(id) { supplier.get() }

        @JvmStatic
        fun multiLine(id: String, supplier: Supplier<Map<String, Int>?>) = MultiLine(id) { supplier.get() }

        @JvmStatic
        fun simpleBar(id: String, supplier: Supplier<Map<String, Int>?>) = SimpleBar(id) { supplier.get() }

        @JvmStatic
        fun advancedBar(id: String, supplier: Supplier<Map<String, IntArray>?>) = AdvancedBar(id) { supplier.get() }

    }
}
