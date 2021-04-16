package com.willfp.eco.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ListUtils {
    /**
     * Initialize 2D list of a given size.
     *
     * @param rows    The amount of rows.
     * @param columns The amount of columns.
     * @param <T>     The type of the object stored in the list.
     * @return The list, filled will null objects.
     */
    public <T> List<List<T>> create2DList(final int rows,
                                          final int columns) {
        List<List<T>> list = new ArrayList<>(rows);
        while (list.size() < rows) {
            List<T> row = new ArrayList<>(columns);
            while (row.size() < columns) {
                row.add(null);
            }
            list.add(row);
        }

        return list;
    }
}
