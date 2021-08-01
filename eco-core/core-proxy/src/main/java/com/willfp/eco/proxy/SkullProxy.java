package com.willfp.eco.proxy;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public interface SkullProxy extends AbstractProxy {
    void setSkullTexture(@NotNull SkullMeta meta,
                         @NotNull String base64);
}
