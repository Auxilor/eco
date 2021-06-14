package com.willfp.eco.internal.config;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JSONConfigSection extends JSONConfigWrapper {
    /**
     * Config section.
     *
     * @param values The values.
     */
    public JSONConfigSection(@NotNull final Map<String, Object> values) {
        this.init(values);
    }
}
