package com.willfp.eco.util;

import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities / API methods for vectors.
 */
public final class VectorUtils {
    /**
     * Cached circles to prevent many sqrt calls.
     */
    private static final Map<Integer, Vector[]> CIRCLE_CACHE = new HashMap<>();

    /**
     * If vector has all components as finite.
     *
     * @param vector The vector to check.
     * @return If the vector is finite.
     */
    public static boolean isFinite(@NotNull final Vector vector) {
        try {
            NumberConversions.checkFinite(vector.getX(), "x not finite");
            NumberConversions.checkFinite(vector.getY(), "y not finite");
            NumberConversions.checkFinite(vector.getZ(), "z not finite");
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * Only keep largest part of normalised vector.
     * For example: -0.8, 0.01, -0.2 would become -1, 0, 0.
     *
     * @param vec The vector to simplify.
     * @return The vector, simplified.
     */
    @NotNull
    public static Vector simplifyVector(@NotNull final Vector vec) {
        double x = Math.abs(vec.getX());
        double y = Math.abs(vec.getY());
        double z = Math.abs(vec.getZ());
        double max = Math.max(x, Math.max(y, z));
        if (x > 1 || z > 1) {
            max = y;
        }
        if (max == x) {
            if (vec.getX() < 0) {
                return new Vector(-1, 0, 0);
            }
            return new Vector(1, 0, 0);
        } else if (max == y) {
            if (vec.getY() < 0) {
                return new Vector(0, -1, 0);
            }
            return new Vector(0, 1, 0);
        } else {
            if (vec.getZ() < 0) {
                return new Vector(0, 0, -1);
            }
            return new Vector(0, 0, 1);
        }
    }

    /**
     * Get circle as relative vectors.
     *
     * @param radius The radius.
     * @return An array of {@link Vector}s.
     */
    @NotNull
    public static Vector[] getCircle(final int radius) {
        Vector[] cached = CIRCLE_CACHE.get(radius);
        if (cached != null) {
            return cached;
        }

        List<Vector> vectors = new ArrayList<>();

        double xoffset = -radius;
        double zoffset = -radius;

        while (zoffset <= radius) {
            while (xoffset <= radius) {
                if (Math.round(Math.sqrt((xoffset * xoffset) + (zoffset * zoffset))) <= radius) {
                    vectors.add(new Vector(xoffset, 0, zoffset));
                } else {
                    xoffset++;
                    continue;
                }
                xoffset++;
            }
            xoffset = -radius;
            zoffset++;
        }

        Vector[] result = vectors.toArray(new Vector[0]);
        CIRCLE_CACHE.put(radius, result);
        return result;
    }

    /**
     * Get square as relative vectors.
     *
     * @param radius The radius of the square.
     * @return An array of {@link Vector}s.
     */
    @NotNull
    public static Vector[] getSquare(final int radius) {
        List<Vector> vectors = new ArrayList<>();

        int xoffset = -radius;
        int zoffset = -radius;

        while (zoffset <= radius) {
            while (xoffset <= radius) {
                vectors.add(new Vector(xoffset, 0, zoffset));
                xoffset++;
            }
            xoffset = -radius;
            zoffset++;
        }

        return vectors.toArray(new Vector[0]);
    }

    /**
     * Get cube as relative vectors.
     *
     * @param radius The radius of the cube.
     * @return An array of {@link Vector}s.
     */
    @NotNull
    public static Vector[] getCube(final int radius) {
        List<Vector> vectors = new ArrayList<>();

        for (int y = -radius; y <= radius; y++) {
            for (int z = -radius; z <= radius; z++) {
                for (int x = -radius; x <= radius; x++) {
                    vectors.add(new Vector(x, y, z));
                }
            }
        }

        return vectors.toArray(new Vector[0]);
    }

    /**
     * Get if a vector is a safe velocity.
     *
     * @param vec The vector to check.
     * @return If safe.
     */
    public static boolean isSafeVelocity(@NotNull final Vector vec) {
        double x = Math.abs(vec.getX());
        double y = Math.abs(vec.getY());
        double z = Math.abs(vec.getZ());

        return x < 4 && y < 4 && z < 4;
    }

    private VectorUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
