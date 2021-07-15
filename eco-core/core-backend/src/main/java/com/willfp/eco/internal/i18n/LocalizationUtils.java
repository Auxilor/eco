package com.willfp.eco.internal.i18n;

import com.willfp.eco.core.EcoPlugin;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class LocalizationUtils {
    public LocalizedString getStringFromKey(@NotNull final NamespacedKey key) {
        Validate.isTrue(EcoPlugin.getPluginNames().contains(key.getNamespace()));

        EcoPlugin source = EcoPlugin.getPlugin(key.getNamespace());

        assert source != null;

        String message = source.getLangYml().getStringOrNull("messages." + key.getKey());

        if (message == null) {
            return new LocalizedString(source, key.getKey());
        } else {
            return new LocalizedMessage(source, key.getKey());
        }
    }
}
