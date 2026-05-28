# `minecraft:use_item`

Allows an entity to use an item

# Example Config
```yaml
- key: minecraft:use_item
  priority: 0
  args:
    item: apple # The item
    sound: item_totem_use # The sound to play
    condition: zombie health:10 # The condition the entity must match to use the item - takes an entity lookup string (eg requiring a certain amount of health)
```