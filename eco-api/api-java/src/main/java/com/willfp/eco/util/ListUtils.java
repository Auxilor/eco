package com.willfp.eco.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities / API methods for lists.
 */
public final class ListUtils {
    /**
     * Initialize 2D list of a given size.
     *
     * @param rows    The amount of rows.
     * @param columns The amount of columns.
     * @param <T>     The type of the object stored in the list.
     * @return The list, filled will null objects.
     */
    @NotNull
    public static <T> List<List<T>> create2DList(final int rows,
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

    /**
     * Convert a list potentially containing duplicates to a map where the value is the frequency of the key.
     *
     * @param list The list.
     * @param <T>  The type parameter of the list.
     * @return The frequency map.
     */
    @NotNull
    public static <T> Map<T, Integer> listToFrequencyMap(@NotNull final List<T> list) {
        Map<T, Integer> frequencyMap = new HashMap<>();
        for (T object : list) {
            if (frequencyMap.containsKey(object)) {
                frequencyMap.put(object, frequencyMap.get(object) + 1);
            } else {
                frequencyMap.put(object, 1);
            }
        }

        return frequencyMap;
    }

    /**
     * Convert nullable object to either singleton list or empty list.
     *
     * @param object The object.
     * @param <T>    The type of the object.
     * @return The list.
     */
    @NotNull
    public static <T> List<T> toSingletonList(@Nullable final T object) {
        if (object == null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(object);
        }
    }

    /**
     * Get element from list or return null if out of bounds.
     *
     * @param list  The list.
     * @param index The index.
     * @param <T>   The type of the list.
     * @return The found element, or null if out of bounds.
     */
    @Nullable
    public static <T> T getOrNull(@Nullable final List<T> list,
                                  final int index) {
        if (list == null) {
            return null;
        }

        return index >= 0 && index < list.size() ? list.get(index) : null;
    }

    /**
     * Get if an iterable of strings contains a certain element regardless of case.
     *
     * @param list    The list.
     * @param element The element.
     * @return If contained.
     */
    public static boolean containsIgnoreCase(@NotNull final Iterable<String> list,
                                             @NotNull final String element) {
        for (String s : list) {
            if (s.equalsIgnoreCase(element)) {
                return true;
            }
        }

        return false;
    }

    private ListUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
