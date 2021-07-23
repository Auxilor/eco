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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

public class EcoExtensionLoader extends PluginDependent<EcoPlugin> implements ExtensionLoader {
    private final Set<Extension> extensions = new HashSet<>();

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

        ClassLoader cl = new URLClassLoader(new URL[]{url}, this.getPlugin().getClass().getClassLoader());

        InputStream ymlIn = cl.getResourceAsStream("extension.yml");

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
            name = "Unnamed Extension " + extensionJar.getName();
        }

        if (version == null) {
            version = "1.0.0";
        }

        if (author == null) {
            author = "Unnamed Author";
        }

        ExtensionMetadata metadata = new ExtensionMetadata(version, name, author);

        Class<?> cls;
        Object object = null;
        try {
            cls = cl.loadClass(mainClass);
            object = cls.getConstructor(EcoPlugin.class).newInstance(this.getPlugin());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        if (!(object instanceof Extension extension)) {
            throw new MalformedExtensionException(extensionJar.getName() + " is invalid");
        }

        extension.setMetadata(metadata);
        extension.enable();
        extensions.add(extension);
    }

    @Override
    public void unloadExtensions() {
        extensions.forEach(Extension::disable);
        extensions.clear();
    }

    @Override
    public Set<Extension> getLoadedExtensions() {
        return extensions;
    }
}
