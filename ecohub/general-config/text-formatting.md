---
title: Text Formatting
sidebar_position: 10
---

## Formatting text
Names, lore and messages across eco plugins accept several colour formats, and you can mix them in the same line:

- Legacy codes: `&a`, `&l`, `&r` and the rest of the `&` codes.
- Hex colours: `&#ff8800`, `<#ff8800>` and `{#ff8800}`.
- eco gradients: `<gradient:#f12711>Text</gradient:#f5af19>` and the short form `<g:#f12711>Text</g:#f5af19>`.
- [MiniMessage](https://docs.advntr.dev/minimessage/format.html) tags, such as `<bold>`, `<gradient:red:blue>` or `<hover:show_text:'Hi'>`.

```yaml
lore:
  - "&7A plain grey line."
  - "&8» &#FF0099Hex colours work too."
  - "<gradient:#00B4DB>Gradient text</gradient:#0083B0>"
```

## Sprites and heads
Item lore can show sprites and player heads inline, using MiniMessage:

- `<sprite:atlas:path>` shows a texture from an atlas, for example `<sprite:items:item/diamond>`.
- `<head:PlayerName>` shows a player's head.

```yaml
lore:
  - "<sprite:items:item/diamond> <gray>Worth a lot of diamonds."
  - "<head:Notch> <gray>Crafted by Notch."
```

Sprites and heads need Minecraft 1.21.9 or newer. On 1.21.8 they are removed and the rest of the line is kept.

:::info
Sprites and heads show in lore from EcoItems, Talismans, Reforges, EcoScrolls, EcoCrates and EcoMobs. Enchantment descriptions and EcoArmor lore do not support them.
:::

## Lore from other plugins
eco adds its lore to items when they are sent to players, and takes it off again when items come back, so the items stored on your server never change. Lore added by other plugins is left exactly as it was, including custom fonts, sprites and hover text.

eco marks its own lore lines so it can find them again. By default it also starts each line with an invisible legacy code, so other plugins can recognise eco's lines:

```yaml
# If eco's lore lines start with its legacy prefix. The prefix is invisible and lets other
# plugins recognise lines eco added. eco also marks its lines without text, so this can be
# disabled if another plugin shows or trips over the prefix. When disabled, eco can't find its
# lines again after another plugin rebuilds the lore from legacy text, so they can be duplicated.
display-legacy-prefix-marker: true
```

## Refreshing displayed items
When any eco plugin reloads, eco sends every player's inventory and open GUI again, so changed names and lore show straight away. Lore that uses time-based placeholders can also be refreshed on a timer.

```yaml
# If inventories and open GUIs are sent to players again after any eco plugin reloads, so
# displayed items update straight away instead of the next time the slot changes.
display-refresh-on-reload: true

# How often, in ticks, inventories are sent to players again so that time-based placeholders in
# lore stay up to date. 0 disables this. Players are spread across the interval, but every
# player's whole inventory is displayed again once per interval, so short intervals cost a lot of
# performance on busy servers. Keep it at 20 ticks or more.
display-refresh-interval: 0
```

These options are in eco's `config.yml`.

:::tip Troubleshooting
- **Lore doesn't update after a reload:** check that `display-refresh-on-reload` is `true` in eco's `config.yml`.
- **A sprite shows as text:** the server or client is older than 1.21.9, or the atlas or path is wrong.
- **Lore is duplicated or missing:** report it with the list of plugins that add lore to the item.
:::
