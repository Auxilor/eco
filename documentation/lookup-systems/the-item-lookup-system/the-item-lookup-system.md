---
title: The Item Lookup System
sidebar_position: 1
---

## What is the Item Lookup System?

The item lookup system is how items are loaded from configs. It's designed to be extremely flexible and intuitive, allowing you to use custom items, stacks, enchantments, etc. wherever you want, without having to worry about what plugin they're from.

Anywhere you need to use items, you can use this system. To look up an item, you need a key, and optional modifiers.

## Keys Explained

In each string is the key for an item. A key looks one of a few ways

- A vanilla minecraft material ID: (e.g. `golden_apple`)
- An item from another eco plugin: (e.g. `ecoitems:packmaster_tear`) 
- An item from an external plugin: (e.g. `oraxen:alumite_pickaxe`), See [External Integrations](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-external-item-integrations) for more info
- An item tag: (e.g. `#talismans:talisman` or `#items_axes`)
- An exact item NBT tag: (e.g. `{id:"stone",Count:3,tag:{Name:"your name"}}`), see [here](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-external-item-integrations#using-external-items-within-item-lookup) for more info on NBT.

#### Extra syntax

- `?` between two items means 'try to use the first item, but if it doesn't exist, use the second item'. You can chain these together.
- `||` groups two items, allowing either one of them to be used. You can chain these together.
- You can specify stack size, e.g. `string 64` would mean a full stack of string.

When using exact item NBT, you can't use `?`. `||`, or other modifiers.

### Vanilla Materials

By default, a vanilla material (e.g. `diamond_pickaxe`) will not accept custom items with the same material. For example, if you have an EcoItems item with `diamond_pickaxe` as its base material,
then that item will not be recognised as a `diamond_pickaxe`.

If you want custom items to be accepted, place a `*` at the start, so `"diamond_pickaxe"` becomes `"*diamond_pickaxe"`. Outside of crafting recipes, most plugins use `*` items in their
default configs for performance reasons.

### Item Tags

Item tags are groups of items. A list of vanilla tags can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Tag.html), and you can use them with `#tag`, e.g. `#items_pickaxes`. These are especially useful in filters and recipes where you may use the same lists of items in different places.

You can create your own tags in [tags.yml](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system#item-tags) too, use `#libreforge:<tag_id>` to reference these.
```yaml
tags:
  - id: example_tag # The ID, used in the item-lookup-system.
    items: # The item's contained in the tag.
      - "netherite_sword"
      - "diamond_sword"
```

### Using items from eco plugins

| Plugin       | Item Lookup Key                                                                                             | Item Tag                                   |
|--------------|-------------------------------------------------------------------------------------------------------------|--------------------------------------------|
| EcoArmor     | `ecoarmor:set_<set>_<slot>` (Optional: `_advanced`) <br/>`ecoarmor:shard_<set>`<br/>`ecoarmor:crystal_<id>` |                                            |
| EcoCrates    | `ecocrates:<crate>_key`                                                                                     |                                            |
| EcoItems     | `ecoitems:<id>`                                                                                             | `#ecoitems:item`                           |
| EcoMobs      | `ecomobs:<id>_spawn_egg`                                                                                    |                                            |
| EcoPets      | `ecopets:<id>_spawn_egg`                                                                                    |                                            |
| EcoScrolls   | `ecoscrolls:scroll_<id>`                                                                                    | `#ecoscrolls:scroll`                       |
| Reforges     | `reforges:stone_<id>`                                                                                       | `#reforges:stone`<br/>`#reforges:reforged` |
| StatTrackers | `stattrackers:<id>`                                                                                         |                                            |
| Talismans    | `talismans:<id>`                                                                                            | `#talismans:talisman`                      |

### Using items from third-party plugins

Visit the [External Item Integrations](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-external-item-integrations) page for more information on how to use items from third-party plugins.

## Potions & Fireworks
- **Potion Builder:** You can create potions to use in your item section with `potion_effect:<potion_type>:<level>:<duration>`. <br/>
  e.g. `splash_potion potion_effect:swiftness:5:3600`. This would be a splash potion with Swiftness 5 for 3 minutes (3600 ticks)

  This works for `potion`, `splash_potion`, `lingering_potion` and `tipped_arrow`. <br/>
  Optionally, you can add the `color` modifier, e.g. `color:#ff0000`.

- **Firework Builder:** You can also create custom fireworks using `firework_effect:<index>:<type>:<colors>:<fadeColors>:<trail>:<flicker>`. <br/>
  It may look complicated at first, but it's easy once you know the parts:

  | Part           | Description                                                                    |
  |----------------|--------------------------------------------------------------------------------|
  | `<index>`      | Explosion number (0-2) – controls the order of explosions.                     |
  | `<type>`       | Shape of the explosion (`BALL`, `BALL_LARGE`, `STAR`, `CREEPER`, `BURST`)      |
  | `<colors>`     | Main colors in **hex** format (`#RRGGBB`). Multiple colors separated by commas |
  | `<fadeColors>` | Colors it fades to after the explosion. Use `false` if no fade                 |
  | `<trail>`      | `true` or `false` – whether it leaves a trail                                  |
  | `<flicker>`    | `true` or `false` – whether it flickers                                        |

  e.g. `firework_rocket firework_effect:0:CREEPER:#FF0000:#0000FF:true:true` would produce a firework with a red creeper that fades into blue, with trails and flicker.

  Fireworks can support up to 3 explosions, you can string these along in one lookup string, e.g. `firework_rocket firework_effect:0:... firework_effect:1:... firework_effect:2:...`

  Optionally, you can add the `firework_power` modifier, e.g. `firework_power:1`. Firework Power: 1 = short, 2 = medium, 3 = long.

## Modifiers

Items can  have modifiers applied to them. For example, lets say you're configuring the GUI for EcoSkills. You want it to be a player head with a texture, but you're not sure how to do that, because it looks like you have to just specify a material. Actually, in all eco plugins, wherever it asks for a material, it's actually doing a lookup. You can specify any of the following modifiers to it:

- **Name:** You can specify the display name of an item with `name:<name>`. You can have multiple words by surrounding the name with quotes: `name:"Long Name"`
- **Enchantments:** You can specify an enchantment with `<enchantment>:<level>`
- **Enchantment Glint:** You can give an enchantment glint with `glint`
- **Item Flags:** You can specify flags for the item to have, by dropping in any of [these values](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/inventory/ItemFlag.html) (not case sensitive)
- **Unbreakable:** You can make an item unbreakable by having the word `unbreakable` in the flags
- **Unenchantable:** You can prevent an item from being enchanted (via enchanting table or anvil) by adding `unenchantable`
- **Custom Model Data:** You can specify custom model data with `custom_model_data:<id>`
- **Stack Quantity:** You can specify a stack quantity by using the amount, e.g. `iron_ingot 32`
- **Max Stack Size:** You can set the max stack size with `max_stack_size:<size>`
- **Durability:** You can set the item durability with `max_damage:<durability>`
- **Leather Armor & Potion Color:** You can specify the leather armor or potion color with `color:#hex`, e.g. `color:#303030`
- **Armor Trims:** You can specify armor trims with `trim:<material>:<pattern>`, e.g. `trim:emerald:snout`
- **Fire Resistance:** You can make an item fire-resistant with `fire_resistant`
- **Player Head:** If the material is a player head, you can specify a player using `head:<name>`. You can also use placeholders, e.g. `head:%player%`
- **Skull Texture:** If the material is a player head, you can specify the texture with `texture:"<base64>"`. A list of skulls and textures can be found [here](https://minecraft-heads.com/).
- **Reforge:** You can specify the reforge by adding `reforge:<id>` to the key.
- **Spawner Entity:** You can specify the spawner entity with `entity:<id>`
- **Glider:** You can make any chestplate slot item a glider (work like an elytra) with `glider`
- **Item Model:** You can set the item model (different to custom-model-data) with `item_model:<namespace>:<id>`, e.g. `item_model:nexo:dragon_helmet`. For minecraft items, you can skip the namespace
- **Tooltip Style:** You can set the tooltip style with `tooltip_style:<namespace>:<id>`, e.g. `tooltip_style:nexo:epic_tooltip`.
- **Item Name:** You can set the item name (different to display name) with `item_name:<name>`
- **Attribute:** You can add an attribute modifier with `attribute:<attribute>:<operation>:<amount>:[slot]`. The `<slotGroup>` is optional and defaults to `any`.

  | Part          | Description                                                                                                                                                  |
  |---------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------|
  | `<attribute>` | The attribute to modify (e.g. `generic_attack_damage`, `generic_max_health`, `generic_movement_speed`)                                                       |
  | `<operation>` | The operation type: `add_number`, `add_scalar`, or `multiply_scalar`                                                                                         |
  | `<amount>`    | The numeric value for the modifier                                                                                                                           |
  | `[slot]`      | *(Optional)* The slot group the modifier applies to <br/> (e.g. `mainhand`, `offhand`, `hands`, `helmet`, `chestplate`, `leggings`, `boots`, `armor`, `any`) |

  [Visit the Minecraft Wiki to learn about Attributes](https://minecraft.wiki/w/Attribute)

  Examples:
  - `diamond_sword attribute:generic_attack_damage:add_number:5:mainhand` — adds +5 attack damage when held in the main hand
  - `diamond_chestplate attribute:generic_max_health:add_number:10` — adds +10 max health (slot defaults to `any`)
  - `leather_boots attribute:generic_movement_speed:add_scalar:0.1:feet` — adds +10% movement speed when worn on feet

So, let's say you have an EcoMobs mob, and you want it to drop a rare custom weapon with extra modifiers already applied. Without the Item Lookup system, this wouldn't be possible, but thanks to it, you can just do this: `ecoitems:enlightened_blade razor:4 unbreaking:3 criticals:2 fire_aspect:2 reforge:mighty unbreakable hide_attributes custom_model_data:2`

## Using eco plugin items in other plugins

Visit the [External Item Integrations](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-external-item-integrations) page for more information on how to use items within third-party plugins.

