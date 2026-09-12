---
title: Supported Plugins
sidebar_position: 1
---

# 🔌 Supported Plugins

Compatibility is a huge part of eco plugins, so we natively integrate with a large range of third-party plugins. These integrations are built directly into eco, libreforge, and the eco plugins themselves — no extra configuration needed. Just install the supported plugin alongside eco and everything will work automatically.

:::tip Don't see your plugin?
If a plugin isn't listed here, it doesn't necessarily mean it's incompatible — it just means there isn't a direct integration yet. Feel free to **request support** on the [Discord](https://hub.auxilor.io/discord)!
:::

---

## 🛡️ Antigrief / Claims / Protection

eco respects claim and protection regions from these plugins, preventing exploits and ensuring effects only apply where allowed.

| Plugin | Plugin |
| --- | --- |
| BentoBox | Lands |
| CombatLogX | PvPManager |
| DeluxeCombat | Residence |
| FabledSkyblock | RPGHorses |
| FactionsUUID | SuperiorSkyblock2 |
| GriefPrevention | Towny |
| HuskClaims | WorldGuard |
| HuskTowns | |
| IridiumSkyblock | |
| Kingdoms | |

---

## 🚫 Anticheat

eco exempts its own gameplay mechanics from anticheat false-positives.

| Plugin | Plugin |
| --- | --- |
| AAC | Spartan |
| Alice | Vulcan |

---

## 🐉 Custom Entities & Models

Use custom mobs, models, and disguises from these plugins inside eco plugins.

| Plugin | Support |
| --- | --- |
| MythicMobs | Custom entities in the Entity Lookup System |
| ModelEngine | Entity Lookup, EcoMobs models, EcoPets pet models, animation effect |
| BetterModel | EcoMobs models, animation effect |
| LevelledMobs | EcoMobs compatibility, mob level placeholders |
| LibsDisguises | EcoMobs disguises |

---

## 🎒 Custom Items & Blocks

### Integration into the Item Lookup System

Items from these plugins can be used anywhere eco accepts an [Item Lookup](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system) — in recipes, loot tables, GUIs, and more.

| Plugin | Plugin |
| --- | --- |
| CraftEngine | ItemBridge |
| CustomCrafting | ItemsAdder |
| Denizen | Nexo |
| ExecutableItems | Oraxen |
| HeadDatabase | Scyther |

### Custom Blocks

Custom blocks from these plugins are supported by eco, and work with block-based effects such as telekinesis.

| Plugin | Plugin |
| --- | --- |
| CraftEngine | Nexo |
| ItemsAdder | Oraxen |

### Integration from eco plugins

- **MythicMobs** — eco items can be used inside MythicMobs configurations (eco → MythicMobs)

---

## 🏪 Shops

eco integrates with shop plugins for sell prices, sellwands, and more.

| Plugin | Plugin |
| --- | --- |
| DeluxeSellwands | ShopGUIPlus |
| EconomyShopGUI (+ Premium) | zShop |
| ExcellentShop | |

---

## 💰 Economy & Currencies

eco hooks into economy providers for all money-related features — prices, rewards, and requirements.

| Plugin | Plugin |
| --- | --- |
| Vault | PlayerPoints |
| ExcellentEconomy | RoyaleEconomy |
| UltraEconomy | |

---

## 🌍 Custom Terrain

eco respects custom biomes and terrain from these world generators.

- Terra
- TerraformGenerator

---

## ⚔️ Skills & Jobs

Integrate with skills and jobs plugins for XP rewards, level requirements, and effects.

- **AuraSkills** (v2)
- **mcMMO**
- **Jobs Reborn**

---

## 🧑 NPCs

Use NPCs from these plugins in eco quests, shops, and more.

- Citizens
- FancyNpcs
- Shopkeepers

---

## 🧩 Misc

General-purpose integrations.

| Plugin | Support |
| --- | --- |
| PlaceholderAPI | Placeholders everywhere in eco plugins |
| Essentials | AFK detection, EcoEnchants enchantments in `/enchant` |
| CMI | AFK detection, EcoEnchants enchantments in `/enchant` |
| Multiverse-Inventories | Armor effects update correctly when switching worlds |
| RoseStacker | Stacked mobs correctly fire breed, death, and kill triggers; placeholders |

---

## ⚡ Effects System

These plugins add **effects, conditions, filters, mutators, and triggers** to the [Effects System](https://hub.auxilor.io/wiki/libreforge/configuring-an-effect), allowing you to create powerful custom gameplay mechanics with them.

| Plugin | Effects | Conditions | Filters | Mutators | Triggers |
| --- | --- | --- | --- | --- | --- |
| AuraSkills | ✅ | ✅ | | | |
| AxEnvoy | | | ✅ | | ✅ |
| AxTrade | | | | | ✅ |
| BetterModel | ✅ | | | | |
| Citizens | | | ✅ | | ✅ |
| CMI | ✅ | ✅ | ✅ | ✅ | ✅ |
| CustomCrops | | ✅ | ✅ | | ✅ |
| CustomFishing | | | ✅ | | ✅ |
| EcoBits | | | | | ✅ |
| EdPrison | ✅ | ✅ | | | |
| FancyNpcs | | | ✅ | | ✅ |
| HuskClaims | | ✅ | | | ✅ |
| HuskTowns | | ✅ | ✅ | | ✅ |
| Jobs Reborn | ✅ | | | | ✅ |
| Lands | ✅ | ✅ | ✅ | | ✅ |
| LuckPerms | ✅ | ✅ | ✅ | ✅ | ✅ |
| mcMMO | ✅ | ✅ | ✅ | | ✅ |
| ModelEngine | ✅ | | | | |
| MythicMobs | ✅ | | | | ✅ |
| NuVotifier | | | ✅ | | ✅ |
| PyroFishingPro | | | ✅ | | ✅ |
| Scyther | | | | | ✅ |
| Shopkeepers | | | | | ✅ |
| SkinsRestorer | ✅ | ✅ | ✅ | ✅ | ✅ |
| TAB | | ✅ | | | |
| TMMobcoins | ✅ | | | | |
| UltimateMobCoins | ✅ | | | | |
| Vault | ✅ | | | | |
| WorldGuard | | ✅ | ✅ | | ✅ |

### Server Software

When running on these server platforms, libreforge unlocks extra effects, conditions, and triggers that aren't available on Spigot.

- **Paper** (and forks)
- **Purpur**

---

:::info Total integrations
eco, libreforge, and the eco plugins natively support **75+ plugins** across all categories, and the list keeps growing!
:::
