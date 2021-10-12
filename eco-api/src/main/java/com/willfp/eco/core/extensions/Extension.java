package com.willfp.eco.core.extensions;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.updating.ConfigHandler;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * An extension is a separate jar file that hooks into the base plugin jar.
 * <p>
 * If you take PlaceholderAPI as an example, the PAPI expansions are identical to
 * extensions.
 * <p>
 * Syntactically, extensions are very similar to plugins in their own right, except that
 * they are loaded by another plugin.
 *
 * @see <a href="https://auxilor.polymart.org">Extension examples.</a>
 */
public abstract class Extension implements PluginLike {
    /**
     * The {@link EcoPlugin} that this extension is for.
     */
    @Getter(AccessLevel.PROTECTED)
    private final EcoPlugin plugin;

    /**
     * Create a new extension for a plugin.
     *
     * @param plugin The plugin.
     */
    protected Extension(@NotNull final EcoPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Metadata containing version and name.
     */
    private ExtensionMetadata metadata = null;

    /**
     * Method to validate metadata and enable extension.
     */
    public final void enable() {
        Validate.notNull(metadata, "Metadata cannot be null!");
        this.onEnable();
    }

    /**
     * Method to disable extension.
     */
    public final void disable() {
        this.onDisable();
    }

    /**
     * Called on enabling Extension.
     */
    protected abstract void onEnable();

    /**
     * Called when Extension is disabled.
     */
    protected abstract void onDisable();

    /**
     * Set the metadata of the extension.
     * <p>
     * Must be called before enabling.
     *
     * @param metadata The metadata to set.
     */
    public final void setMetadata(@NotNull final ExtensionMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Get the name of the extension.
     *
     * @return The name of the metadata attached to the extension.
     */
    public final String getName() {
        Validate.notNull(metadata, "Metadata cannot be null!");
        return this.metadata.name();
    }

    /**
     * Get the author of the extension.
     *
     * @return The author of the metadata attached to the extension.
     */
    public final String getAuthor() {
        Validate.notNull(metadata, "Metadata cannot be null!");
        return this.metadata.author();
    }

    /**
     * Get the version of the extension.
     *
     * @return The version of the metadata attached to the extension.
     */
    public final String getVersion() {
        Validate.notNull(metadata, "Metadata cannot be null!");
        return this.metadata.version();
    }

    @Override
    public File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    @Override
    public ConfigHandler getConfigHandler() {
        return this.plugin.getConfigHandler();
    }
}
