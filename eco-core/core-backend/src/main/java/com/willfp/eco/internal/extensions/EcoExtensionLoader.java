package com.willfp.eco.internal.extensions;


import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.config.yaml.YamlTransientConfig;
import com.willfp.eco.core.extensions.Extension;
import com.willfp.eco.core.extensions.ExtensionLoader;
import com.willfp.eco.core.extensions.ExtensionMetadata;
import com.willfp.eco.core.extensions.MalformedExtensionException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EcoExtensionLoader extends PluginDependent<EcoPlugin> implements ExtensionLoader {
    private final Map<Extension, ClassLoader> extensions = new HashMap<>();

    public EcoExtensionLoader(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @Override
    public void loadExtensions() {
        File dir = new File(this.getPlugin().getDataFolder(), "/extensions");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File[] extensionJars = dir.listFiles();

        if (extensionJars == null) {
            return;
        }

        for (File extensionJar : extensionJars) {
            if (!extensionJar.isFile()) {
                continue;
            }

            try {
                loadExtension(extensionJar);
            } catch (MalformedExtensionException e) {
                this.getPlugin().getLogger().warning(extensionJar.getName() + " caused an error!");
            }
        }
    }

    private void loadExtension(@NotNull final File extensionJar) throws MalformedExtensionException {
        URL url = null;
        try {
            url = extensionJar.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        ClassLoader classLoader = new URLClassLoader(new URL[]{url}, this.getPlugin().getClass().getClassLoader());

        InputStream ymlIn = classLoader.getResourceAsStream("extension.yml");

        if (ymlIn == null) {
            throw new MalformedExtensionException("No extension.yml found in " + extensionJar.getName());
        }

        Config extensionYml = new YamlTransientConfig(YamlConfiguration.loadConfiguration(new InputStreamReader(ymlIn)));

        String mainClass = extensionYml.getStringOrNull("main");
        String name = extensionYml.getStringOrNull("name");
        String version = extensionYml.getStringOrNull("version");
        String author = extensionYml.getStringOrNull("author");

        if (mainClass == null) {
            throw new MalformedExtensionException("Invalid extension.yml found in " + extensionJar.getName());
        }

        if (name == null) {
            this.getPlugin().getLogger().warning(extensionJar.getName() + " doesn't have a name!");
            name = "Unnamed Extension " + extensionJar.getName();
        }

        if (version == null) {
            this.getPlugin().getLogger().warning(extensionJar.getName() + " doesn't have a version!");
            version = "1.0.0";
        }

        if (author == null) {
            this.getPlugin().getLogger().warning(extensionJar.getName() + " doesn't have an author!");
            author = "Unnamed Author";
        }

        ExtensionMetadata metadata = new ExtensionMetadata(version, name, author);

        Class<?> cls;
        Object object = null;
        try {
            cls = classLoader.loadClass(mainClass);
            object = cls.getConstructor(EcoPlugin.class).newInstance(this.getPlugin());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        if (!(object instanceof Extension extension)) {
            throw new MalformedExtensionException(extensionJar.getName() + " is invalid");
        }

        extension.setMetadata(metadata);
        extension.enable();
        extensions.put(extension, classLoader);
    }

    @Override
    public void unloadExtensions() {
        extensions.keySet().forEach(Extension::disable);
        for (ClassLoader loader : extensions.values()) {
            try {
                ((URLClassLoader) loader).close();
            } catch (IOException e) {
                // Do nothing.
            }
        }
        extensions.clear();
    }

    @Override
    public Set<Extension> getLoadedExtensions() {
        return extensions.keySet();
    }
}
