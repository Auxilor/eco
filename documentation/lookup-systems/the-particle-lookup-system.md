---
title: The Particle Lookup System
slug: /the-particle-lookup-system
sidebar_position: 1.4
---

## What is the Particle Lookup System?
In Minecraft, there are more particles than meet the eye. While there are the [default particles](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html), there are also colored particles. The particle lookup system exists to give a unified way of getting both default and these custom colored particles.

## Keys Explained
Particle keys are much simpler than entity or item keys. There are no arguments, no modifiers, nothing like that. (After all, they're just particles). Instead, there are two ways of specifying a particle:

Default particle names, such as `magic`, `end_rod`, etc. You can find a list of all default particles [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html).

Custom colored particles, written as `rgb:0faab5` or `color:15fe2f`. You can pass in any valid hex code.

