package com.willfp.eco.core.recipe.recipes;

/**
 * Positions in a 3x3 crafting recipe.
 */
public enum RecipePosition {
    /**
     * Top left of matrix.
     */
    TOP_LEFT(0),

    /**
     * Top middle of matrix.
     */
    TOP_MIDDLE(1),

    /**
     * Top right of matrix.
     */
    TOP_RIGHT(2),

    /**
     * Middle left of matrix.
     */
    MIDDLE_LEFT(3),

    /**
     * Middle of matrix.
     */
    MIDDLE(4),

    /**
     * Middle right of matrix.
     */
    MIDDLE_RIGHT(5),

    /**
     * Bottom left of matrix.
     */
    BOTTOM_LEFT(6),

    /**
     * Bottom middle of matrix.
     */
    BOTTOM_MIDDLE(7),

    /**
     * Bottom right of matrix.
     */
    BOTTOM_RIGHT(8);

    /**
     * The index within a crafting table matrix.
     */
    private final int index;

    /**
     * Recipe position with crafting table index.
     *
     * @param index The index.
     */
    RecipePosition(final int index) {
        this.index = index;
    }

    /**
     * Get the index within a crafting table matrix.
     *
     * @return The index.
     */
    public int getIndex() {
        return index;
    }
}
