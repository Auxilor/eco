package com.willfp.eco.core.serialization;

import org.bukkit.Keyed;

/**
 * Serializer with a key.
 *
 * @param <T> The type of object to serialize.
 * @see ConfigSerializer
 */
public interface KeyedSerializer<T> extends ConfigSerializer<T>, Keyed {

}
