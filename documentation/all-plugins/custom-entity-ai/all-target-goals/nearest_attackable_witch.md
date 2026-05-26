# `minecraft:nearest_attackable_witch`

Allows an entity to attack the closest witch within a given subset of specific target types. Can only be applied to raiders

# Example Config
```yaml
- key: minecraft:nearest_attackable_witch
  priority: 0
  args:
    target: # The types of entities to attack
      - witch
    checkVisibility: true # If visibility should be checked
    checkCanNavigate: true # If navigation should be checked
    reciprocalChance: 5 # 1 in reciprocal chance (eg 1 in 20) of not activating on any given tick
    targetFilter: [] # The filter for targets to match (entity lookup string)
```