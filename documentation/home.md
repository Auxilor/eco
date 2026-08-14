---
title: Information (Read First)
sidebar_position: 0
---

## Welcome to EcoHub

EcoHub is the documentation platform for eco and every plugin built on it. Everything you need is in one place: guides written by us, and reference pages generated directly from the plugins themselves.

That second part is what makes EcoHub different from a normal wiki. Large parts of this site are produced by parsing the code source of eco, libreforge, and the plugins, then turning what it finds into pages. Every effect, condition, trigger, filter, mutator, price type, and config option you see documented here was read out of the code that actually runs on your server. When a plugin is updated, the pages are regenerated, so what you read matches the release you are running instead of a wiki that someone forgot to edit two years ago.

Use the left sidebar to move between sections. Each plugin has its own. If you already know what you are looking for, search is faster than browsing.

## Where to start

If you are new to eco plugins, read this page, then the eco section, then the plugin you bought. The eco section is not optional reading. Almost every config option in every plugin is written in a syntax that eco defines, so learning it once saves you from re-learning it per plugin.

The pages worth reading before anything else:

* **The lookup systems.** Items, blocks, entities, prices, and particles are all written as lookup strings rather than plain material names. One string can carry a custom item from another plugin, enchantments, a name, a texture, or a stack size. Start with the [Item Lookup System](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system).
* **[Math](https://hub.auxilor.io/wiki/eco/math).** Nearly every numeric option accepts an expression with placeholders instead of a fixed number, so values can scale with level, balance, or anything else you can read from a placeholder.
* **GUI pages.** Menus across all plugins share one mask and pattern format, so restyling every menu you own is a single skill.

## What is eco?
eco is the development library that powers all eco plugins. It handles a wide range of backend features, and allows eco plugins to be as powerful as they are.

On its own, eco adds very little that your players will see. It provides the systems underneath: the lookup strings, the math parser, the GUI framework, the storage layer, the placeholder engine, custom recipes, entity AI, and 60+ native integrations with other plugins, from claim and anticheat plugins to economies, shops, and custom item plugins. Every eco plugin leverages those systems automatically.

### What server versions are supported?

We currently support 1.21.8 and above.

For Minecraft 26.1, eco 7.5.0+ is required.

As of June 12th 2026, we have changed our versioning infrastructure to follow the week number of the year, e.g. 2026.1 would be the first week of the year 2026, all plugins align in versioning; making it easier for you to match up dependencies.
In the rare occassion, if a bug reaches production, we might release a sub-version which will suffix the version with its iteration, e.g. 2026.33.1.
Keep eco at least as new as the plugins that depend on it. New plugin releases often use systems added in a recent eco build, so updating plugins without updating eco is the most common cause of startup errors.

We recommend that you use Paper for the best compatibility and stability. While eco works on Spigot and other server software, Paper's optimizations and additional features allow for a smoother experience. Get Paper [here](https://papermc.io/downloads).

### How do I install it?

It's just like any other plugin on your server. Drop it into the /plugins/ folder and start/restart the server!

[![modrinth](/img/modrinth.png)](https://modrinth.com/plugin/eco-plugin)
[![polymart](/img/polymart.png)](https://polymart.org/product/773/eco)

There is no setup step and no license key. eco itself is free and open source under the MIT license, and the source is on [GitHub](https://github.com/Auxilor/eco).

Other projects such as EcoSkills or EcoEnchants are premium, these are under the GPL3 license 

## Where should I buy the plugins?

[![spigot](/img/spigot.png)](https://www.spigotmc.org/members/exanthiax.1580784/)
[![polymart](/img/polymart.png)](https://polymart.org/user/14873/exanthiax)
[![builtbybit](/img/builtbybit.png)](https://builtbybit.com/store/exanthiax.301/)

You can save up to 30% on the Plugins by buying the Eco Pack bundle. Available on [Voxel Shop](https://polymart.org/bundle/615/eco-pack) and [BuiltByBit](https://builtbybit.com/resources/bundle/ecopack.3418/).

## If something goes wrong

Check your console first. eco reports config errors with the file and the option that caused them, which is usually enough to find the mistake. If it isn't, join the [Discord](https://hub.auxilor.io/discord) with your server version, your eco version, your plugin version, and the full error from the console.

:::info Important
The contents of this documentation are for the latest versions of eco and all plugins. If you're using an older version, some features or options may not be available or supported.
:::
