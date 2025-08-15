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
            "v1_17_R1",
            "v1_18_R1",
            "v1_18_R2",
            "v1_19_R1",
            "v1_19_R2",
            "v1_19_R3",
            "v1_20_R1",
            "v1_20_R2",
            "v1_20_R3",
            "v1_21",
            "v1_21_3",
            "v1_21_4",
            "v1_21_5",
            "v1_21_7"
    );

    private ProxyConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static String convertVersion(@NotNull final String version) {
        return switch (version) {
            case "v1_21_1" -> "v1_21";
            case "v1_21_2" -> "v1_21_3";
            case "v1_21_6", "v1_21_8" -> "v1_21_7";
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
