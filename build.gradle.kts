plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("maven-publish")
    id("java")
}

dependencies {
    implementation(project(":eco-api"))
    implementation(project(":eco-core:core-plugin"))
    implementation(project(":eco-core:core-proxy"))
    implementation(project(":eco-core:core-backend"))
    implementation(project(":eco-core:core-nms:v1_16_R3"))
    implementation(project(path = ":eco-core:core-nms:v1_17_R1", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_18_R1", configuration = "reobf"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")

        // SuperiorSkyblock2
        maven("https://repo.bg-software.com/repository/api/")

        // NMS (for jitpack compilation)
        maven("https://repo.codemc.org/repository/nms/")

        // bStats, mcMMO, BentoBox
        maven("https://repo.codemc.org/repository/maven-public/")

        // Spigot API, Bungee API
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        // PlaceholderAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // ProtocolLib
        maven("https://repo.dmulloy2.net/nexus/repository/public/")

        // WorldGuard
        maven("https://maven.enginehub.org/repo/")

        // FactionsUUID
        maven("https://ci.ender.zone/plugin/repository/everything/")

        // NoCheatPlus
        maven("https://repo.md-5.net/content/repositories/snapshots/")

        // CombatLogX
        maven("https://nexus.sirblobman.xyz/repository/public/")

        // IridiumSkyblock
        maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:23.0.0")

        // Test
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    configurations.all {
        exclude(group = "org.codehaus.plexus", module = "plexus-utils")
        exclude(group = "com.mojang", module = "brigadier")
        exclude(group = "org.kitteh", module = "paste-gg-api")
        exclude(group = "org.kitteh", module = "pastegg")
        exclude(group = "org.spongepowered", module = "configurate-hocon")
        exclude(group = "com.darkblade12", module = "particleeffect")
        exclude(group = "com.github.cryptomorin", module = "XSeries")
    }

    tasks {
        shadowJar {
            relocate("org.bstats", "com.willfp.eco.shaded.bstats")
            relocate("net.kyori.adventure.text.minimessage", "com.willfp.eco.shaded.minimessage")
        }

        compileJava {
            dependsOn(clean)
            options.encoding = "UTF-8"
        }

        java {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
            withSourcesJar()
        }

        test {
            useJUnitPlatform()

            // Show test results.
            testLogging {
                events("passed", "skipped", "failed")
            }
        }

        build {
            dependsOn(shadowJar)
        }
    }
}

group = "com.willfp"
version = findProperty("version")!!