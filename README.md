<h1 align="center">
  <br>
  <img src="https://i.imgur.com/p5uR2Qp.png" alt="eco logo" width="256">
  <br>
</h1>

<h4 align="center">eco - Simplify spigot development.</h4>

# Installation for Development

eco is a standalone plugin, so you will need to install it on any servers that have plugins which depend on it, and specify it as a dependency in your plugin.yml like this:

```yaml
depend: [RedLib]
```

eco is available from any of these places:

- [GitHub](https://github.com/Auxilor/eco/releases)
- [Spigot](https://www.spigotmc.org/resources/eco.87955/)
- [Polymart](https://polymart.org/resource/eco.773)
- [Build it locally](https://github.com/Auxilor/eco#build-locally).

## Get from JitPack:

Gradle:

```groovy
repositories {
        maven { url 'https://jitpack.io' }
}

```

```groovy
dependencies {
        compileOnly 'com.willfp:eco:Tag'
}
```

Replace `Tag` with a release tag for eco, eg `6.0.0`.

Maven:

```xml
<repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
        <groupId>com.willfp</groupId>
        <artifactId>eco</artifactId>
        <version>Tag</version>
        <scope>provided</scope>
</dependency>
```

Replace `Tag` with a release tag for eco, eg `6.0.0`.

## Build locally:

Run the following commands in your terminal of choice.

If you're on windows, you will need to have git bash installed.
```
git clone https://github.com/Auxilor/eco
cd eco
./gradlew build
```

# Features

Here's a list of some (not all) of the features of eco:

- Command system with subcommands
- Reworked config system
- JSON Config Support
- Client-Side item display
- World drop system
- Event manager
    - PlayerJumpEvent
    - ArmorEquipEvent
    - EntityDeathByEntityEvent
    - NaturalExpGainEvent
- Plugin extensions (Plugins for plugins)
- GUI System
- Integration system for external plugins
    - Anticheat support
        - AAC
        - Matrix
        - NCP
        - Spartan
        - Vulcan
    - Antigrief/Combat support
        - CombatLogX (V10 + V11)
        - FactionsUUID
        - GriefPrevention
        - Kingdoms
        - Lands
        - Towny
        - WorldGuard
    - mcMMO support
    - Custom Items support
        - Oraxen
    - PlaceholderAPI support
- NMS Proxy / Wrapper system built in
- Custom Items system
- Crafting Recipe handler
- Tuples
- Support uploading to / downloading from hastebin
- Packet System (via ProtocolLib)
- Dependency Injection systems
- Prerequisite system
- API additions (via utility classes)
    - Get bow from arrow
    - Break a block as a player
    - Get a vein of blocks
    - Create 2D lists
    - Create NamespacedKeys safely
    - Random number, distribution, roman numerals
    - Set skull texture
    - Format all strings
        - Hex Support
        - Gradient Support
        - Placeholder Support
    - Get a scoreboard team from any color
    - Telekinesis (Drops straight to inventory) system
    - More vector options
- Update checker
- bStats integration
- Reworked systems for:
    - NamespacedKey
    - MetadataValue
    - Runnables / Scheduling

... and a lot more!

## License

*Click here to read [the entire license](https://github.com/Auxilor/eco/blob/master/LICENSE.md).*

<h1 align="center">
  <br>
    <a href="https://dedimc.promo/Auxilor" target="_blank">
      <img src="https://i.imgur.com/zdDLhFA.png" alt="dedimc banner">
    </a>
  <br>
</h1>