# `minecraft:random_stroll`

Allows an entity to choose a random direction to walk towards

# Example Config
```yaml
- key: minecraft:random_stroll
  priority: 0
  args:
    speed: 0.4 # The speed at which to move around
    interval: 80 # The amount of ticks (on average) to wait between strolling around
    canDespawn: false # If the entity can despawn
```