package com.willfp.eco.core.gui.menu;

/**
 * The type of menu.
 */
public enum MenuType {
    /**
     * Normal menu (1x9, 2x9, 3x9, etc).
     */
    NORMAL(9, 6),

    /**
     * Dispenser menu (3x3).
     */
    DISPENSER(3, 3);

    /**
     * The amount of columns.
     */
    private final int columns;

    /**
     * The default amount of rows.
     */
    private final int defaultRows;

    /**
     * Create a new menu type.
     *
     * @param columns     The number of columns.
     * @param defaultRows The default number of rows.
     */
    MenuType(final int columns,
             final int defaultRows) {
        this.columns = columns;
        this.defaultRows = defaultRows;
    }

    /**
     * Get the amount of columns.
     *
     * @return The columns.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Get the default amount of rows.
     *
     * @return The default amount of rows.
     */
    public int getDefaultRows() {
        return defaultRows;
    }
}
