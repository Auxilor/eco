package com.willfp.eco.proxy;

import com.willfp.eco.core.proxy.AbstractProxy;
import org.jetbrains.annotations.NotNull;

public interface AutoCraftProxy extends AbstractProxy {
    void modifyPacket(@NotNull Object packet) throws NoSuchFieldException, IllegalAccessException;
}
