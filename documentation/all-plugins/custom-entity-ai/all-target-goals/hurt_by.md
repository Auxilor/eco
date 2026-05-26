# `minecraft:hurt_by`

Allows an entity to react when hit by a set target

# Example Config
```yaml
- key: minecraft:hurt_by
  priority: 0
  args:
    blacklist: # The entities that the entity shouldn't react to
      - player
```