package com.willfp.eco.proxy.v1_17_R1

import com.willfp.eco.core.display.Display
import com.willfp.eco.proxy.ChatComponentProxy
import net.kyori.adventure.nbt.api.BinaryTagHolder
import net.kyori.adventure.text.BuildableComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minecraft.nbt.TagParser
import net.minecraft.world.item.ItemStack
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Player

@Suppress("UNCHECKED_CAST")
class ChatComponent : ChatComponentProxy {
    private val gsonComponentSerializer = GsonComponentSerializer.gson()

    override fun modifyComponent(obj: Any, player: Player): Any {
        if (obj !is net.minecraft.network.chat.Component) {
            return obj
        }

        val component = gsonComponentSerializer.deserialize(
            net.minecraft.network.chat.Component.Serializer.toJson(
                obj
            )
        ).asComponent() as BuildableComponent<*, *>

        val newComponent = modifyBaseComponent(component, player)

        return net.minecraft.network.chat.Component.Serializer.fromJson(
            gsonComponentSerializer.serialize(newComponent)
        ) ?: obj
    }
    private fun <C: BuildableComponent<C, B>, B: ComponentBuilder<C, B>> modifyBaseComponent(baseComponent: BuildableComponent<C, B>, player: Player): Component {
        val component = baseComponent.toBuilder().mapChildren {
            modifyBaseComponent(it, player) as BuildableComponent<C, B>
        }.asComponent()

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
                        ItemStack.of(
                            TagParser.parseTag(
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