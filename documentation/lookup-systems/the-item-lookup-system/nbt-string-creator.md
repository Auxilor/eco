---
title: NBT String Creator
slug: /the-item-lookup-system/nbt-string-creator
sidebar_position: 2
---
Using NBT data can be a great way to use items from any other plugin within eco, even where there is no direct integration. When configuring items in YAML files, you need to escape NBT strings to prevent syntax errors.

The simplest way to do this is to use the libreforge command to get the NBT data of an item in a slot.

Once the command is run, you can click-to-copy the chat output, and paste into your configs. The NBT string will be properly escaped for use in YAML files, so you don't have to worry about syntax errors.

Command: `/libreforge getitemdata <player> <slot>`

Alternatively, you can also use the below converter tool to escape your NBT strings.

This tool converts raw Minecraft NBT strings into properly escaped format for eco-compatible config files.

There are a few steps you need to take to make this work efficiently.
1. Give yourself the item you want to use.
   - This can be from another plugin, the creative menu, or via a custom item creation tool (eg: [Give Command Generator](https://www.gamergeeks.net/apps/minecraft/give-command-generator))
2. Hold this item in your main hand. Avoid holding anything in your off-hand.
3. **From console** run this command: `data get entity <YOUR_NAME> SelectedItem`, and copy the result.
   - It should look like this: `{id: "minecraft:potion", count: 1, components: {"minecraft:potion_contents": {potion: "minecraft:leaping"}}}`
4. Paste that NBT string into the converter below.
5. Use your newly escaped NBT string in your eco configs.

import NbtEscaper from '@site/src/components/NbtEscaper';

<NbtEscaper />
