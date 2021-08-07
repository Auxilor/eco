package com.willfp.eco.proxy.v1_17_R1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.willfp.eco.core.display.Display;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.willfp.eco.proxy.ChatComponentProxy;

public final class ChatComponent implements ChatComponentProxy {
    @Override
    public Object modifyComponent(@NotNull final Object obj,
                                  @NotNull final Player player) {
        if (!(obj instanceof Component chatComponent)) {
            return obj;
        }

        for (Component iChatBaseComponent : chatComponent) {
            if (iChatBaseComponent == null) {
                continue;
            }

            modifyBaseComponent(iChatBaseComponent, player);
        }

        return chatComponent;
    }

    private void modifyBaseComponent(@NotNull final Component component,
                                     @NotNull final Player player) {
        for (Component sibling : component.getSiblings()) {
            if (sibling == null) {
                continue;
            }

            modifyBaseComponent(sibling, player);
        }

        if (component instanceof TranslatableComponent baseComponent) {
            for (Object arg : baseComponent.getArgs()) {
                if (arg instanceof Component) {
                    modifyBaseComponent((Component) arg, player);
                }
            }
        }

        HoverEvent hoverable = component.getStyle().getHoverEvent();

        if (hoverable == null) {
            return;
        }

        JsonObject jsonObject = hoverable.serialize();
        JsonElement json = jsonObject.get("contents");
        if (json.getAsJsonObject().get("id") == null) {
            return;
        }
        if (json.getAsJsonObject().get("tag") == null) {
            return;
        }
        String id = json.getAsJsonObject().get("id").toString();
        String tag = json.getAsJsonObject().get("tag").toString();
        ItemStack itemStack = getFromTag(tag, id);

        Display.displayAndFinalize(itemStack, player);

        json.getAsJsonObject().remove("tag");
        String newTag = toJson(itemStack);
        json.getAsJsonObject().add("tag", new JsonPrimitive(newTag));

        jsonObject.remove("contents");
        jsonObject.add("contents", json);
        HoverEvent newHoverable = HoverEvent.deserialize(jsonObject);
        Style modifier = component.getStyle();
        modifier = modifier.withHoverEvent(newHoverable);

        ((BaseComponent) component).setStyle(modifier);
    }

    private static ItemStack getFromTag(@NotNull final String jsonTag,
                                        @NotNull final String id) {
        String processedId = id;
        String processedJsonTag = jsonTag;
        processedId = processedId.replace("minecraft:", "");
        processedId = processedId.toUpperCase();
        processedId = processedId.replace("\"", "");
        processedJsonTag = processedJsonTag.substring(1, processedJsonTag.length() - 1);
        processedJsonTag = processedJsonTag.replace("id:", "\"id\":");
        processedJsonTag = processedJsonTag.replace("\\", "");
        Material material = Material.getMaterial(processedId);

        assert material != null;
        ItemStack itemStack = new ItemStack(material);
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);

        try {
            nmsStack.setTag(TagParser.parseTag(processedJsonTag));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private static String toJson(@NotNull final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().toString();
    }
}
