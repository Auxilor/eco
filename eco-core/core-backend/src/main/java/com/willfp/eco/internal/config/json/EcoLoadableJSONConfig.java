package com.willfp.eco.internal.config.json;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.config.interfaces.LoadableConfig;
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
public class EcoLoadableJSONConfig extends EcoJSONConfigWrapper implements LoadableConfig {
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

    public EcoLoadableJSONConfig(@NotNull final String configName,
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
        configFile.delete();
        Files.write(configFile.toPath(), this.toPlaintext().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
    }

    
    public void init(@NotNull final File file) throws FileNotFoundException {
        super.init(this.getHandle().fromJson(new FileReader(file), HashMap.class));
    }
}
