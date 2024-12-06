import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    }
}

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "8.3.5"
    id("maven-publish")
    id("java")
    kotlin("jvm") version "2.1.0"
}

dependencies {
    implementation(project(":eco-api"))
    implementation(project(path = ":eco-core:core-plugin", configuration = "shadow"))
    implementation(project(":eco-core:core-proxy"))
    implementation(project(":eco-core:core-backend"))
    implementation(project(":eco-core:core-backend-modern"))
    implementation(project(path = ":eco-core:core-nms:v1_17_R1", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_18_R1", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_18_R2", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_19_R1", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_19_R2", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_19_R3", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_20_R1", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_20_R2", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_20_R3", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_21", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_21_3", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_21_4", configuration = "reobf"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "kotlin")

    repositories {
        mavenCentral()

        maven("https://repo.auxilor.io/repository/maven-public/")

        maven("https://jitpack.io") {
            content { includeGroupByRegex("com\\.github\\..*") }
        }

        // Paper
        maven("https://repo.papermc.io/repository/maven-public/")

        // SuperiorSkyblock2
        maven("https://repo.bg-software.com/repository/api/")

        // mcMMO, BentoBox
        maven("https://repo.codemc.io/repository/maven-public/")

        // Spigot API, Bungee API
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

        // PlaceholderAPI
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        // ProtocolLib
        maven("https://repo.dmulloy2.net/nexus/repository/public/")

        // WorldGuard
        maven("https://maven.enginehub.org/repo/")

        // FactionsUUID
        //maven("https://ci.ender.zone/plugin/repository/everything/")

        // NoCheatPlus
        maven("https://repo.md-5.net/content/repositories/snapshots/")

        // CombatLogX
        maven("https://nexus.sirblobman.xyz/repository/public/")

        // MythicMobs
        maven("https://mvn.lumine.io/repository/maven-public/")

        // Crunch
        maven("https://redempt.dev")

        // LibsDisguises
        maven("https://repo.md-5.net/content/groups/public/")

        // PlayerPoints
        maven("https://repo.rosewooddev.io/repository/public/")

        // Denizen
        maven("https://maven.citizensnpcs.co/repo")

        // IridiumSkyblock
        maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")

        // HuskPlugins
        maven("https://repo.william278.net/releases")

        // FancyHolograms
        maven("https://repo.fancyplugins.de/releases")
    }

    dependencies {
        // Kotlin
        implementation(kotlin("stdlib", version = "2.1.0"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

        // Included in spigot jar, no need to move to implementation
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly("com.google.guava:guava:32.0.0-jre")

        // Test
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

        // Adventure
        implementation("net.kyori:adventure-api:4.10.1")
        implementation("net.kyori:adventure-text-serializer-gson:4.10.1") {
            exclude("com.google.code.gson", "gson") // Prevent shading into the jar
        }
        implementation("net.kyori:adventure-text-serializer-legacy:4.10.1")

        // Other
        implementation("com.github.ben-manes.caffeine:caffeine:3.1.5")
        implementation("org.apache.maven:maven-artifact:3.9.0")
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
        exclude(group = "net.wesjd", module = "anvilgui")
        exclude(group = "org.slf4j", module = "slf4j-api")
    }

    configurations.testImplementation {
        setExtendsFrom(listOf(configurations.compileOnly.get(), configurations.implementation.get()))
    }

    tasks {
        withType<Jar> {
            duplicatesStrategy = DuplicatesStrategy.WARN
        }

        compileKotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        compileJava {
            dependsOn(clean)
            options.encoding = "UTF-8"
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

        withType<JavaCompile>().configureEach {
            options.release.set(17)
        }
    }

    java {
        withSourcesJar()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

tasks {
    shadowJar {
        relocate("org.bstats", "com.willfp.eco.libs.bstats")
        relocate("redempt.crunch", "com.willfp.eco.libs.crunch")
        relocate("org.apache.commons.lang3", "com.willfp.eco.libs.lang3")
        relocate("org.apache.maven", "com.willfp.eco.libs.maven")
        relocate("org.checkerframework", "com.willfp.eco.libs.checkerframework")
        relocate("org.intellij", "com.willfp.eco.libs.intellij")
        relocate("org.jetbrains.annotations", "com.willfp.eco.libs.jetbrains.annotations")
        //relocate("org.jetbrains.exposed", "com.willfp.eco.libs.exposed")
        relocate("org.objenesis", "com.willfp.eco.libs.objenesis")
        relocate("org.reflections", "com.willfp.eco.libs.reflections")
        relocate("javassist", "com.willfp.eco.libs.javassist")
        relocate("javax.annotation", "com.willfp.eco.libs.annotation")
        relocate("com.google.errorprone", "com.willfp.eco.libs.errorprone")
        relocate("com.google.j2objc", "com.willfp.eco.libs.j2objc")
        relocate("com.google.thirdparty", "com.willfp.eco.libs.google.thirdparty")
        relocate("com.google.protobuf", "com.willfp.eco.libs.google.protobuf") // No I don't know either
        relocate("google.protobuf", "com.willfp.eco.libs.protobuf") // Still don't know
        relocate("com.zaxxer.hikari", "com.willfp.eco.libs.hikari")
        //relocate("com.mysql", "com.willfp.eco.libs.mysql")
        relocate("com.mongodb", "com.willfp.eco.libs.mongodb")
        relocate("org.bson", "com.willfp.eco.libs.bson")
        relocate("org.reactivestreams", "com.willfp.eco.libs.reactivestreams")
        relocate("reactor.", "com.willfp.eco.libs.reactor.") // Dot in name to be safe
        relocate("com.moandjiezana.toml", "com.willfp.eco.libs.toml")
        relocate("com.willfp.modelenginebridge", "com.willfp.eco.libs.modelenginebridge")

        /*
        Kotlin and caffeine are not shaded so that they can be accessed directly by eco plugins.
        Also, not relocating adventure, because it's a pain in the ass, and it doesn't *seem* to be causing loader constraint violations.
         */
    }
}

group = "com.willfp"
version = findProperty("version")!!
