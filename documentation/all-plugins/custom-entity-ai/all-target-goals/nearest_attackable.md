# `minecraft:nearest_attackable`

Allows an entity to attack the closest target within a given subset of specific target types

# Example Config
```yaml
- key: minecraft:nearest_attackable
  priority: 0
  args:
    target: # The types of entities to attack
      - zombie
      - skeleton
      - wither_skeleton
    checkVisibility: true # If visibility should be checked
    checkCanNavigate: true # If navigation should be checked
    reciprocalChance: 5 # 1 in reciprocal chance (eg 1 in 20) of not activating on any given tick
    targetFilter: "" # (Optional, remove if empty) The filter for targets to match (entity lookup string)
```
