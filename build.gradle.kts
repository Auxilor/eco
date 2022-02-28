buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
}

plugins {
    id("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
    id("java")
    kotlin("jvm") version "1.6.10"
}

dependencies {
    implementation(project(":eco-api"))
    implementation(project(":eco-core:core-plugin"))
    implementation(project(":eco-core:core-proxy"))
    implementation(project(":eco-core:core-backend"))
    implementation(project(path = ":eco-core:core-nms:v1_17_R1", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_18_R1", configuration = "reobf"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")

        // CustomCrafting
        maven("https://maven.wolfyscript.com/repository/public/")

        // SuperiorSkyblock2
        maven("https://repo.bg-software.com/repository/api/")

        // NMS (for jitpack compilation)
        maven("https://repo.codemc.org/repository/nms/")

        // mcMMO, BentoBox
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

        // MythicMobs
        maven("https://mvn.lumine.io/repository/maven-public/")

        // Crunch
        maven("https://redempt.dev")

        // LibsDisguises
        maven("https://repo.md-5.net/content/groups/public/")
    }

    dependencies {
        compileOnly(kotlin("stdlib", version = "1.6.10"))
        compileOnly("org.jetbrains:annotations:23.0.0")

        // Test
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        
        // Adventure
        compileOnly("net.kyori:adventure-api:4.9.3")
        compileOnly("net.kyori:adventure-text-serializer-gson:4.9.3")
        compileOnly("net.kyori:adventure-text-serializer-legacy:4.9.3")

        // Other
        compileOnly("com.google.guava:guava:31.0.1-jre")
        compileOnly("com.github.ben-manes.caffeine:caffeine:3.0.5")
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

    configurations.testImplementation {
        setExtendsFrom(listOf(configurations.compileOnly.get()))
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = "17"
            }
            targetCompatibility = "17"
            sourceCompatibility = "17"
        }

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