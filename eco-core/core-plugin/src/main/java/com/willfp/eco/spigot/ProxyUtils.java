package com.willfp.eco.spigot;

import com.willfp.eco.core.proxy.AbstractProxy;
import com.willfp.eco.proxy.util.ProxyFactory;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class ProxyUtils {
    public <T extends AbstractProxy> T getProxy(@NotNull final Class<T> proxy) {
        return new ProxyFactory<>(EcoSpigotPlugin.getInstance(), proxy).getProxy();
    }
}
