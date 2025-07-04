package com.willfp.eco.internal.spigot.proxy.v1_21_7

import com.willfp.eco.core.display.Display
import com.willfp.eco.internal.spigot.proxy.MiniMessageTranslatorProxy
import com.willfp.eco.util.toLegacy
import net.kyori.adventure.text.minimessage.MiniMessage

class MiniMessageTranslator : MiniMessageTranslatorProxy {
    override fun format(message: String): String {
        var mut = message

        val startsWithPrefix = mut.startsWith(Display.PREFIX)
        if (startsWithPrefix) {
            mut = mut.substring(2)
        }

        mut = mut.replace('§', '&')

        val miniMessage = runCatching {
            MiniMessage.miniMessage().deserialize(
                mut
            ).toLegacy()
        }.getOrNull() ?: mut

        mut = if (startsWithPrefix) {
            Display.PREFIX + miniMessage
        } else {
            miniMessage
        }

        return mut
    }
}
