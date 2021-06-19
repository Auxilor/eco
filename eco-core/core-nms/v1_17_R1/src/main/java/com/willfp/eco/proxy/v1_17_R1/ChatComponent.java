package com.willfp.eco.proxy.v1_17_R1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.willfp.eco.proxy.proxies.ChatComponentProxy;
import com.willfp.eco.core.display.Display;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.network.chat.ChatBaseComponent;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.IChatBaseComponent;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public final class ChatComponent implements ChatComponentProxy {
    @Override
    public Object modifyComponent(@NotNull final Object object) {
        if (!(object instanceof IChatBaseComponent chatComponent)) {
            return object;
        }

        for (IChatBaseComponent iChatBaseComponent : chatComponent) {
            if (iChatBaseComponent == null) {
                continue;
            }

            modifyBaseComponent(iChatBaseComponent);
        }

        return chatComponent;
    }

    private void modifyBaseComponent(@NotNull final IChatBaseComponent component) {
        for (IChatBaseComponent sibling : component.getSiblings()) {
            if (sibling == null) {
                continue;
            }

            modifyBaseComponent(sibling);
        }
        if (component instanceof ChatMessage) {
            Arrays.stream(((ChatMessage) component).getArgs())
                    .filter(o -> o instanceof IChatBaseComponent)
                    .map(o -> (IChatBaseComponent) o)
                    .forEach(this::modifyBaseComponent);
        }

        ChatHoverable hoverable = component.getChatModifier().getHoverEvent();

        if (hoverable == null) {
            return;
        }

        JsonObject jsonObject = hoverable.b();
        JsonElement json = hoverable.b().get("contents");
        if (json.getAsJsonObject().get("id") == null) {
            return;
        }
        if (json.getAsJsonObject().get("tag") == null) {
            return;
        }
        String id = json.getAsJsonObject().get("id").toString();
        String tag = json.getAsJsonObject().get("tag").toString();
        ItemStack itemStack = getFromTag(tag, id);

        Display.displayAndFinalize(itemStack);

        json.getAsJsonObject().remove("tag");
        String newTag = toJson(itemStack);
        json.getAsJsonObject().add("tag", new JsonPrimitive(newTag));

        jsonObject.remove("contents");
        jsonObject.add("contents", json);
        ChatHoverable newHoverable = ChatHoverable.a(jsonObject);
        ChatModifier modifier = component.getChatModifier();
        modifier = modifier.setChatHoverable(newHoverable);

        ((ChatBaseComponent) component).setChatModifier(modifier);
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
            nmsStack.setTag(MojangsonParser.parse(processedJsonTag));
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return CraftItemStack.asBukkitCopy(nmsStack);
    }

    private static String toJson(@NotNull final ItemStack itemStack) {
        return CraftItemStack.asNMSCopy(itemStack).getOrCreateTag().toString();
    }
}
