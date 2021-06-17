package com.willfp.eco.internal.config.json;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.internal.config.LoadableConfig;
import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

@SuppressWarnings({"unchecked", "unused"})
public abstract class LoadableJsonConfig extends JSONConfigWrapper implements LoadableConfig {
    /**
     * The physical config file, as stored on disk.
     */
    @Getter
    private final File configFile;

    /**
     * Plugin handle.
     */
    @Getter(AccessLevel.PROTECTED)
    private final EcoPlugin plugin;

    /**
     * The full name of the config file (eg config.json).
     */
    @Getter
    private final String name;

    /**
     * The subdirectory path.
     */
    @Getter(AccessLevel.PROTECTED)
    private final String subDirectoryPath;

    /**
     * The provider of the config.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Class<?> source;

    /**
     * Abstract config.
     *
     * @param configName       The name of the config
     * @param plugin           The plugin.
     * @param subDirectoryPath The subdirectory path.
     * @param source           The class that owns the resource.
     */
    protected LoadableJsonConfig(@NotNull final String configName,
                                 @NotNull final EcoPlugin plugin,
                                 @NotNull final String subDirectoryPath,
                                 @NotNull final Class<?> source) {
        this.plugin = plugin;
        this.name = configName + ".json";
        this.source = source;
        this.subDirectoryPath = subDirectoryPath;

        File directory = new File(this.getPlugin().getDataFolder(), subDirectoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (!new File(directory, this.name).exists()) {
            createFile();
        }

        this.configFile = new File(directory, this.name);

        try {
            init(this.configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        plugin.getConfigHandler().addConfig(this);
    }

    @Override
    public void createFile() {
        String resourcePath = getResourcePath();
        InputStream in = source.getResourceAsStream(resourcePath);

        File outFile = new File(this.getPlugin().getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(this.getPlugin().getDataFolder(), resourcePath.substring(0, Math.max(lastIndex, 0)));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ignored) {
        }
    }

    /**
     * Get resource path as relative to base directory.
     *
     * @return The resource path.
     */
    @Override
    public String getResourcePath() {
        String resourcePath;

        if (subDirectoryPath.isEmpty()) {
            resourcePath = name;
        } else {
            resourcePath = subDirectoryPath + name;
        }

        return "/" + resourcePath;
    }

    /**
     * Save the config.
     *
     * @throws IOException If error in saving.
     */
    @Override
    public void save() throws IOException {
        configFile.delete();
        Files.write(configFile.toPath(), this.toPlaintext().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    /**
     * Initialize the config.
     *
     * @param file The config file.
     * @throws FileNotFoundException If the file doesn't exist.
     */
    public void init(@NotNull final File file) throws FileNotFoundException {
        super.init(this.getHandle().fromJson(new FileReader(file), HashMap.class));
    }
}
