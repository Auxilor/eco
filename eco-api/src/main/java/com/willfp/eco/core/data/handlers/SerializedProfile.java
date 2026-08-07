package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * A snapshot of every stored value for a profile, used to move data between
 * {@link PersistentDataHandler}s.
 *
 * @param uuid The uuid of the profile.
 * @param data The stored values, keyed by the persistent data key they belong to.
 */
public record SerializedProfile(
        @NotNull UUID uuid,
        @NotNull Map<PersistentDataKey<?>, Object> data
) {

}
