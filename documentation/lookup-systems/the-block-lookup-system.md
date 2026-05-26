---
title: The Block Lookup System
slug: /the-block-lookup-system
sidebar_position: 2
---

## What is the Block Lookup System?

The block lookup system is how blocks are loaded from configs. It's designed to be extremely flexible and intuitive, allowing you to use custom blocks, block states, modifiers, etc. wherever you want, without having to worry about what plugin they're from.

Anywhere you need to use blocks, you can use this system. To look up a block, you need a key, and optional modifiers.

## Keys Explained

In each string is the key for a block. A key looks one of a few ways

- A vanilla minecraft material ID: (e.g. `stone`)
- A block from an external plugin: (e.g. `nexo:ruby_ore`), See [External Integrations](https://plugins.auxilor.io/the-item-lookup-system/external-item-integrations) for more info
- A block tag: (e.g. `#logs` or `#wool`)

#### Extra syntax

- `?` between two blocks means 'try to use the first block, but if it doesn't exist, use the second block'. You can chain these together.
- `||` groups two blocks, allowing either one of them to be used. You can chain these together.

### Vanilla Materials

By default, a vanilla material (e.g. `oak_log`) will not accept custom blocks with the same material. For example, if you have a Nexo block with `oak_log` as its base material,
then that block will not be recognised as an `oak_log`.

If you want custom blocks to be accepted, place a `*` at the start, so `"oak_log"` becomes `"*oak_log"`.

### Block Tags

Block tags are groups of blocks. A list of vanilla tags can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Tag.html), and you can use them with `#tag`, e.g. `#logs`. These are especially useful in filters and conditions where you may use the same lists of blocks in different places.

You can create your own tags in [tags.yml](https://plugins.auxilor.io/the-item-lookup-system#item-tags) too, use `#libreforge:<tag_id>` to reference these.

### Using blocks from third-party plugins

| Plugin      | Block Lookup Key      |
|-------------|-----------------------|
| CraftEngine | `craftengine:<id>`    |
| ItemsAdder  | `itemsadder:<id>`     |
| Nexo        | `nexo:<id>`           |
| Oraxen      | `oraxen:<id>`         |

## Modifiers

Blocks can have modifiers applied to them to match or set specific block states. Add modifiers after the block key, separated by spaces.

The same modifier name can sometimes mean different things depending on the block, such as `face`, `shape`, and `mode`.

### Orientation and placement

| Modifier                    | Applies to                                            | Allowed values / notes                                                        | Example                                     |
|-----------------------------|-------------------------------------------------------|-------------------------------------------------------------------------------|---------------------------------------------|
| `direction:<face>`          | Directional blocks such as furnaces and dispensers    | `north`, `south`, `east`, `west`, `up`, `down`                                | `furnace direction:north`                   |
| `axis:<axis>`               | Orientable blocks such as logs and pillars            | `x`, `y`, `z`                                                                 | `oak_log axis:x`                            |
| `bisected:<half>`           | Tall blocks such as doors and tall grass              | `top`, `bottom`                                                               | `tall_grass bisected:top`                   |
| `slab:<type>`               | Slabs                                                 | `top`, `bottom`, `double`                                                     | `oak_slab slab:top`                         |
| `hinge:<side>`              | Doors                                                 | `left`, `right`                                                               | `oak_door hinge:left`                       |
| `rotation:<face>`           | Rotatable blocks such as signs and skulls             | Block face value                                                              | `oak_sign rotation:south`                   |
| `face:<face>`               | Multi-facing blocks such as vines and mushroom blocks | `north`, `south`, `east`, `west`, `up`, `down`; can be chained multiple times | `vine face:north face:east`                 |
| `face:<type>`               | Face-attached blocks such as buttons and levers       | `floor`, `wall`, `ceiling`                                                    | `lever face:ceiling`                        |
| `shape:<type>`              | Stairs                                                | `straight`, `inner_left`, `inner_right`, `outer_left`, `outer_right`          | `oak_stairs shape:inner_left`               |
| `shape:<type>`              | Rails                                                 | Values such as `north_south`, `east_west`, `ascending_north`                  | `rail shape:north_south`                    |
| `type:<type>`               | Chests                                                | `single`, `left`, `right`                                                     | `chest type:left`                           |
| `vertical_direction:<type>` | Pointed dripstone                                     | `up`, `down`                                                                  | `pointed_dripstone vertical_direction:down` |

### Toggle states

| Modifier      | Applies to                                                | Allowed values / notes            | Example                    |
|---------------|-----------------------------------------------------------|-----------------------------------|----------------------------|
| `waterlogged` | Waterloggable blocks                                      | Makes the block waterlogged       | `oak_slab waterlogged`     |
| `lit`         | Lightable blocks such as furnaces, candles, and campfires | Sets the block as lit             | `campfire lit`             |
| `powered`     | Powerable blocks such as buttons and pressure plates      | Sets the block as powered         | `stone_button powered`     |
| `open`        | Openable blocks such as doors, trapdoors, and fence gates | Sets the block as open            | `oak_door open`            |
| `eye`         | End portal frames                                         | Adds an eye of ender              | `end_portal_frame eye`     |
| `hanging`     | Hangable blocks such as lanterns                          | Sets the block as hanging         | `lantern hanging`          |
| `snowy`       | Snowable blocks such as grass blocks                      | Sets the block as snowy           | `grass_block snowy`        |
| `in_wall`     | Fence gates                                               | Marks the gate as being in a wall | `oak_fence_gate in_wall`   |
| `drag`        | Bubble columns                                            | Enables drag mode                 | `bubble_column drag`       |
| `berries`     | Cave vines                                                | Adds berries                      | `cave_vines_plant berries` |
| `unstable`    | TNT                                                       | Sets TNT as unstable              | `tnt unstable`             |

### Numeric values and counts

| Modifier           | Applies to                                                             | Allowed values / notes       | Example                      |
|--------------------|------------------------------------------------------------------------|------------------------------|------------------------------|
| `age:<value>`      | Ageable blocks such as crops and saplings                              | `0` to the maximum age       | `wheat age:7`                |
| `level:<value>`    | Levelled blocks such as water, cauldrons, and composters               | Block-specific level value   | `water level:3`              |
| `layers:<count>`   | Snow                                                                   | Number of snow layers        | `snow layers:4`              |
| `quantity:<count>` | Candles, sea pickles, and turtle eggs                                  | Number of items in the block | `candle quantity:3`          |
| `bites:<count>`    | Cakes                                                                  | `0-6`                        | `cake bites:3`               |
| `power:<level>`    | Analogue-powerable blocks such as daylight detectors and sculk sensors | `0-15`                       | `daylight_detector power:10` |
| `delay:<ticks>`    | Repeaters                                                              | `1-4`                        | `repeater delay:3`           |
| `charges:<count>`  | Respawn anchors                                                        | `0-4`                        | `respawn_anchor charges:3`   |
| `moisture:<level>` | Farmland                                                               | `0-7`                        | `farmland moisture:7`        |
| `distance:<value>` | Scaffolding                                                            | Scaffolding distance value   | `scaffolding distance:3`     |

### Block-specific variants

| Modifier            | Applies to       | Allowed values / notes                               | Example                      |
|---------------------|------------------|------------------------------------------------------|------------------------------|
| `leaves:<type>`     | Bamboo           | `none`, `small`, `large`                             | `bamboo leaves:large`        |
| `mode:<type>`       | Comparators      | `compare`, `subtract`                                | `comparator mode:subtract`   |
| `instrument:<type>` | Note blocks      | Values such as `harp`, `basedrum`, `snare`, `sticks` | `note_block instrument:harp` |
| `mode:<type>`       | Structure blocks | `save`, `load`, `corner`, `data`                     | `structure_block mode:save`  |

## Examples

Here are some practical examples combining keys and modifiers:

- `oak_stairs direction:north shape:straight waterlogged` — north-facing straight oak stairs, waterlogged
- `oak_door direction:east hinge:left open bisected:bottom` — open oak door, east-facing, left hinge, bottom half
- `wheat age:7` — fully grown wheat
- `oak_log axis:x` — sideways oak log
- `oak_slab slab:double waterlogged` — double oak slab, waterlogged
- `nexo:ruby_ore` — a custom Nexo block
- `candle lit quantity:4` — four lit candles
- `vine face:north face:west` — vine attached to north and west faces