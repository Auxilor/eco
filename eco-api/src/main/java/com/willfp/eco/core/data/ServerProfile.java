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
     * Load the server profile.
     *
     * @return The profile.
     */
    @NotNull
    static ServerProfile load() {
        return Eco.get().getServerProfile();
    }
}
