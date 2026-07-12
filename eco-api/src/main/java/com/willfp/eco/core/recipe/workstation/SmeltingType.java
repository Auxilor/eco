package com.willfp.eco.core.recipe.workstation;

/**
 * The smelting workstation type for a {@link SmeltingRecipe}.
 */
public enum SmeltingType {
    /**
     * Standard furnace. Default cook time: 200 ticks.
     */
    FURNACE,

    /**
     * Blast furnace; smelts ores twice as fast. Default cook time: 100 ticks.
     */
    BLAST_FURNACE,

    /**
     * Smoker; cooks food twice as fast. Default cook time: 100 ticks.
     */
    SMOKER,

    /**
     * Campfire; slow outdoor cooking. Default cook time: 600 ticks.
     */
    CAMPFIRE
}
