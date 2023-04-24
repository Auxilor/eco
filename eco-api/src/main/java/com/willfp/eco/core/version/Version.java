package com.willfp.eco.core.version;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jetbrains.annotations.NotNull;

/**
 * A minimal class to represent a version, uses DefaultArtifactVersion under the hood.
 * <p>
 * This class exists to resolve issues where 1.17 doesn't include DefaultArtifactVersion.
 */
public class Version implements Comparable<Version> {
    /**
     * The version.
     */
    private final DefaultArtifactVersion version;

    /**
     * Create a new version.
     *
     * @param version The version.
     */
    public Version(@NotNull final String version) {
        this.version = new DefaultArtifactVersion(version);
    }

    @Override
    public int compareTo(@NotNull final Version o) {
        return this.version.compareTo(o.version);
    }

    @Override
    public String toString() {
        return this.version.toString();
    }

    @Override
    public boolean equals(@NotNull final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Version other)) {
            return false;
        }

        return this.version.equals(other.version);
    }

    @Override
    public int hashCode() {
        return this.version.hashCode();
    }
}
