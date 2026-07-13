group = "com.willfp"
version = rootProject.version

val bStatsVersion = "v3.2.1"
val bStatsTargetPackage = "com.willfp.eco.libs.bstats"
val bStatsGeneratedDir = layout.buildDirectory.dir("generated/sources/bstats")

val vendorBStats by tasks.registering {
    group = "vendor"
    description = "Vendors bStats $bStatsVersion source under $bStatsTargetPackage"

    inputs.property("bStatsVersion", bStatsVersion)
    outputs.dir(bStatsGeneratedDir)

    doLast {
        val attribution = """
            /*
             * Sourced from bStats (https://bstats.org), MIT License.
             * https://github.com/Bastian/bStats-Metrics
             * Vendored under $bStatsTargetPackage to avoid classpath conflicts.
             */
        """.trimIndent() + "\n"

        val cloneDir = temporaryDir.resolve("bstats-metrics")
        cloneDir.deleteRecursively()

        val clone = ProcessBuilder(
            "git", "clone", "--depth", "1", "--branch", bStatsVersion,
            "https://github.com/Bastian/bStats-Metrics.git",
            cloneDir.absolutePath
        ).redirectErrorStream(true).start()
        val cloneOutput = clone.inputStream.bufferedReader().readText()
        check(clone.waitFor() == 0) { "Failed to clone bStats-Metrics at $bStatsVersion:\n$cloneOutput" }

        val outDir = bStatsGeneratedDir.get().asFile
        outDir.deleteRecursively()

        listOf("base", "bukkit").forEach { module ->
            val javaRoot = cloneDir.resolve("$module/src/main/java")
            val bStatsRoot = javaRoot.resolve("org/bstats")
            bStatsRoot.walkTopDown()
                .filter { it.isFile && it.extension == "java" }
                .forEach { file ->
                    val rel = file.relativeTo(bStatsRoot)
                    val outFile = outDir.resolve("com/willfp/eco/libs/bstats/$rel")
                    outFile.parentFile.mkdirs()
                    outFile.writeText(
                        attribution + file.readText().replace("org.bstats", bStatsTargetPackage)
                    )
                }
        }

        cloneDir.deleteRecursively()
    }
}

sourceSets.main {
    java.srcDir(bStatsGeneratedDir)
}

tasks.compileJava { dependsOn(vendorBStats) }
tasks.compileKotlin { dependsOn(vendorBStats) }
tasks.sourcesJar { dependsOn(vendorBStats) }

dependencies {
    compileOnly(project(":eco-core:core-backend"))

    // Libraries (provided at runtime via Paper library loader)
    compileOnly("com.mysql:mysql-connector-j:9.6.0")
    compileOnly("org.mariadb.jdbc:mariadb-java-client:2.7.12")
    implementation("org.jetbrains.exposed:exposed-core:1.2.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.2.0")
    compileOnly("com.zaxxer:HikariCP:7.0.2")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("org.mongodb:mongodb-driver-kotlin-coroutine:5.6.2")
    compileOnly("io.hotmoka:toml4j:0.7.3") {
        exclude(group = "com.google.code.gson", module = "gson")
    }
    implementation("com.willfp:ModelEngineBridge:1.4.0")

    // Included in spigot jar
    compileOnly("com.google.code.gson:gson:2.8.9")
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

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
    compileOnly("net.momirealms:craft-engine-core:26.6.2")
    compileOnly("net.momirealms:craft-engine-bukkit:26.6.2")
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
    compileOnly("com.denizenscript:denizen:1.3.0-SNAPSHOT") {
        exclude(group = "*", module = "*")
    }
    compileOnly("com.iridium:IridiumSkyblock:4.1.2")
    compileOnly("net.william278.huskclaims:huskclaims-bukkit:1.5.10")
    compileOnly("net.william278.husktowns:husktowns-bukkit:3.1.4")
    compileOnly("com.github.jojodmo:ItemBridge:b0054538c1")
    compileOnly("de.oliver:FancyHolograms:2.9.1")
    compileOnly("su.nightexpress.excellenteconomy:ExcellentEconomy:2.8.0")
    compileOnly("su.nightexpress.nightcore:main:2.15.3")
    compileOnly("su.nightexpress.excellentshop:Core:4.22.0")
    compileOnly("dev.kitteh:factions:4.4.0")

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
