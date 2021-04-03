package com.willfp.eco.proxy.proxies;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public interface SkullProxy extends AbstractProxy {
    /**
     * Set the texture of a skull from base64.
     *
     * @param meta   The meta to modify.
     * @param base64 The base64 texture.
     */
    void setSkullTexture(@NotNull SkullMeta meta,
                         @NotNull String base64);
}
