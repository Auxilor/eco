package com.willfp.eco.core.serialization;

import org.bukkit.Keyed;

/**
 * Deserialize objects from configs.
 * <p>
 * Has a key.
 *
 * @param <T> The type of object to deserialize.
 */
public interface KeyedDeserializer<T> extends ConfigDeserializer<T>, Keyed {

}
