---
title: "AI Goals"
---

## What are AI goals?
AI goals are how entities behave, how they interact with the world around them. There are two types of goals: entity goals, and target goals, where entity goals are how entities behave, and target goals are how they decide who to attack.

## Configuring a goal
A goal consists of a key, a priority, and some arguments (options) - for example:
```yaml
- key: minecraft:tempt
  priority: 2
  args:
    items:
      - ecoitems:dark_blade
    speed: 0.6
    canBeScared: false
```
Priorities are calculated in descending order, so 0 is the top priority, etc.
All items use item lookup strings, as do all entities, so you can use custom items and entities in your goals.
