# eco
eco is a powerful Spigot plugin framework that simplifies the process of plugin creation and supercharges
your plugins.
It's the engine behind [EcoEnchants](https://polymart.org/resource/490), [Reforges](https://polymart.org/resource/1330),
[EcoItems](https://polymart.org/resource/1247), [EcoSkills](https://polymart.org/resource/1351),
[EcoArmor](https://polymart.org/resource/687), [Talismans](https://polymart.org/resource/611),
and many more.

<p>
    <a href="https://github.com/Auxilor/eco/releases">
        <img alt="spigot" src="https://img.shields.io/github/v/release/Auxilor/eco?color=informational"/>
    </a>
    <a href="https://bstats.org/plugin/bukkit/EcoEnchants" alt="bstats servers">
        <img src="https://img.shields.io/bstats/servers/7666?color=informational"/>
    </a>
    <a href="https://bstats.org/plugin/bukkit/EcoEnchants" alt="bstats players">
        <img src="https://img.shields.io/bstats/players/7666?color=informational"/>
    </a>
    <a href="https://discord.gg/ZcwpSsE/" alt="Discord">
        <img src="https://img.shields.io/discord/452518336627081236?label=discord&color=informational"/>
    </a>
    <a href="https://github.com/Auxilor/eco/actions/workflows/java-ci.yml" alt="Latest Dev Build">
        <img src="https://img.shields.io/github/actions/workflow/status/Auxilor/eco/java-ci.yml?branch=develop&color=informational"/>
    </a>
</p>

eco comes packed with all the tools you need in your plugins:

- Modern command API
- Native color parsing with full hex/RGB/MiniMessage support
- Yaml/JSON/TOML config system
- Persistent data storage API with Yaml/MySQL/MongoDB support
- Packet item display system
- Lightweight event loop based packet API
- Entity AI API with near-1:1 NMS mappings
- More events
- Extension API, essentially plugins for plugins
- Fluent dependency injection for NamespacedKey, Metadata values, etc.
- Ultra-fast ItemStack reimplementation bypassing ItemMeta
- Complete GUI API with pre-made components available from [ecomponent](https://github.com/Auxilor/ecomponent)
- Over 30 native integrations for other plugins
- First-class custom item support with lookup strings
- Math expression parsing via [Crunch](https://github.com/Redempt/Crunch)
- Particle lookups
- Complete Placeholder API
- Price system, supporting economy plugins, XP, Items, etc.
- NMS/Version-specific tooling
- Custom crafting recipe API with support for stacks and custom items
- Native plugin update checking
- Native bStats support
- Full Kotlin support and native extensions
- Tooling to make meta-frameworks, like [libreforge](https://github.com/Auxilor/libreforge)
- And much more

# For server owners
- Supports 1.17+

## Downloads

- Stable: [GitHub](https://github.com/Auxilor/eco/releases), [Polymart](https://polymart.org/resource/eco.773)
- Dev: [GitHub](https://github.com/Auxilor/eco/actions/workflows/java-ci.yml) (Open latest run and download)

# For developers

## Javadoc
The 6.53.0 Javadoc can be found [here](https://javadoc.jitpack.io/com/willfp/eco/6.53.0/javadoc/)

## Plugin Information

eco is a standalone plugin, so you will need to install it on any servers that have plugins which depend on it,
and specify it as a dependency in your plugin.yml:

```yaml
depend:
  - eco
```

## Dependency Information:

Gradle:

```kts
repositories {
        maven("https://repo.auxilor.io/repository/maven-public/")
}

```

```groovy
dependencies {
        compileOnly("com.willfp:eco:Tag")
}
```

Replace `Tag` with a release tag for eco, eg `6.53.0`.

Maven:

```xml
<repository>
        <id>auxilor</id>
        <url>https://repo.auxilor.io/repository/maven-public/</url>
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

Replace `Tag` with a release tag for eco, eg `6.53.0`.

## Build locally:

Run the following commands in your terminal of choice.

If you're on windows, you will need to have git bash installed.
```
git clone https://github.com/Auxilor/eco
cd eco
./gradlew build
```

## License

eco is licensed under the MIT license. *Click here to read [the entire license](https://github.com/Auxilor/eco/blob/master/LICENSE.md).*

<h1 align="center">
  Check out our partners!
  <br>
  <div style="width: 50%; margin: 0 auto;">
  <br>
    <a href="https://gamersupps.gg/discount/Auxilor?afmc=Auxilor" target="_blank">
      <img src="https://i.imgur.com/7mFhlQO.png" alt="supps banner">
    </a>
    <a href="https://dedimc.promo/Auxilor" target="_blank">
      <img src="https://i.imgur.com/x9aeH38.png" alt="dedimc banner">
    </a>
  <br>
  </div>
</h1>
