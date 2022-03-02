package com.willfp.eco.core.serialization;

import org.bukkit.Keyed;

/**
 * Deserializer with a key.
 *
 * @param <T> The type of object to deserialize.
 * @see ConfigDeserializer
 */
public interface KeyedDeserializer<T> extends ConfigDeserializer<T>, Keyed {

}
