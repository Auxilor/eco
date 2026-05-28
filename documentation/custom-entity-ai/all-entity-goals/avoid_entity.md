# `minecraft:avoid_entity`

Avoids an entity

# Example Config
```yaml
- key: minecraft:avoid_entity
  priority: 0
  args:
    entity: ecomobs:steel_golem # The entity to avoid
    distance: 10 # The distance to flee to
    slowSpeed: 0.8 # The slow flee speed
    fastSpeed: 2.0 # The fast flee speed
```