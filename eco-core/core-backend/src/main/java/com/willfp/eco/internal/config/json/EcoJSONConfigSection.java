package com.willfp.eco.internal.config.json;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class EcoJSONConfigSection extends EcoJSONConfigWrapper {
    /**
     * Config section.
     *
     * @param values The values.
     */
    public EcoJSONConfigSection(@NotNull final Map<String, Object> values) {
        this.init(values);
    }
}
