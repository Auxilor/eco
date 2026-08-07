package com.willfp.eco.core.display;

/**
 * The priority (order) of display modules.
 * <p>
 * Each priority maps onto a weight, and modules are run in ascending weight order, so a
 * lower weight runs first. Modules that need finer control can pass a raw weight into
 * {@link DisplayModule#DisplayModule(com.willfp.eco.core.EcoPlugin, int)} instead.
 */
public enum DisplayPriority {
    /**
     * Custom weight.
     *
     * @deprecated Will never be used.
     */
    @Deprecated(since = "6.53.0", forRemoval = true)
    CUSTOM(250),

    /**
     * Ran first.
     */
    LOWEST(100),

    /**
     * Ran second.
     */
    LOW(200),

    /**
     * Ran third.
     */
    HIGH(300),

    /**
     * Ran last.
     */
    HIGHEST(400);

    /**
     * The display priority weight.
     */
    private final int weight;

    /**
     * Create new display priority.
     *
     * @param weight The weight.
     */
    DisplayPriority(final int weight) {
        this.weight = weight;
    }

    /**
     * Get the weight.
     *
     * @return The weight.
     */
    public int getWeight() {
        return weight;
    }
}
