# `minecraft:non_tame_random`

Target a random non-tame entity

# Example Config
```yaml
- key: minecraft:non_tame_random
  priority: 0
  args:
    target: # The types of entities to attack
      - cow
      - pig
      - sheep
    checkVisibility: true # If visibility should be checked
    targetFilter: "" # The filter for targets to match (entity lookup string)
```