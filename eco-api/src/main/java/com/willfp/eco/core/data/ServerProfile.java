package com.willfp.eco.core.data;

import com.willfp.eco.core.Eco;
import org.jetbrains.annotations.NotNull;

/**
 * Persistent data storage interface for servers.
 * <p>
 * Profiles save automatically, so there is no need to save after changes.
 */
public interface ServerProfile extends Profile {
    /**
     * Get the server ID.
     * <p>
     * The ID is a random UUID string, generated and persisted on first access. It is
     * stored with the configured data handler, so servers sharing a database will
     * report the same ID.
     *
     * @return The server ID.
     */
    @NotNull
    String getServerID();

    /**
     * Get the local server ID.
     * <p>
     * Like {@link #getServerID()}, but always stored in local storage, so it is unique
     * to this server even when several servers share a database.
     *
     * @return The local server ID.
     */
    @NotNull
    String getLocalServerID();

    /**
     * Load the server profile.
     *
     * @return The profile.
     */
    @NotNull
    static ServerProfile load() {
        return Eco.get().getServerProfile();
    }
}
