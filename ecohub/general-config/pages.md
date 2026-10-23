---
title: GUI Pages
sidebar_position: 8
---

## Custom Pages
Custom pages are used in most of the plugins, and understanding how to correctly configure a GUI page is important to creating your menus.

### How to make a page

Pages consist of three key components, a mask, a pattern, and sometimes a page number. A pattern is the layout of the background or filler items. Think of the pattern section as the GUI, with 9 columns and up to 6 rows. 

Patterns use a simple format:
`0` is an empty slot.
`1-9` are the first nine different filler items
`a-z` is the remaining 26 items.
In total you could display 35 different items as "filler" items in your GUI.

A mask is the items to be shown in the pattern layout, these work from the top down. You can use the [Item Lookup System](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system) here to add custom items, apply names or any other of the options.
The first item in the list will represent `1` in the pattern, the second item in the list is `2`, etc..

### Example Page Config

```yaml
    - page: 1
      mask:
        items: # The order of items to display
          - gray_stained_glass_pane # The 1st Item
          - black_stained_glass_pane # The 2nd Item
        pattern: 
          - "222222222"
          - "211111112"
          - "222222222"
```

This example has a surrounding layer of `gray_stained_glass_pane` and a center strip of `black_stained_glass_pane`.

![Page](https://i.imgur.com/tQLXe3F.png)

## Custom GUI Slots
### What are custom GUI slots?

When configuring a GUI in a plugin, you might stumble across this:

```yaml
# Custom GUI slots; see here for a how-to: https://hub.auxilor.io/wiki/eco/pages#custom-gui-slots
custom-slots: []
```

This means you can add custom items (with commands) to your GUIs for that extra layer of customizability.

### How to make a custom GUI slot

Quite simply, a GUI slot looks like this:

```yaml
custom-slots:
  - row: 6 
    column: 9
    item: ecoitems:skill_gui_item 
    lore: []
    left-click:
      - console:op %player% # Commands can start with console: to be ran by console, and use %player% as a placeholder.
      - spawn # If you don't specify, then the command will be ran by the player.
    right-click: []
    shift-left-click: []
    shift-right-click: []
```

If you have no right click / shift left click / etc.. commands to add, you can omit the sections, like this:

```yaml
custom-slots:
  - row: 1
    column: 5
    item: player_head texture:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODU3MDVjZjg2NGRmMmMxODJlMzJjNDg2YjcxNDdjYmY3ODJhMGFhM2RmOGE2ZDYxNDUzOTM5MGJmODRmYjE1ZCJ9fX0=
    right-click:
      - console:eco give %player% 1000
```

To show the same custom slot in several places, use a `locations` list instead of `row` and `column`:

```yaml
custom-slots:
  - locations:
      - row: 6
        column: 1
      - row: 6
        column: 9
    item: compass name:"&aSpawn"
    left-click:
      - spawn
```

And you can add as many custom slots as you want, like this:

```yaml
custom-slots:
  - <slot 1>
  - <slot 2>
  - <slot 3>
  - ...etc
  ```

## Page Change Buttons

Paged GUIs have a forwards and a backwards button, usually configured like this:

```yaml
forwards-arrow:
  item: arrow name:"&fNext Page" # The item shown when there is a next page.
  item-inactive: gray_dye name:"&7Next Page" # Optional. The item shown on the last page; leave it out to hide the button there.
  location:
    row: 6
    column: 8
```

To make one button cover several slots, add a `locations` list. The button then shows in every listed slot instead of the single `location`:

```yaml
forwards-arrow:
  item: arrow name:"&fNext Page"
  locations:
    - row: 6
      column: 7
    - row: 6
      column: 8
    - row: 6
      column: 9
```

Plugins that use a flat `row` and `column` under the button accept the same `locations` list.

## Multiple Slots for Buttons

Every button placed with a `row` and `column` (or a `location` section), such as close, back, info and confirm buttons, accepts the same `locations` list. When `locations` has entries, the button shows in each listed slot and the single `row` / `column` is ignored. Leave it as `locations: [ ]` to keep the single slot.
