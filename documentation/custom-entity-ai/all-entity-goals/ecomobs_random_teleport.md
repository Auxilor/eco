# `ecomobs:random_teleport`
:::infoRequires:
EcoMobs
:::

Allows a mob to teleport around randomly
# Example Config
```yaml
- key: ecomobs:random_teleport
  priority: 0
  args:
    interval: 20 # The time to wait between teleportation attempts (in ticks)
    range: 8 # The range to teleport within
```