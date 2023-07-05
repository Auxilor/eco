group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly(project(":eco-core:core-proxy"))
    compileOnly(project(":eco-core:core-backend"))

    // Libraries
    implementation("com.github.WillFP:Crunch:1.1.3")
    implementation("mysql:mysql-connector-java:8.0.25")
    implementation("org.jetbrains.exposed:exposed-core:0.37.3")
    implementation("org.jetbrains.exposed:exposed-dao:0.37.3")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.37.3")
    implementation("com.zaxxer:HikariCP:5.0.0")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")
    implementation("org.javassist:javassist:3.29.2-GA")
    implementation("org.mongodb:mongodb-driver-sync:4.6.0")
    implementation("org.litote.kmongo:kmongo-coroutine:4.9.0")
    implementation("com.moandjiezana.toml:toml4j:0.7.2") {
        exclude(group = "com.google.code.gson", module = "gson")
    }

    // Included in spigot jar
    compileOnly("com.google.code.gson:gson:2.8.8")
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    // Plugin dependencies
    compileOnly("com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.7-SNAPSHOT")
    compileOnly("com.github.TechFortress:GriefPrevention:16.17.1")
    compileOnly("com.github.TownyAdvanced:Towny:0.97.2.6") {
        exclude(group = "com.zaxxer", module = "HikariCP")
    }
    compileOnly("com.github.angeschossen:LandsAPI:6.26.18")
    compileOnly("com.github.angeschossen:PluginFrameworkAPI:1.0.0")
    compileOnly("fr.neatmonster:nocheatplus:3.16.1-SNAPSHOT")
    compileOnly("com.github.jiangdashao:matrix-api-repo:317d4635fd")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.202")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.github.oraxen:oraxen:1.155.0")
    compileOnly("com.github.brcdev-minecraft:shopgui-api:3.0.0")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:2.4.7")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.1")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.0")
    compileOnly("com.github.EssentialsX:Essentials:2.18.2")
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:1.8.3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.WhipDevelopment:CrashClaim:f9cd7d92eb")
    compileOnly("com.wolfyscript.wolfyutilities:wolfyutilities:3.16.0.0")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.1.2")
    compileOnly("com.github.Gypopo:EconomyShopGUI-API:1.4.6")
    compileOnly("com.github.N0RSKA:ScytherAPI:55a")
    compileOnly("com.ticxo.modelengine:api:R3.0.1")
    compileOnly("me.TechsCode:UltraEconomyAPI:1.0.0")
    compileOnly("org.black_ixx:playerpoints:3.2.5")
    compileOnly("com.github.Ssomar-Developement:SCore:3.4.7")
    compileOnly("io.lumine:Mythic:5.3.5")
    compileOnly("io.lumine:LumineUtils:1.19-SNAPSHOT")
    compileOnly("com.SirBlobman.combatlogx:CombatLogX-API:10.0.0.0-SNAPSHOT")
    compileOnly("com.github.sirblobman.combatlogx:api:11.0.0.0-SNAPSHOT")
    compileOnly("LibsDisguises:LibsDisguises:10.0.26")
    compileOnly("com.denizenscript:denizen:1.2.7-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }

    compileOnly(fileTree("../../lib") {
        include("*.jar")
    })
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("org.litote.kmongo:kmongo-coroutine:.*"))
            exclude(dependency("org.jetbrains.exposed:.*:.*"))
        }
    }

    processResources {
        filesMatching(listOf("**plugin.yml", "**eco.yml")) {
            expand("projectVersion" to project.version)
        }
    }
}
