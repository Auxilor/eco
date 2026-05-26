# `minecraft:move_through_village`

Allows the entity to create paths around the village

# Example Config
```yaml
- key: minecraft:move_through_village
  priority: 0
  args:
    speed: 0.8 # The speed at which to move through the village
    onlyAtNight: false # If the entity can only move through the village at night
    distance: 20 # The distance to move through the village
    canPassThroughDoors: true # If the entity can pass through doors
```