package com.willfp.eco.core.serialization;

import org.bukkit.Keyed;

/**
 * Serialize objects to configs.
 * <p>
 * Has a key.
 *
 * @param <T> The type of object to serialize.
 */
public interface KeyedSerializer<T> extends ConfigSerializer<T>, Keyed {

}
