---
title: The Particle Lookup System
sidebar_position: 5
---

## What is the Particle Lookup System?
In Minecraft, there are more particles than meet the eye. While there are the [default particles](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html), there are also colored particles. The particle lookup system exists to give a unified way of getting both default and these custom colored particles.

## Keys Explained
Particle keys are much simpler than entity or item keys. There are no arguments, no modifiers, nothing like that. (After all, they're just particles). Instead, there are two ways of specifying a particle:

Default particle names, such as `magic`, `end_rod`, etc. You can find a list of all default particles [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).

Colored particles, written as a type followed by a colon and its options, e.g. `rgb:0faab5`. The available types are listed below. You can pass in any valid hex code, with or without a leading `#`.

## Colored Particles

### Dust
Written as `rgb:<color>`, `color:<color>`, `hex:<color>`, or `dust:<color>`.

You can optionally pass a size after the color, which defaults to `1`:

```yaml
particle: rgb:0faab5      # A blue dust particle
particle: dust:ff0000:3   # A large red dust particle
```

### Dust Transition
A dust particle that fades from one color to another. Written as `dust_transition:<from>:<to>` or `transition:<from>:<to>`, with an optional size:

```yaml
particle: dust_transition:ff0000:0000ff     # Fades from red to blue
particle: transition:ff0000:0000ff:3        # The same, but larger
```

### Entity Effect
The swirling particle used by potion effects. Written as `entity_effect:<color>`:

```yaml
particle: entity_effect:15fe2f
```

## Particles That Need Data

Some default particles can't be spawned by name alone, because Minecraft requires extra data to render them. `dust`, `dust_color_transition`, and `entity_effect` need a color, and particles like `block`, `item`, and `falling_dust` need a block or item.

Writing one of these as a plain name (e.g. `particle: dust`) does nothing. Use the colored types above instead. Particles that need a block or item are not currently supported.

