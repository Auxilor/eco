package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

/**
 * Serialized profile.
 *
 * @param uuid The uuid.
 * @param data The data.
 */
public record SerializedProfile(
        @NotNull UUID uuid,
        @NotNull Map<PersistentDataKey<?>, Object> data
) {

}
