# `minecraft:ranged_bow_attack`

Perform a ranged bow attack, can only be applied to mobs that have bow attacks

# Example Config
```yaml
- key: minecraft:ranged_attack
  priority: 0
  args:
    speed: 1.2 # The speed
    interval: 40 # The average interval between attacks (in ticks)
    maxRange: 30 # The maximum range at which to attack from
```