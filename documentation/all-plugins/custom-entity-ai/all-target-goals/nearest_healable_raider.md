# `minecraft:nearest_healable_raider`

Target nearest healable raider. Can only be applied to raiders

# Example Config
```yaml
- key: minecraft:nearest_healable_raider
  priority: 0
  args:
    target: # The types of entities to attack
      - illusioner
    checkVisibility: false # If visibility should be checked
    targetFilter: illusioner health:2 # The filter for targets to match (entity lookup string)
```