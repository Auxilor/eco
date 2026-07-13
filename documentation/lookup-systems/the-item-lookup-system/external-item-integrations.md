---
title: External Item Integrations
sidebar_position: 2
---
The Item Lookup System allows you to use custom items from other plugins, inside of your eco plugins.

This means you can use items from plugins like Oraxen, ItemsAdder, and more.

## Using external items within Item Lookup
To use external items within The Item Lookup System, you need to use the relevant plugin's lookup-key.

| Plugin          | Lookup Key                                                                                                                 |
|-----------------|----------------------------------------------------------------------------------------------------------------------------|
| CraftEngine     | `craftengine:<namespace>__<key>` CraftEngine items are namespaced. **There is a double underscore! `__`**                  |
| CustomCrafting  | `customcrafting:<id>`                                                                                                      |
| Denizen         | `denizen:<id>`                                                                                                             |
| ExecutableItems | `executableitems:<id>`                                                                                                     |
| HeadDatabase    | `headdb:<id>`                                                                                                              |
| ItemBridge      | `itembridge:saved__<id>` for items you've saved.<br/>`itembridge:<prefix>__<id>` for plugin items supported in ItemBridge. |
| ItemsAdder      | `itemsadder:<namespace>__<key>` ItemsAdder items are namespaced, example below. **There is a double underscore! `__`**     |
| MythicMobs      | `mythicmobs:<id>`                                                                                                          |
| Nexo            | `nexo:<id>`                                                                                                                |
| Oraxen          | `oraxen:<id>`                                                                                                              |

:::tip Other Plugins
Using NBT data can be a great way to use items from any other plugin within eco, even where there is no direct integration. When configuring items in YAML files, you need to escape NBT strings to prevent syntax errors.

The simplest way to do this is to use the libreforge command to get the NBT data of an item in a slot.

Once the command is run, you can click-to-copy the chat output, and paste into your configs. The NBT string will be properly escaped for use in YAML files, so you don't have to worry about syntax errors.

Command: `/libreforge getitemdata <player> <slot>`
:::

#### ItemsAdder Usage

```yaml
# ** ItemsAdder configuration **
info:
  namespace: my_items
items:
  my_helmet:
    display_name: "&9Custom Helmet"
```

ItemsAdder items are namespaced, so for example, the above would be `itemsadder:crystal_pack__alumite_pickaxe`, where `crystal_pack` is the namespace, and `alumite_pickaxe` is the item ID.

## Using items from eco plugins externally
You can also use items from eco plugins in other plugins, such as MythicMobs, or ShopGUIPlus.

| Plugin      | Item Lookup Key                                                                                            |
|-------------|------------------------------------------------------------------------------------------------------------|
| MythicMobs  | To use a lookup item in MythicMobs, use `eco{type=<lookup_key>}`. e.g, `eco{type=ecoitems:grappling_hook}` |
| ShopGUIPlus | *Example below                                                                                             |

```yaml
** ShopGUIPlus configuration **
type: item
item:
eco: "ecoitems:holy_flesh"
quantity: 1
sellPrice: 7500
slot: 27
```

