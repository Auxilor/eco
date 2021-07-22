package com.willfp.eco.internal.config.yaml;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
import com.willfp.eco.core.config.interfaces.WrappedYamlConfiguration;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class EcoLoadableYamlConfig extends EcoYamlConfigWrapper<YamlConfiguration> implements WrappedYamlConfiguration, LoadableConfig {
    @Getter
    private final File configFile;
    
    @Getter(AccessLevel.PROTECTED)
    private final EcoPlugin plugin;

    @Getter
    private final String name;
    
    @Getter(AccessLevel.PROTECTED)
    private final String subDirectoryPath;

    @Getter(AccessLevel.PROTECTED)
    private final Class<?> source;

    public EcoLoadableYamlConfig(@NotNull final String configName,
                                 @NotNull final EcoPlugin plugin,
                                 @NotNull final String subDirectoryPath,
                                 @NotNull final Class<?> source) {
        this.plugin = plugin;
        this.name = configName + ".yml";
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

        this.getPlugin().getConfigHandler().addConfig(this);
        init(YamlConfiguration.loadConfiguration(configFile));
    }

    public void reloadFromFile() {
        try {
            this.getHandle().load(this.getConfigFile());
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
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

        plugin.getConfigHandler().addConfig(this);
    }

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

    @Override
    public void save() throws IOException {
        this.getHandle().save(this.getConfigFile());
    }

    @Override
    public YamlConfiguration getBukkitHandle() {
        return this.getHandle();
    }
}
