---
title: Crafting Recipes
slug: /the-item-lookup-system/recipes
sidebar_position: 1
---
Anywhere that you configure items for players in eco plugins, you can use the Item Lookup System to add crafting recipes for them. 

Because of this, you can make use of all the different modifiers too, such as stack size, enchantments, name, tags, and more, to make your recipes more unique and interesting and fully customisable. 

EcoItems also allows you to add custom crafting recipes for any lookup item, not limited to EcoItems items. This means you can add more recipes for items from other plugins, such as EcoPets or EcoScrolls, or even vanilla items, like Enchanted Golden Apples.

Crafting recipes in eco plugins are super easy to configure, and often look something like this:

# Shaped Recipe Example

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

While it may look meaningless, this system is straightforward once you understand how it works. A crafting recipe is written as a list of 9 strings, the first three being the top row (left to right),
the second three being the middle row (left to right), and the last three being the last row (left to right).

# Shapeless Recipe Example

```yaml
recipe:
  - "diamond"
  - "emerald"
  - "diamond"
```

Similar to above, you just create a list of items that you want to be in the crafting grid. The order does not matter for shapeless recipes.

# Additional Recipe Options

When creating recipes, there are a few additional options you can use to customize the recipe further. 

## `shapeless: true/false` (Optional - default: false)
If your shapeless recipe contains less than 9 items (not including air) then you do not need this option, as the plugin will automatically detect it as a shapeless recipe. 

However, if you want to have a shapeless recipe with 9 items, you must set `shapeless: true` to tell the plugin that this is a shapeless recipe and not a shaped one.

## `enabled: true/false`
In all recipe locations, you can disable crafting. Just set `enabled: false` and the recipe will not be registered.

## `recipe-permission: permission.node` (Optional - default: none)
If you want to require a permission to craft the item, you can set this option to the permission you want to require. Players without this permission will not be able to craft the item.
Please note: In some plugins, this may be called `crafting-permission` or something similar, but the functionality is the same. This is because plugins were created at different times and to remain backward compatible. Check the _example.yml for the correct key.

## `recipe-give-amount: 2` (EcoItems only)
This option allows you to set how many of the item the recipe gives when crafted.

## A Complete Recipe Example

Below is a complete, 9 item, shapeless recipe example with all the options included:
```yaml
enabled: true
shapeless: true
recipe-permission: myplugin.craft.example_item
recipe-give-amount: 2
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

:::tipInvisible Recipe Results
When using non-vanilla custom items in crafting recipes, the result may sometimes appear invisible. <br/>
Although it looks like the recipe is broken and produces no result, clicking the result slot will still give you the correct item.

To fix this, open `/plugins/eco/config.yml` and find the `enforce-preparing-recipes` option. Set it to "true", then restart your server.
:::


:::noteCreating Multiple Recipes
Creating multiple recipes for one item is not supported in the current config structure.

However, if you own EcoItems, you can easily create more custom recipes, you can read about that [here](https://plugins.auxilor.io/ecoitems/additional-configuration-options#how-to-add-additional-recipes).
:::

:::warningRecipe Book
There is a display issue where only vanilla items are shown in the recipe book, and heads use the default texture. However, the recipes still work correctly.
This is a Minecraft/Server limitation due to how custom items are handled. There were events of servers crashing and corrupting chunks when displaying custom items in recipes.

This may be fixed in a future version if possible.
:::

