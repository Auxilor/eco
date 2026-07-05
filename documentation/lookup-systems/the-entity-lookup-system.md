---
title: The Entity Lookup System
sidebar_position: 4
---

## What is the Entity Lookup System?
Much like how the item lookup system allows for specifying many options for ItemStacks in a simple, user-friendly string, the entity lookup system is designed to do the same for entities.

## Keys Explained
In each string is the key for an entity. A key looks one of two ways:
- A vanilla minecraft entity: (eg `husk`)
- An entity from another plugin: (eg `ecomobs:tarantula`)

### Using entities from other plugins

| Plugin     | Item Lookup Key  |
|------------|------------------|
| EcoMobs    | `ecomobs:<id>`   |
| MythicMobs | `mythicmobs:<id>` |

#### Extra syntax
- `?` between two entities means 'try to spawn the first entity, but if it doesn't exist, spawn the second entity'. You can chain these together.
- `||` groups two entities, allowing either one of them to be spawned, at random. You can chain these together to create further randomness on spawns.

### Entity Tags

Entity tags are groups of entities. A list of vanilla tags can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Tag.html), and you can use them with `#tag`, e.g. `#entity_types_aquatic`. These are especially useful in filters and conditions where you may use the same lists of entities in different places.

You can create your own tags in [tags.yml](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system#item-tags) too, use `#libreforge:<tag_id>` to reference these.

## Modifiers
Entities can have modifiers applied to them in the key. For example, let's say you're configuring a mob in EcoMobs. You want it to be a massive slime, a baby zombie, or a charged creeper, but you're not sure how to do that, because it looks like you have to just specify an entity type. Actually, in all of my plugins, wherever it asks for an entity, it's actually doing a lookup. You can specify any of the following modifiers to it:
- `adult` Force the entity to be an adult
- `attack-damage` Set the entity's attack damage
- `attack-speed` Set the entity's attack speed
- `baby` Force the entity to be a baby
- `charged` Set a creeper to be charged
- `explosion-radius` Set a creepers explosion radius
- `fly-speed` Set the entity's fly speed
- `follow-range` Set the entity's follow range
- `health` Set the entity's health
- `jump-strength` Set a horses jump strength
- `knockback` Set the entity's attack knockback
- `knockback-resistance` Set the entity's knockback resistance
- `name` Set the entity's display name, use quotes (") for multi-word names
- `no-ai` Set the entity to have no AI
- `size` Set the size of a slime or phantom
- `spawn-reinforcements` Set a zombie to spawn reinforcements
- `speed` Set the movement speed of the entity
- `silent` Set the entity to make no sound
- `head` Set the helmet item
- `chest` Set the chestplate
- `legs` Set the leggings
- `feet` Set the boots
- `hand` Set the held item
- `off_hand` Set the held item in the offhand
- `model-engine` Set the Model Engine ID and animation (for ID do `model-engine:id`, for ID and animation do `model-engine:id,animation`)

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

You simply chain the modifiers together, for example:
`phantom size:5 follow-range:25 speed:2 health:200 name:"&4Deadly Phantom" model-engine:dragon,flap`