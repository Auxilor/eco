package com.willfp.eco.internal.i18n;

import com.willfp.eco.core.EcoPlugin;
import org.jetbrains.annotations.NotNull;

public class EcoLocalizedMessage extends EcoLocalizedString {
    /**
     * Create a localized message.
     *
     * @param plugin The plugin.
     * @param id     The message id.
     */
    public EcoLocalizedMessage(@NotNull final EcoPlugin plugin,
                               @NotNull final String id) {
        super(plugin, id);
    }

    @Override
    public String toString() {
        return this.getPlugin().getLangYml().getMessage(this.getId());
    }
}
