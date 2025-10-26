package com.willfp.eco.core.proxy;

import com.willfp.eco.core.version.Version;
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
            "v1_21_4",
            "v1_21_5",
            "v1_21_6",
            "v1_21_7",
            "v1_21_8",
            "v1_21_10"
    );

    private ProxyConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static String convertVersion(@NotNull final String version) {
        return switch (version) {
            case "v1_21_9" -> "v1_21_10";
            default -> version;
        };
    }

    static {
        String currentMinecraftVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        String nmsVersion;

        if (new Version(currentMinecraftVersion).compareTo(new Version("1.20.5")) < 0) {
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } else {
            nmsVersion = "v" + currentMinecraftVersion.replace(".", "_");
        }

        NMS_VERSION = convertVersion(nmsVersion);
    }
}
