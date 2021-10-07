package com.willfp.eco.proxy.v1_16_R3

import com.willfp.eco.core.display.Display
import com.willfp.eco.proxy.ChatComponentProxy
import net.kyori.adventure.nbt.api.BinaryTagHolder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.server.v1_16_R3.IChatBaseComponent
import net.minecraft.server.v1_16_R3.ItemStack
import net.minecraft.server.v1_16_R3.MojangsonParser
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
class ChatComponent : ChatComponentProxy {
    private val gsonComponentSerializer = GsonComponentSerializer.gson()

    override fun modifyComponent(obj: Any, player: Player): Any {
        if (obj !is IChatBaseComponent) {
            return obj
        }

        val component = gsonComponentSerializer.deserialize(
            IChatBaseComponent.ChatSerializer.a(
                obj
            )
        ).asComponent()

        val newComponent = modifyBaseComponent(component, player)

        return IChatBaseComponent.ChatSerializer.a(
            gsonComponentSerializer.serialize(newComponent)
        ) ?: obj
    }

    private fun modifyBaseComponent(baseComponent: Component, player: Player): Component {
        val children = mutableListOf<Component>()

        for (child in baseComponent.children()) {
            children.add(modifyBaseComponent(child, player))
        }

        val component = baseComponent.children(children)

        val hoverEvent: HoverEvent<Any?> = component.style().hoverEvent() as HoverEvent<Any?>? ?: return component

        val showItem = hoverEvent.value()

        if (showItem !is HoverEvent.ShowItem) {
            return component
        }

        val tagHolder = showItem.nbt() ?: return component

        val newTag = BinaryTagHolder.of(
            CraftItemStack.asNMSCopy(
                Display.display(
                    CraftItemStack.asBukkitCopy(
                        ItemStack.a(
                            MojangsonParser.parse(
                                tagHolder.string()
                            )
                        )
                    ),
                    player
                )
            ).orCreateTag.toString()
        )

        val newShowItem = showItem.nbt(newTag);

        val newHover = hoverEvent.value(newShowItem)
        val style = component.style().hoverEvent(newHover)
        return component.style(style)
    }
}