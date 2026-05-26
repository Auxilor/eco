# `minecraft:interact`

Allows an entity to interact with other mobs

# Example Config
```yaml
- key: minecraft:interact
  priority: 0
  args:
    target: cow # The type of entity to interact with
    range: 5 # The range at which to interact with other entities
    chance: 0.1 # The chance to interact, between 0 and 1
```