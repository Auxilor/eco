package com.willfp.eco.core.floodgate;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The operations {@link FloodgateService} needs from Floodgate.
 * <p>
 * No Floodgate type appears anywhere in this interface, so that {@link FloodgateService} can
 * be loaded, and can decide whether Floodgate is present, without the JVM ever having to
 * resolve a Floodgate class. Only {@link FloodgateBridgeImpl} references them.
 */
interface FloodgateBridge {
    /**
     * Get the Floodgate username prefix.
     *
     * @return The prefix.
     */
    @NotNull
    String getPrefix();

    /**
     * If the online player with the given UUID is a Bedrock player.
     *
     * @param uuid The UUID.
     * @return If the player is a Bedrock player.
     */
    boolean isBedrockPlayer(@NotNull UUID uuid);

    /**
     * If the given UUID has the format Floodgate generates for unlinked Bedrock players.
     *
     * @param uuid The UUID.
     * @return If the UUID is a Floodgate UUID.
     */
    boolean isBedrockUniqueId(@NotNull UUID uuid);

    /**
     * Find the UUID of an online Bedrock player by name.
     *
     * @param name     The name as given, which may include the prefix.
     * @param gamertag The name with any prefix removed.
     * @return The UUID, or null if no online Bedrock player matches.
     */
    @Nullable
    UUID findUniqueId(@NotNull String name,
                      @NotNull String gamertag);

    /**
     * Look up the Floodgate UUID for a gamertag through the Geyser global API.
     *
     * @param gamertag The gamertag, without a prefix.
     * @return A future completing with the UUID, or with null if there is no such gamertag.
     */
    @NotNull
    CompletableFuture<UUID> fetchUniqueId(@NotNull String gamertag);

    /**
     * Get the raw Bedrock gamertag of an online player.
     *
     * @param uuid The UUID.
     * @return The gamertag, or null if the player isn't an online Bedrock player.
     */
    @Nullable
    String getGamertag(@NotNull UUID uuid);
}
