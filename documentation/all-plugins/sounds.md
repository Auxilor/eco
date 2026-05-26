---
title: Sound Configs
sidebar_position: 9
---

## Sounds
In many places, you may want to play some sounds, such as when a Booster is activated, a GUI is clicked, or an item is bought from a shop. Auxilor plugins allow you to configure sounds in these places to add that extra layer of immersion and feedback for your players.

There are two ways to configure sounds for players, either through fixed locations, or via effects.

## How to configure a sound
Across the plugins you will find various sound options, but they all follow the same format:

### Config Sound Options

```yaml
sounds:
  enabled: true # Whether the sound should be played or not.
  sound: BLOCK_ANVIL_USE # The sound https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
  pitch: 1 # The pitch (0.5 - 2)
  volume: 2 # The volume (0.5 - 2)
  category: PLAYERS # The sound category https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html
```

As you can see, you can enable or disable the sound, choose the sound to play, and adjust the pitch, volume and category of the sound. <br/>
*You can find all the sounds here: [Sounds](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html)* <br/>
*You can find all the sound categories here: [Sound Categories](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html)*

### Effects Sound Options
When configuring a sound effect, the options are slightly different, it just doesn't have the `enabled` option.

```yaml
effects:
  - id: play_sound
    args:
      sound: BLOCK_ANVIL_USE # The sound https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
      pitch: 1 # The pitch (0.5 - 2)
      volume: 2 # The volume (0.5 - 2)
      category: PLAYERS # The sound category https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html
```

## Plugin Examples
Sometimes you might choose to disable the config sound and use an effect to play a sound instead. This is usually because config sounds are static, or more universal, whereas effects can be more dynamic and have conditions or placeholders.

We can use the Boosters plugin as an example here. In the config.yml, you can configure a sound to play when a booster is activated, but this would apply to all boosters. <br/>
If you wanted a specific sound to play for the "1.5x Sell Booster", you could instead add a `play_sound` effect to the `activation-effects` section of the booster config, and this sound would only play when that specific booster is activated.

:::noteCustom Sounds
Currently we only support Bukkit sounds, but we are looking into adding support for custom sounds in the future.
:::