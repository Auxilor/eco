package com.willfp.eco.internal.i18n;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

public class LocalizedString extends PluginDependent<EcoPlugin> {
    /**
     * The message ID.
     */
    @Getter
    private final String id;

    /**
     * Create a localized message.
     *
     * @param plugin The plugin.
     * @param id     The message id.
     */
    public LocalizedString(@NotNull final EcoPlugin plugin,
                           @NotNull final String id) {
        super(plugin);

        this.id = id;
    }

    @Override
    public String toString() {
        return this.getPlugin().getLangYml().getString(id);
    }
}
