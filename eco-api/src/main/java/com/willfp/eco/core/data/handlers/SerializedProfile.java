package com.willfp.eco.core.data.handlers;

import com.willfp.eco.core.data.keys.PersistentDataKey;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

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
