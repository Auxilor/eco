group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly(project(":eco-core:core-backend"))
    compileOnly(project(":eco-core:core-folia"))

    // Libraries
    implementation("com.github.Redempt:Crunch:1.0.0")
    implementation("com.mysql:mysql-connector-j:9.6.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.12")
    implementation("org.jetbrains.exposed:exposed-core:1.2.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.2.0")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("net.kyori:adventure-platform-bukkit:4.4.1")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.6.2")
    implementation("io.hotmoka:toml4j:0.7.3") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    implementation("com.willfp:ModelEngineBridge:1.3.0")

    // Included in spigot jar
    compileOnly("com.google.code.gson:gson:2.8.8")
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    // Plugin dependencies
    compileOnly("me.libraryaddict.disguises:libsdisguises:11.0.14")
    compileOnly("net.dmulloy2:ProtocolLib:5.4.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.15") {
        exclude("*", "*")
    }
    compileOnly("com.palmergames.bukkit.towny:towny:0.102.0.9") {
        exclude(group = "com.zaxxer", module = "HikariCP")
    }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.2.049")
    compileOnly("me.clip:placeholderapi:2.11.7")
    compileOnly("com.github.LoneDev6:API-ItemsAdder:2.4.7")
    compileOnly("com.nexomc:nexo:1.19.1") {
        exclude(group = "*", module = "*")
    }
    compileOnly("net.momirealms:craft-engine-core:0.0.66")
    compileOnly("net.momirealms:craft-engine-bukkit:0.0.66")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.0")
    compileOnly("net.essentialsx:EssentialsX:2.21.2") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.bgsoftware:SuperiorSkyblockAPI:2025.2.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.9.9")
    compileOnly("com.github.N0RSKA:ScytherAPI:55a")
    compileOnly("org.black_ixx:playerpoints:3.2.6")
    compileOnly("io.lumine:Mythic:5.11.1")
    compileOnly("io.lumine:LumineUtils:1.21-SNAPSHOT")
    compileOnly("com.github.sirblobman.api:core:2.9-SNAPSHOT")
    compileOnly("com.github.sirblobman.combatlogx:api:11.6-SNAPSHOT")
    compileOnly("com.SirBlobman.combatlogx:CombatLogX-API:10.0.0.0-SNAPSHOT")
    compileOnly("com.denizenscript:denizen:1.3.0-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.iridium:IridiumSkyblock:4.1.2")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.5.10")
    compileOnly("net.william278.husktowns:husktowns-bukkit:3.1.4")
    compileOnly("com.github.jojodmo:ItemBridge:b0054538c1")
    compileOnly("de.oliver:FancyHolograms:2.9.1")
    compileOnly("su.nightexpress.coinsengine:CoinsEngine:2.7.0")
    compileOnly("su.nightexpress.nightcore:main:2.14.1")
    compileOnly("su.nightexpress.excellentshop:Core:4.22.0")

    compileOnly(fileTree("../../lib") {
        include("*.jar")
    })
}

tasks {
    shadowJar {
        minimize {
            exclude(dependency("org.jetbrains.exposed:.*:.*"))
            exclude(dependency("org.mariadb.jdbc:.*:.*"))
            exclude(dependency("com.willfp:ModelEngineBridge:.*"))
        }
    }

    processResources {
        filesMatching(listOf("**plugin.yml", "**eco.yml")) {
            expand("projectVersion" to project.version)
        }
    }
}
