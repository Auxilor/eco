package com.willfp.eco.core.floodgate;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The only class in eco that touches Floodgate directly.
 * <p>
 * Constructing this resolves the Floodgate API eagerly, so that a missing or incompatible
 * Floodgate fails once, at hook time, rather than partway through a command.
 */
final class FloodgateBridgeImpl implements FloodgateBridge {
    /**
     * The Floodgate API.
     */
    private final FloodgateApi api;

    /**
     * Hook into Floodgate.
     *
     * @throws Throwable If Floodgate is absent or its API could not be resolved.
     */
    FloodgateBridgeImpl() {
        this.api = Objects.requireNonNull(FloodgateApi.getInstance(), "Floodgate API is null");
    }

    @NotNull
    @Override
    public String getPrefix() {
        String prefix = this.api.getPlayerPrefix();

        return prefix == null ? "" : prefix;
    }

    @Override
    public boolean isBedrockPlayer(@NotNull final UUID uuid) {
        return this.api.isFloodgatePlayer(uuid);
    }

    @Override
    public boolean isBedrockUniqueId(@NotNull final UUID uuid) {
        return this.api.isFloodgateId(uuid);
    }

    @Nullable
    @Override
    public UUID findUniqueId(@NotNull final String name,
                             @NotNull final String gamertag) {
        for (FloodgatePlayer player : this.api.getPlayers()) {
            /*
            The gamertag is the raw Bedrock name, and the correct username is what the server
            shows: the prefixed name for unlinked players, or the linked Java account's name.
            Either is a reasonable thing for someone to have typed.
             */
            if (gamertag.equalsIgnoreCase(player.getUsername())
                    || name.equalsIgnoreCase(player.getCorrectUsername())) {
                return player.getCorrectUniqueId();
            }
        }

        return null;
    }

    @NotNull
    @Override
    public CompletableFuture<UUID> fetchUniqueId(@NotNull final String gamertag) {
        return this.api.getUuidFor(gamertag);
    }

    @Nullable
    @Override
    public String getGamertag(@NotNull final UUID uuid) {
        FloodgatePlayer player = this.api.getPlayer(uuid);

        return player == null ? null : player.getUsername();
    }
}
