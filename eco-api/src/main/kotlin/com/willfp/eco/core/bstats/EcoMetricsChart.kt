package com.willfp.eco.core.bstats

import java.util.function.Supplier

/**
 * A custom bStats chart, described independently of the bStats library.
 *
 * Charts are supplied by a plugin from `EcoPlugin#getCustomCharts` and converted into real
 * bStats charts by eco. Each subclass mirrors a bStats chart type, and each carries a supplier
 * that is invoked whenever metrics are submitted. Suppliers that may return null skip the
 * submission for that interval.
 *
 * @param id The chart ID, as it appears on bStats.
 */
sealed class EcoMetricsChart(val id: String) {

    /**
     * A pie chart of a single value.
     *
     * @param id       The chart ID.
     * @param supplier Supplies the value, or null to skip.
     */
    class SimplePie(id: String, val supplier: () -> String?) : EcoMetricsChart(id)

    /**
     * A pie chart of several values with counts.
     *
     * @param id       The chart ID.
     * @param supplier Supplies value to count, or null to skip.
     */
    class AdvancedPie(id: String, val supplier: () -> Map<String, Int>?) : EcoMetricsChart(id)

    /**
     * A two-level pie chart.
     *
     * @param id       The chart ID.
     * @param supplier Supplies outer value to inner value to count, or null to skip.
     */
    class DrilldownPie(id: String, val supplier: () -> Map<String, Map<String, Int>>?) : EcoMetricsChart(id)

    /**
     * A line chart of a single value over time.
     *
     * @param id       The chart ID.
     * @param supplier Supplies the value.
     */
    class SingleLine(id: String, val supplier: () -> Int) : EcoMetricsChart(id)

    /**
     * A line chart of several values over time.
     *
     * @param id       The chart ID.
     * @param supplier Supplies line name to value, or null to skip.
     */
    class MultiLine(id: String, val supplier: () -> Map<String, Int>?) : EcoMetricsChart(id)

    /**
     * A bar chart of several values with counts.
     *
     * @param id       The chart ID.
     * @param supplier Supplies bar name to value, or null to skip.
     */
    class SimpleBar(id: String, val supplier: () -> Map<String, Int>?) : EcoMetricsChart(id)

    /**
     * A bar chart of several values, each with a series of counts.
     *
     * @param id       The chart ID.
     * @param supplier Supplies bar name to values, or null to skip.
     */
    class AdvancedBar(id: String, val supplier: () -> Map<String, IntArray>?) : EcoMetricsChart(id)

    /**
     * Java-friendly factories, taking [Supplier] instead of a Kotlin function type.
     */
    companion object {

        /**
         * Create a new [SimplePie].
         *
         * @param id       The chart ID.
         * @param supplier Supplies the value, or null to skip.
         * @return The chart.
         */
        @JvmStatic
        fun simplePie(id: String, supplier: Supplier<String?>) = SimplePie(id) { supplier.get() }

        /**
         * Create a new [AdvancedPie].
         *
         * @param id       The chart ID.
         * @param supplier Supplies value to count, or null to skip.
         * @return The chart.
         */
        @JvmStatic
        fun advancedPie(id: String, supplier: Supplier<Map<String, Int>?>) = AdvancedPie(id) { supplier.get() }

        /**
         * Create a new [DrilldownPie].
         *
         * @param id       The chart ID.
         * @param supplier Supplies outer value to inner value to count, or null to skip.
         * @return The chart.
         */
        @JvmStatic
        fun drilldownPie(id: String, supplier: Supplier<Map<String, Map<String, Int>>?>) = DrilldownPie(id) { supplier.get() }

        /**
         * Create a new [SingleLine].
         *
         * @param id       The chart ID.
         * @param supplier Supplies the value.
         * @return The chart.
         */
        @JvmStatic
        fun singleLine(id: String, supplier: Supplier<Int>) = SingleLine(id) { supplier.get() }

        /**
         * Create a new [MultiLine].
         *
         * @param id       The chart ID.
         * @param supplier Supplies line name to value, or null to skip.
         * @return The chart.
         */
        @JvmStatic
        fun multiLine(id: String, supplier: Supplier<Map<String, Int>?>) = MultiLine(id) { supplier.get() }

        /**
         * Create a new [SimpleBar].
         *
         * @param id       The chart ID.
         * @param supplier Supplies bar name to value, or null to skip.
         * @return The chart.
         */
        @JvmStatic
        fun simpleBar(id: String, supplier: Supplier<Map<String, Int>?>) = SimpleBar(id) { supplier.get() }

        /**
         * Create a new [AdvancedBar].
         *
         * @param id       The chart ID.
         * @param supplier Supplies bar name to values, or null to skip.
         * @return The chart.
         */
        @JvmStatic
        fun advancedBar(id: String, supplier: Supplier<Map<String, IntArray>?>) = AdvancedBar(id) { supplier.get() }

    }
}
