---
title: The Price Lookup System
sidebar_position: 3
---

## What are Prices?

Lots of servers have lots of different types of economies. Maybe you have a standard vault economy, maybe you have an item-based economy, maybe you're using [points](https://hub.auxilor.io/wiki/libreforge/points), or something else entirely.

To simplify this, there's a unified way to handle all of this: the price system.
For price values, you can use [math](https://hub.auxilor.io/wiki/eco/math) to create adaptive/versatile pricing systems.

## Types
Below are the different Price types you can use.

| Type                                                                 | Alisases                                                                             |
|----------------------------------------------------------------------|--------------------------------------------------------------------------------------|
| Standard Economy                                                     | `$`, `coins`                                                                         |
| XP Points                                                            | `xp`, `exp`, `experience`                                                            |
| XP Levels                                                            | `l`, `levels`, `xplevels`                                                            |
| [Points](https://hub.auxilor.io/wiki/libreforge/points)                  | `<point_id>`, eg `souls`                                                             |
| [Magic](https://hub.auxilor.io/wiki/ecoskills/how-to-configure-magic) | `<magic_id>`, eg `mana`                                                              |
| Player Points (External)                                             | `p_points`, `player_points`                                                          |
| [EcoBits](https://hub.auxilor.io/wiki/ecobits/ecobits)                        | `<ecobits_id>`                                                                       |
| UltraEconomy (External)                                              | `<currency_id>`                                                                      |
| ExcellentEconomy (External)                                          | `<currency_id>`                                                                      |
| [Items](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system)           | Use the [item-lookup-system](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system) here |

## Effects System
Prices are seamlessly integrated into the effects system. Below, you'll find a list of all available effects and conditions.

| Effect/Condition   | Type      | Link                                                                       |
|--------------------|-----------|----------------------------------------------------------------------------|
| `give_price`       | Effect    | [Link](https://hub.auxilor.io/wiki/libreforge/give_price?category=effects)          |
| `pay_price`        | Effect    | [Link](https://hub.auxilor.io/wiki/libreforge/pay_price?category=effects)           |
| `can_afford_price` | Condition | [Link](https://hub.auxilor.io/wiki/libreforge/can_afford_price?category=conditions) |

## Display Names

You can specify display names for each price individually, however this might be quite
cumbersome, especially if you use prices in lots of places.

So, instead of configuring your price like this:

```yaml
price:
    value: 100 * %player_y%
    type: crystals # EcoBits currency
    display: "&b%value% Crystals ❖"
```

You can add the following to `/plugins/eco/lang.yml`:

```yaml
price-display:
    - type: crystals
      display: "&b%value% Crystals ❖"
```

This will override any per-price formatting, which should make your life much easier to achieve consistency
between different prices.

You can display prices in two ways, using `%value%` which will return the number unformatted (e.g.  `$1234567.89`), or you can use `%value_commas%` to format the price with commas (e.g. `$1,234,567.89`).

## Config Examples

```yaml
price:
    value: 100 * %player_y%
    type: crystals # EcoBits currency
```

```yaml
price:
    value: 16
    type: ecoitems:shiny_diamond
    display: "%value% &fShiny Diamonds" # Uses local display
```

```yaml
price:
    value: 5000
    type: xp
```

```yaml
price:
    value: 10
    type: mana # EcoSkills magic
```
