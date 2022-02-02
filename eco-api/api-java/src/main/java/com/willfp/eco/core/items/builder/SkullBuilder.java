package com.willfp.eco.core.items.builder;

import com.willfp.eco.util.SkullUtils;
import org.bukkit.Material;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Builder for player heads.
 */
public class SkullBuilder extends AbstractItemStackBuilder<SkullMeta, SkullBuilder> {
    /**
     * Create a new EnchantedBookBuilder.
     */
    public SkullBuilder() {
        super(Material.PLAYER_HEAD);
    }

    /**
     * Set skull texture.
     *
     * @param texture The texture.
     * @return The builder.
     */
    public SkullBuilder setSkullTexture(@NotNull final String texture) {
        SkullUtils.setSkullTexture(this.getMeta(), texture);

        return this;
    }

    /**
     * Set skull texture.
     *
     * @param texture The texture.
     * @return The builder.
     */
    public SkullBuilder setSkullTexture(@NotNull final Supplier<String> texture) {
        return this.setSkullTexture(texture.get());
    }
}
