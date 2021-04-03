package com.willfp.eco.util;

import com.willfp.eco.util.config.Config;
import com.willfp.eco.util.serialization.Deserializer;
import com.willfp.eco.util.serialization.EcoSerializable;
import com.willfp.eco.util.serialization.NoRegisteredDeserializerException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class SerializationUtils {
    /**
     * All registered deserializers.
     */
    private static final Map<String, Deserializer<?>> REGISTRY = new HashMap<>();

    /**
     * Register deserializer.
     *
     * @param clazz        The serializable class.
     * @param deserializer The deserializer.
     * @param <T>          The object type.
     */
    public <T extends EcoSerializable<T>> void registerDeserializer(@NotNull final Class<T> clazz,
                                                                    @NotNull final Deserializer<T> deserializer) {
        REGISTRY.put(clazz.getName(), deserializer);
    }

    /**
     * Deserialize object.
     *
     * @param config The config.
     * @param clazz  The class.
     * @param <T>    The object type.
     */
    @SneakyThrows
    public <T extends EcoSerializable<T>> T deserialize(@NotNull final Config config,
                                                        @NotNull final Class<T> clazz) {
        if (!REGISTRY.containsKey(clazz.getName())) {
            throw new NoRegisteredDeserializerException("No deserializer registered for " + clazz.getName());
        }

        return clazz.cast(REGISTRY.get(clazz.getName()).deserialize(config));
    }
}
