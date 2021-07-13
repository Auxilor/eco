package com.willfp.eco.internal.config.json;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JsonConfigSection extends JsonConfigWrapper {
    /**
     * Config section.
     *
     * @param values The values.
     */
    public JsonConfigSection(@NotNull final Map<String, Object> values) {
        this.init(values);
    }
}
