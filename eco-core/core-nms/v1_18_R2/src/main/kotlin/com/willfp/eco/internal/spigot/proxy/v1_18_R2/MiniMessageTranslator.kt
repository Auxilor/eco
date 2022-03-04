package com.willfp.eco.internal.spigot.proxy.v1_18_R2

import com.willfp.eco.internal.spigot.proxy.MiniMessageTranslatorProxy
import com.willfp.eco.util.toLegacy
import net.kyori.adventure.text.minimessage.MiniMessage
import net.md_5.bungee.api.ChatColor

class MiniMessageTranslator : MiniMessageTranslatorProxy {
    override fun format(message: String): String {
        return MiniMessage.miniMessage().deserialize(
            ChatColor.stripColor(message) // 4.10.0 is annoying
        ).toLegacy()
    }
}
