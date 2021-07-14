package com.willfp.eco.core.config.json;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.json.wrapper.JSONConfigWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class JSONConfig extends JSONConfigWrapper {
    /**
     * Config implementation for passing maps.
     * <p>
     * Does not automatically update.
     *
     * @param values The map of values.
     */
    public JSONConfig(@NotNull final Map<String, Object> values) {
        super(Eco.getHandler().getConfigFactory().createJSONConfig(values));
    }
}
