package com.willfp.eco.util;

import org.jetbrains.annotations.NotNull;

/**
 * Utilities / API methods for classes.
 */
public final class ClassUtils {
    /**
     * Get if a class exists.
     *
     * @param className The class to check.
     * @return If the class exists.
     * @see Class#forName(String)
     */
    public static boolean exists(@NotNull final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private ClassUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
