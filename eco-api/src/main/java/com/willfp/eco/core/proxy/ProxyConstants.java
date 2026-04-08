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
     */
    public static final String NMS_VERSION;

    /**
     * All supported NMS versions.
     */
    public static final List<String> SUPPORTED_VERSIONS = Arrays.asList(
            "v1_21_8",
            "v1_21_10",
            "v1_21_11",
            "v26_1_1"
    );

    private ProxyConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static String convertVersion(@NotNull final String version) {
        String normalized = version;

        // Paper API versions can include build suffixes (e.g. v26_1_1_build_16).
        int buildSuffixIndex = normalized.indexOf("_build_");
        if (buildSuffixIndex != -1) {
            normalized = normalized.substring(0, buildSuffixIndex);
        }

        return switch (normalized) {
            case "v1_21_9" -> "v1_21_10";
            default -> normalized;
        };
    }

    static {
        String currentMinecraftVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        String nmsVersion = "v" + currentMinecraftVersion.replace(".", "_");

        NMS_VERSION = convertVersion(nmsVersion);
    }
}
