---
title: Recipes
sidebar_position: 3
---

Anywhere you configure items for players in eco plugins, you can use the Item Lookup System to give them a recipe. Because recipes are built on lookups, every modifier works inside them too - stack size, enchantments, names, tags, and more - so a recipe can require a very specific item, not just a material.

This page is the reference for every recipe format across eco plugins. Which of them you can actually use depends on the plugin.

## What each plugin supports

| Recipe | Supported in |
| --- | --- |
| **Crafting table** (shaped and shapeless, with permissions) | **Every** eco plugin |
| Furnace, blast furnace, smoker, campfire | EcoItems and EcoCrafting only |
| Stonecutter, smithing table, anvil, brewing stand | EcoItems and EcoCrafting only |
| Villager trading, grindstone | **EcoCrafting only** |

:::info Most plugins are crafting table only
If you're configuring a recipe in EcoArmor, EcoPets, Talismans, Reforges, EcoScrolls, or any other eco plugin, the crafting table is the only workstation available to you - with `shapeless` and a permission. Everything in [Other workstations](#other-workstations) needs EcoItems or EcoCrafting.
:::

## Crafting table recipes

A recipe is written as a list of 9 strings: the first three are the top row (left to right), the next three the middle row, and the last three the bottom row. `""` is an empty slot.

```yaml
recipe:
  - "diamond"
  - "emerald"
  - "diamond"
  - ""
  - "stick"
  - ""
  - ""
  - "stick"
  - ""
```

### Shapeless recipes

Set `shapeless: true` and the slot positions stop mattering - only the set of ingredients counts. A shapeless recipe is a flat list and can be shorter than 9:

```yaml
shapeless: true
recipe:
  - "diamond"
  - "emerald"
  - "diamond"
```

:::warning `shapeless` is not auto-detected
A shaped recipe must have **exactly 9** entries. A shorter list without `shapeless: true` is rejected, and a warning is logged on reload telling you how many ingredients it found. If your recipe uses fewer than 9 items, either pad it out to 9 with `""` or set `shapeless: true`.
:::

### Permissions

Set a permission node to require it before a player can craft the item:

```yaml
recipe-permission: myplugin.craft.example_item
```

:::note The key name varies by plugin
Plugins were written at different times, so this is `permission` in some and `recipe-permission` or `crafting-permission` in others. The behaviour is identical - check the plugin's `_example.yml` for the key it expects.
:::

### Other options

| Option | What it does |
| --- | --- |
| `craftable: true` | Turns the recipe on - see below. |
| `give-amount: 2` | How many items one craft gives. EcoItems only. |
| `enabled: false` | Stops the recipe registering. EcoCrafting only. |

### `craftable`

Most eco plugins gate the recipe behind `craftable`, and **won't register it unless you set `craftable: true`**. It sits next to the recipe, so the exact path varies - `craftable` in Talismans, `item.craftable` in EcoScrolls, `stone.craftable` in Reforges, `spawn-egg.craftable` in EcoPets, `shard.craftable` in EcoArmor, and so on. Check the plugin's `_example.yml`.

Setting `craftable: false` always switches a recipe off.

### A complete example

```yaml
shapeless: true
recipe-permission: myplugin.craft.example_item
recipe:
  - "diamond"
  - "emerald"
  - "diamond"
  - "diamond"
  - "nether_star"
  - "diamond"
  - "netherite_ingot"
  - "gold_ingot"
  - "netherite_ingot"
```

## Other workstations

**EcoItems and EcoCrafting only.** Pick the workstation with `type:`, then add that workstation's ingredient keys. Both plugins use the same keys, so a recipe reads the same in either - they differ only in where the config lives and where the result comes from:

| | EcoItems | EcoCrafting |
| --- | --- | --- |
| Where it goes | The item's `item.recipes` section | Its own file in `recipes/<workstation>/` |
| The result | The item itself | The `output:` key |
| Permission | `permission:` | `permission:` |

| `type` | Ingredient keys | Extra keys |
| --- | --- | --- |
| `crafting_table` *(default)* | `recipe` (9 slots) | `shapeless` |
| `furnace` | `input` | `experience`, `cook-time` |
| `blast_furnace` | `input` | `experience`, `cook-time` |
| `smoker` | `input` | `experience`, `cook-time` |
| `campfire` | `input` | `experience`, `cook-time` |
| `stonecutter` | `input` | |
| `smithing_table` | `template`, `base`, `addition` | |
| `anvil` | `base`, `material` *(optional)* | `result-name`, `repair-cost` |
| `brewing_stand` | `base`, `ingredient` | `brew-time` |

### Furnace, blast furnace, smoker, and campfire

One `input` that cooks into the result. `experience` is the XP dropped when it finishes, and `cook-time` is how long it takes in ticks. Leave `cook-time` out to use the vanilla speed for that block: 200 for a furnace, 100 for a blast furnace or smoker, 600 for a campfire.

```yaml
input: raw_iron
experience: 0.7
cook-time: 200
```

### Stonecutter

One `input`, cut into the result.

```yaml
input: stone
```

:::info Multiple outputs
EcoCrafting can give a stonecutter recipe several outputs, each appearing as its own option in the UI, via an `outputs:` list. EcoItems produces the one item.
:::

### Smithing table

All three slots are required: `template` (left), `base` (middle), and `addition` (right).

```yaml
template: netherite_upgrade_smithing_template
base: diamond_sword
addition: netherite_ingot
```

### Anvil

`base` goes in the left slot. `material` is optional - leave it out and the base alone is enough. `repair-cost` is the level cost in levels.

```yaml
base: iron_sword
material: diamond
repair-cost: 3
```

:::note `result-name` is EcoCrafting only
EcoCrafting can rename an anvil result with `result-name`. EcoItems has no use for it - the result is the custom item, which already carries its own name.
:::

### Brewing stand

`base` is the item in the bottle slots (often an existing potion), and `ingredient` is what goes in the top slot. `brew-time` is in ticks.

```yaml
base: glass_bottle
ingredient: nether_wart
brew-time: 400
```

:::warning Shift-click into a brewing stand
Shift-clicking only sends an item into a brewing stand's slots if vanilla already considers it valid there - a potion or bottle, or a vanilla brewing ingredient. A custom `base`/`ingredient` that vanilla doesn't recognise has to be clicked into the slot manually.
:::

## EcoCrafting only

[EcoCrafting](https://plugins.auxilor.io/ecocrafting/how-to-make-a-recipe) adds two workstations that no other plugin has:

- **Villager trading** - inject custom trades into villagers or wandering traders, with a profession, minimum level, appearance chance, and villager XP.
- **Grindstone** - a one or two slot grindstone recipe.

It also layers extras onto every recipe type, none of which exist elsewhere: a browsable recipe book GUI with categories, quick-crafting, libreforge `effects` and `conditions`, `price` support, per-player recipe locking and unlocking, `give-result-item: false` to fire effects instead of giving an item, plus `symmetry` and vanilla Crafter support on crafting table recipes.

## Notes and limitations

:::note One recipe per item
The config structure gives an item a single recipe. If you need several ways to make the same thing, [EcoCrafting](https://plugins.auxilor.io/ecocrafting/how-to-make-a-recipe) is built for it - its recipes are standalone files, so you can point as many as you like at the same output.
:::

:::tip Invisible recipe results
When a recipe uses non-vanilla custom items, the result can appear invisible. The recipe isn't broken - clicking the result slot still gives you the correct item.

To fix it, open `/plugins/eco/config.yml`, set `enforce-preparing-recipes` to `true`, and restart your server.
:::

:::warning Recipe book display
Only vanilla items show properly in the recipe book, and heads use the default texture. The recipes themselves still work.

This is a Minecraft/server limitation in how custom items are handled - there were cases of servers crashing and corrupting chunks when displaying custom items in recipes. It may be fixed in future if possible.
:::

<hr/>

## Where to go next

- **EcoItems:** [Workstation Recipes](https://plugins.auxilor.io/ecoitems/additional-configuration-options/workstation-recipes) to craft a custom item at any of the workstations above.
- **EcoCrafting:** [How to make a Recipe](https://plugins.auxilor.io/ecocrafting/how-to-make-a-recipe) for standalone recipes, villager trades, grindstones, and the recipe book.
- **Item lookups:** [The Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system) for the ingredient and result syntax.
