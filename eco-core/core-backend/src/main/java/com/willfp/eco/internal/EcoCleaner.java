package com.willfp.eco.internal;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.proxy.Cleaner;
import com.willfp.eco.internal.proxy.EcoProxyFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URLClassLoader;

public class EcoCleaner implements Cleaner {
    @Override
    public void clean(@NotNull final EcoPlugin plugin) {
        if (!plugin.getProxyPackage().equalsIgnoreCase("")) {
            EcoProxyFactory factory = (EcoProxyFactory) plugin.getProxyFactory();
            factory.clean();
        }

        Plugins.LOADED_ECO_PLUGINS.remove(plugin.getName().toLowerCase());

        if (plugin.getClass().getClassLoader() instanceof URLClassLoader urlClassLoader) {
            try {
                urlClassLoader.close();
            } catch (IOException e) {
                // Do nothing.
            }
        }

        System.gc();
    }
}
