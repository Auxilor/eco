package com.willfp.eco.internal.spigot.proxy.v1_21_5.item

import com.willfp.eco.internal.spigot.proxy.common.modern.NewEcoFastItemStack
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.component.TooltipDisplay
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack


open class NewerEcoFastItemStack(
    bukkit: ItemStack,
) : NewEcoFastItemStack(bukkit) {
    override fun addItemFlags(vararg hideFlags: ItemFlag) {
        for (flag in hideFlags) {
            when (flag) {
                ItemFlag.HIDE_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.ENCHANTMENTS, true)
                    }
                }

                ItemFlag.HIDE_ATTRIBUTES -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true)
                    }
                }

                ItemFlag.HIDE_UNBREAKABLE -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.UNBREAKABLE, true)
                    }
                }

                ItemFlag.HIDE_DESTROYS -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.CAN_BREAK, true)
                    }
                }

                ItemFlag.HIDE_PLACED_ON -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.CAN_PLACE_ON, true)
                    }
                }

                ItemFlag.HIDE_ADDITIONAL_TOOLTIP -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        TooltipDisplay(true, tooltip.hiddenComponents)
                    }
                }

                ItemFlag.HIDE_DYE -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.DYED_COLOR, true)
                    }
                }

                ItemFlag.HIDE_ARMOR_TRIM -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.TRIM, true)
                    }
                }

                ItemFlag.HIDE_STORED_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.STORED_ENCHANTMENTS, true)
                    }
                }
            }
        }

        apply()
    }

    override fun removeItemFlags(vararg hideFlags: ItemFlag) {
        for (flag in hideFlags) {
            when (flag) {
                ItemFlag.HIDE_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.ENCHANTMENTS, false)
                    }
                }

                ItemFlag.HIDE_ATTRIBUTES -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, false)
                    }
                }

                ItemFlag.HIDE_UNBREAKABLE -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.UNBREAKABLE, false)
                    }
                }

                ItemFlag.HIDE_DESTROYS -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.CAN_BREAK, false)
                    }
                }

                ItemFlag.HIDE_PLACED_ON -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.CAN_PLACE_ON, false)
                    }
                }

                ItemFlag.HIDE_ADDITIONAL_TOOLTIP -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        TooltipDisplay(false, tooltip.hiddenComponents)
                    }
                }

                ItemFlag.HIDE_DYE -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.DYED_COLOR, false)
                    }
                }

                ItemFlag.HIDE_ARMOR_TRIM -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.TRIM, false)
                    }
                }

                ItemFlag.HIDE_STORED_ENCHANTS -> {
                    handle.modifyComponent(DataComponents.TOOLTIP_DISPLAY) { tooltip ->
                        tooltip.withHidden(DataComponents.STORED_ENCHANTMENTS, false)
                    }
                }
            }
        }

        apply()
    }

    override fun hasItemFlag(flag: ItemFlag): Boolean {
        return when (flag) {
            ItemFlag.HIDE_ENCHANTS -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.ENCHANTMENTS)
            }

            ItemFlag.HIDE_ATTRIBUTES -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.ATTRIBUTE_MODIFIERS)
            }

            ItemFlag.HIDE_UNBREAKABLE -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.UNBREAKABLE)
            }

            ItemFlag.HIDE_DESTROYS -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.CAN_BREAK)
            }

            ItemFlag.HIDE_PLACED_ON -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.CAN_PLACE_ON)
            }

            ItemFlag.HIDE_ADDITIONAL_TOOLTIP -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                tooltip.hideTooltip
            }

            ItemFlag.HIDE_DYE -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.DYED_COLOR)
            }

            ItemFlag.HIDE_ARMOR_TRIM -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.TRIM)
            }

            ItemFlag.HIDE_STORED_ENCHANTS -> {
                val tooltip = handle.get(DataComponents.TOOLTIP_DISPLAY) ?: return false
                !tooltip.shows(DataComponents.STORED_ENCHANTMENTS)
            }
        }
    }
}
