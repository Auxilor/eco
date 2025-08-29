group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly(project(":eco-core:core-proxy"))
    compileOnly(project(":eco-core:core-backend"))

    // Libraries
    implementation("com.github.WillFP:Crunch:1.1.3")
    implementation("com.mysql:mysql-connector-j:8.4.0")
    implementation("org.jetbrains.exposed:exposed-core:0.53.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.53.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
    implementation("org.javassist:javassist:3.29.2-GA")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.1.2")
    implementation("com.moandjiezana.toml:toml4j:0.7.2") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    implementation("com.willfp:ModelEngineBridge:1.2.0")

    // Included in spigot jar
    compileOnly("com.google.code.gson:gson:2.8.8")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")

    // Plugin dependencies
    compileOnly("net.dmulloy2:ProtocolLib:5.1.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7-SNAPSHOT")
    compileOnly("com.github.TechFortress:GriefPrevention:16.17.1")
    compileOnly("com.github.TownyAdvanced:Towny:0.99.5.21") {
        exclude(group = "com.zaxxer", module = "HikariCP")
    }
    compileOnly("com.github.angeschossen:LandsAPI:6.26.18")
    compileOnly("com.github.angeschossen:PluginFrameworkAPI:1.0.0")
    compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    compileOnly("com.github.jiangdashao:matrix-api-repo:317d4635fd")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.202")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.0.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:2.4.7")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.0")
    compileOnly("com.github.EssentialsX:Essentials:2.18.2")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:1.8.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.WhipDevelopment:CrashClaim:c697d3e9ef")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.5")
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.4.6")
    compileOnly("com.github.N0RSKA:ScytherAPI:55a")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("io.lumine:Mythic:5.7.0")
    compileOnly("io.lumine:LumineUtils:1.19-SNAPSHOT")
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT")
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")
    compileOnly("com.SirBlobman.combatlogx:CombatLogX-API:10.0.0.0-SNAPSHOT")
    compileOnly("LibsDisguises:LibsDisguises:10.0.26")
    compileOnly("com.denizenscript:denizen:1.2.7-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.iridium:IridiumSkyblock:4.0.8")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.5")
    compileOnly("net.william278.husktowns:husktowns-bukkit:3.0.8")
    compileOnly("com.github.jojodmo:ItemBridge:b0054538c1")
    compileOnly("de.oliver:FancyHolograms:2.4.0")

    compileOnly(fileTree("../../lib") {
        include("*.jar")
    })
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("org.jetbrains.exposed:.*:.*"))
            exclude(dependency("com.willfp:ModelEngineBridge:.*"))
        }
    }

    processResources {
        filesMatching(listOf("**plugin.yml", "**eco.yml")) {
            expand("projectVersion" to project.version)
        }
    }
}
