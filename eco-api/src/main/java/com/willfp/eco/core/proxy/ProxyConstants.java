package com.willfp.eco.core.proxy;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Proxy / NMS constants.
 */
public final class ProxyConstants {
    /**
     * The NMS version that the server is running on.
     * <p>
     * Derived from the server's bukkit version, e.g. {@code 1.21.8} becomes
     * {@code v1_21_8}, then normalised so that versions sharing a proxy implementation
     * resolve to the same string.
     */
    public static final String NMS_VERSION;

    /**
     * All supported NMS versions.
     */
    public static final List<String> SUPPORTED_VERSIONS = Arrays.asList(
            "v1_21_8",
            "v1_21_10",
            "v1_21_11",
            "v26_1_2",
            "v26_2"
    );

    /**
     * Utility class, cannot be instantiated.
     */
    private ProxyConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Normalise a raw NMS version string.
     * <p>
     * Strips any paper build suffix (e.g. {@code v26_1_1_build_16}) and maps versions
     * that share a proxy implementation onto the version that actually provides it.
     *
     * @param version The raw version.
     * @return The normalised version.
     */
    private static String convertVersion(@NotNull final String version) {
        String normalized = version;

        // Paper API versions can include build suffixes (e.g. v26_1_1_build_16).
        int buildSuffixIndex = normalized.indexOf("_build_");
        if (buildSuffixIndex != -1) {
            normalized = normalized.substring(0, buildSuffixIndex);
        }

        return switch (normalized) {
            case "v1_21_9" -> "v1_21_10";
            case "v26_1_1" -> "v26_1_2";
            default -> normalized;
        };
    }

    static {
        String currentMinecraftVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        String nmsVersion = "v" + currentMinecraftVersion.replace(".", "_");

        NMS_VERSION = convertVersion(nmsVersion);
    }
}
