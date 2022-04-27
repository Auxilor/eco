package com.willfp.eco.core.display;

/**
 * The priority (order) of display modules.
 */
public enum DisplayPriority {
    /**
     * Custom weight.
     */
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
