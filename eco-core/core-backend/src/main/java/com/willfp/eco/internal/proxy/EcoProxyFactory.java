package com.willfp.eco.internal.proxy;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.proxy.AbstractProxy;
import com.willfp.eco.core.proxy.ProxyConstants;
import com.willfp.eco.core.proxy.exceptions.ProxyError;
import com.willfp.eco.core.proxy.ProxyFactory;
import com.willfp.eco.core.proxy.exceptions.UnsupportedVersionException;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class EcoProxyFactory extends PluginDependent<EcoPlugin> implements ProxyFactory {
    private static final List<String> SUPPORTED_VERSIONS = Arrays.asList(
            "v1_16_R3",
            "v1_17_R1"
    );

    private final ClassLoader proxyClassLoader;

    private final Map<Class<? extends AbstractProxy>, AbstractProxy> cache = new IdentityHashMap<>();

    public EcoProxyFactory(@NotNull final EcoPlugin plugin) {
        super(plugin);

        Validate.isTrue(
                !plugin.getProxyPackage().equalsIgnoreCase(""),
                "Specified plugin has no proxy support!"
        );

        this.proxyClassLoader = plugin.getClass().getClassLoader();
    }

    @Override
    public <T extends AbstractProxy> @NotNull T getProxy(@NotNull final Class<T> proxyClass) {
        try {
            T cachedProxy = attemptCache(proxyClass);
            if (cachedProxy != null) {
                return cachedProxy;
            }

            String className = this.getPlugin().getProxyPackage() + "." + ProxyConstants.NMS_VERSION + "." + proxyClass.getSimpleName().replace("Proxy", "");
            Class<?> clazz = this.getPlugin().getClass().getClassLoader().loadClass(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (proxyClass.isAssignableFrom(clazz) && proxyClass.isInstance(instance)) {
                T proxy = proxyClass.cast(instance);
                cache.put(proxyClass, proxy);
                return proxy;
            }
        } catch (Exception e) {
            throwError(e);
        }

        throwError(new IllegalArgumentException());

        throw new RuntimeException("Something went wrong.");
    }

    private void throwError(@Nullable final Exception e) {
        if (e != null) {
            e.printStackTrace();
        }

        if (!SUPPORTED_VERSIONS.contains(ProxyConstants.NMS_VERSION)) {
            throw new UnsupportedVersionException("You're running an unsupported server version: " + ProxyConstants.NMS_VERSION);
        } else {
            throw new ProxyError("Error with proxies - here's a stacktrace. Only god can help you now.");
        }
    }

    private <T extends AbstractProxy> T attemptCache(@NotNull final Class<T> proxyClass) {
        Object proxy = cache.get(proxyClass);
        if (proxy == null) {
            return null;
        }

        if (proxyClass.isInstance(proxy)) {
            return proxyClass.cast(proxy);
        }

        return null;
    }

    public void clean() {
        try {
            if (proxyClassLoader instanceof URLClassLoader urlClassLoader) {
                urlClassLoader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        cache.clear();
    }
}
