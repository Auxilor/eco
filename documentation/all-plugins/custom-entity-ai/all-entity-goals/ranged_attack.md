# `minecraft:ranged_attack`

Perform a ranged attack, can only be applied to mobs that have ranged attacks

# Example Config
```yaml
- key: minecraft:ranged_attack
  priority: 0
  args:
    speed: 1.2 # The speed
    minInterval: 20 # The minimum interval between attacks (in ticks)
    maxInterval: 40 # The maximum interval between attacks (in ticks)
    maxRange: 30 # The maximum range at which to attack from
```