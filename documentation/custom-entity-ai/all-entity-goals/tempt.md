# `minecraft:tempt`

Allows an entity to be tempted by an item

# Example Config
```yaml
- key: minecraft:tempt
  priority: 0
  args:
    speed: 0.8 # The speed at which the entity will follow the item
    items: # The items that the entity will be tempted by
      - ecoitems:dark_blade
      - diamond 16
    canBeScared: true # If the entity can be scared and lose track of the item
```