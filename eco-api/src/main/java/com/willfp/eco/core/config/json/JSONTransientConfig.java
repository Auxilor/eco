package com.willfp.eco.core.config.json;

import com.willfp.eco.core.Eco;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.config.json.wrapper.JSONConfigWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Raw JSON config with a map of values at its core.
 *
 * @deprecated JSON and yml have full parity, use configs without a prefix instead,
 * eg {@link com.willfp.eco.core.config.TransientConfig}, {@link com.willfp.eco.core.config.BaseConfig}.
 * These configs will be removed eventually.
 */
@Deprecated(forRemoval = true)
public class JSONTransientConfig extends JSONConfigWrapper {
    /**
     * Config implementation for passing maps.
     * <p>
     * Does not automatically update.
     *
     * @param values The map of values.
     */
    public JSONTransientConfig(@NotNull final Map<String, Object> values) {
        super((JSONConfig) Eco.getHandler().getConfigFactory().createConfig(values));
    }

    /**
     * Empty JSON config.
     */
    public JSONTransientConfig() {
        super((JSONConfig) Eco.getHandler().getConfigFactory().createConfig(new HashMap<>()));
    }
}
