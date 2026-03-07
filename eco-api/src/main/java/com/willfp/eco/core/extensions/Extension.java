package com.willfp.eco.core.extensions;

import com.google.common.base.Preconditions;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginLike;
import com.willfp.eco.core.config.updating.ConfigHandler;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

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
        Preconditions.checkNotNull(metadata, "Metadata cannot be null!");
        this.onEnable();
    }

    /**
     * Method to disable extension.
     */
    public final void disable() {
        this.onDisable();
    }

    /**
     * Method to handle after load.
     */
    public final void handleAfterLoad() {
        this.onAfterLoad();
    }

    /**
     * Method to handle plugin reloads.
     */
    public final void handleReload() {
        this.onReload();
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
     * Called the once the base plugin is done loading.
     */
    protected void onAfterLoad() {
        // Override if needed
    }

    /**
     * Called on plugin reload.
     */
    protected void onReload() {
        // Override if needed
    }

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
        Preconditions.checkNotNull(metadata, "Metadata cannot be null!");
        return this.metadata.name();
    }

    /**
     * Get the author of the extension.
     *
     * @return The author of the metadata attached to the extension.
     */
    public final String getAuthor() {
        Preconditions.checkNotNull(metadata, "Metadata cannot be null!");
        return this.metadata.author();
    }

    /**
     * Get the version of the extension.
     *
     * @return The version of the metadata attached to the extension.
     */
    public final String getVersion() {
        Preconditions.checkNotNull(metadata, "Metadata cannot be null!");
        return this.metadata.version();
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.plugin.getDataFolder();
    }

    @Override
    public @NotNull ConfigHandler getConfigHandler() {
        return this.plugin.getConfigHandler();
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.plugin.getLogger();
    }

    @Override
    public @NotNull File getFile() {
        Preconditions.checkNotNull(metadata, "Metadata cannot be null!");
        return this.metadata.file();
    }

    /**
     * Get the plugin for the extension.
     *
     * @return The plugin.
     */
    protected EcoPlugin getPlugin() {
        return this.plugin;
    }
}
