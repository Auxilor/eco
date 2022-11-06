# eco
eco is a powerful Spigot development library that simplifies the process of plugin creation and supercharges
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
    <a href="https://plugins.auxilor.io/" alt="Docs (gitbook)">
        <img src="https://img.shields.io/badge/docs-gitbook-informational"/>
    </a>
    <a href="https://discord.gg/ZcwpSsE/" alt="Discord">
        <img src="https://img.shields.io/discord/452518336627081236?label=discord&color=informational"/>
    </a>
    <a href="https://github.com/Auxilor/eco/actions/workflows/java-ci.yml" alt="Latest Dev Build">
        <img src="https://img.shields.io/github/workflow/status/Auxilor/eco/Java%20CI/develop?color=informational"/>
    </a>
</p>

# For server owners
- Requires ProtocolLib to be installed: get the latest version [here](https://www.spigotmc.org/resources/protocollib.1997/)
- Supports 1.17+

## Downloads

- Stable (Recommended): [GitHub](https://github.com/Auxilor/eco/releases), [Polymart](https://polymart.org/resource/eco.773)
- Dev (Not Recommended): [GitHub](https://github.com/Auxilor/eco/actions/workflows/java-ci.yml) (Open latest run and download)

# For developers

## Javadoc
The 6.45.0 Javadoc can be found [here](https://javadoc.jitpack.io/com/willfp/eco/6.45.0/javadoc/)

## Plugin Information

eco is a standalone plugin, so you will need to install it on any servers that have plugins which depend on it,
and specify it as a dependency in your plugin.yml:

```yaml
depend:
  - eco
```

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

Replace `Tag` with a release tag for eco, eg `6.45.0`.

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

Replace `Tag` with a release tag for eco, eg `6.45.0`.

## Build locally:

Run the following commands in your terminal of choice.

If you're on windows, you will need to have git bash installed.
```
git clone https://github.com/Auxilor/eco
cd eco
./gradlew build
```

## License

*Click here to read [the entire license](https://github.com/Auxilor/eco/blob/master/LICENSE.md).*

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
