package com.willfp.eco.core.version;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * A minimal class to represent a version with semantic comparison.
 */
public class Version implements Comparable<Version> {
    private final String raw;
    private final int[] parts;
    private final String qualifier;

    /**
     * Create a new version.
     *
     * @param version The version string (e.g. "1.2.3" or "1.2.3-SNAPSHOT").
     */
    public Version(@NotNull final String version) {
        this.raw = version;
        int dashIdx = version.indexOf('-');
        String numeric = dashIdx >= 0 ? version.substring(0, dashIdx) : version;
        this.qualifier = dashIdx >= 0 ? version.substring(dashIdx + 1) : "";
        String[] segments = numeric.split("\\.", -1);
        this.parts = new int[segments.length];
        for (int i = 0; i < segments.length; i++) {
            try {
                this.parts[i] = Integer.parseInt(segments[i]);
            } catch (NumberFormatException e) {
                this.parts[i] = 0;
            }
        }
    }

    @Override
    public int compareTo(@NotNull final Version o) {
        int maxLen = Math.max(this.parts.length, o.parts.length);
        for (int i = 0; i < maxLen; i++) {
            int a = i < this.parts.length ? this.parts[i] : 0;
            int b = i < o.parts.length ? o.parts[i] : 0;
            if (a != b) {
                return Integer.compare(a, b);
            }
        }
        // No qualifier beats a qualifier (1.0 > 1.0-SNAPSHOT)
        boolean thisHasQual = !this.qualifier.isEmpty();
        boolean otherHasQual = !o.qualifier.isEmpty();
        if (thisHasQual == otherHasQual) {
            return this.qualifier.compareTo(o.qualifier);
        }
        return thisHasQual ? -1 : 1;
    }

    @Override
    public String toString() {
        return raw;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Version other)) {
            return false;
        }
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        // Normalize trailing zero parts so "1.0" and "1.0.0" hash identically
        int len = parts.length;
        while (len > 1 && parts[len - 1] == 0) {
            len--;
        }
        int result = Arrays.hashCode(Arrays.copyOf(parts, len));
        return qualifier.isEmpty() ? result : 31 * result + qualifier.hashCode();
    }
}