package com.willfp.eco.internal.config.json;

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
