import org.jetbrains.kotlin.gradle.dsl.JvmTarget

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.21")
    }
}

plugins {
    id("java-library")
    id("com.gradleup.shadow") version "9.4.1"
    id("maven-publish")
    id("java")
    kotlin("jvm") version "2.3.21"
}

dependencies {
    implementation(project(":eco-api"))
    implementation(project(path = ":eco-core:core-plugin", configuration = "shadow"))
    implementation(project(":eco-core:core-backend"))
    implementation(project(path = ":eco-core:core-nms:v1_21_8", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_21_10", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v1_21_11", configuration = "reobf"))
    implementation(project(path = ":eco-core:core-nms:v26_1_2", configuration = "shadow"))
    implementation(project(path = ":eco-core:core-nms:v26_2", configuration = "shadow"))
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
            content {
                includeGroupByRegex("com\\.github\\..*")
                excludeGroup("com.github.TownyAdvanced")
            }
        }

        // Paper
        maven("https://repo.papermc.io/repository/maven-public/")

        // EssentialsX
        maven("https://repo.essentialsx.net/releases")

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

        // MythicMobs
        maven("https://mvn.lumine.io/repository/maven-public/")

        // LibsDisguises
        maven("https://mvn.lib.co.nz/public")

        // PlayerPoints
        maven("https://repo.rosewooddev.io/repository/public/")

        // Denizen
        maven("https://maven.citizensnpcs.co/repo")

        // IridiumSkyblock
        maven("https://nexus.iridiumdevelopment.net/repository/maven-releases/")

        // HuskPlugins
        maven("https://repo.william278.net/releases")

        // FancyHolograms
        maven("https://repo.fancyinnovations.com/releases")

        // Nexo
        maven("https://repo.nexomc.com/releases")

        // CraftEngine
        maven("https://repo.momirealms.net/releases/")

        // ExcellentEconomy and ExcellentShop
        maven("https://repo.nightexpressdev.com/releases")

        //Towny
        maven("https://repo.glaremasters.me/repository/towny/")

        // FactionsUUID
        exclusiveContent {
            forRepository {
                maven("https://dependency.download/releases")
            }

            filter {
                includeGroup("dev.kitteh")
            }
        }
    }

    dependencies {
        // Kotlin
        implementation(kotlin("stdlib", version = "2.3.21"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

        // Included in spigot jar, no need to move to implementation
        compileOnly("org.jetbrains:annotations:26.1.0")
        compileOnly("com.google.guava:guava:33.6.0-jre")

        // Test
        testImplementation("org.junit.jupiter:junit-jupiter-api:6.0.3")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:6.0.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher:2.0.3")
        testImplementation("io.mockk:mockk-jvm:1.13.17")

        // Adventure (provided at runtime via Paper library loader)
        compileOnly("net.kyori:adventure-api:5.0.1") {
            exclude("com.github.ben-manes.caffeine", "caffeine")
        }
        compileOnly("net.kyori:adventure-text-serializer-gson:5.0.1") {
            exclude("com.google.code.gson", "gson")
        }
        compileOnly("net.kyori:adventure-text-serializer-legacy:5.0.1")

        // Other
        compileOnly("com.github.ben-manes.caffeine:caffeine:3.2.3")
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
        compileKotlin {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_21)
            }
        }

        compileJava {
            dependsOn(clean)
            options.encoding = "UTF-8"
            options.isDeprecation = true
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
            options.release.set(21)
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
        exclude("META-INF/**")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        relocate("org.apache.commons.lang3", "com.willfp.eco.libs.lang3")
relocate("org.intellij", "com.willfp.eco.libs.intellij")
        relocate("org.jetbrains.annotations", "com.willfp.eco.libs.jetbrains.annotations")
        relocate("com.willfp.modelenginebridge", "com.willfp.eco.libs.modelenginebridge")

        relocate("kotlin", "com.willfp.eco.libs.kotlin") {
            exclude("kotlin.kotlin_builtins")
        }

        /*
        Caffeine is not shaded so that it can be accessed directly by eco plugins.
        Also, not relocating adventure, because it's a pain in the ass, and it doesn't *seem* to be causing loader constraint violations.
         */
    }
}

group = "com.willfp"
version = findProperty("version")!!

java {
    withJavadocJar()
}

publishing {
    publications {
        // maven-private: only the shaded 'all' jar
        create<MavenPublication>("private") {
            artifactId = rootProject.name
            artifact(tasks.named("shadowJar"))
        }
        // maven-releases + GitHub: full set (none, all, sources, javadoc)
        create<MavenPublication>("release") {
            artifactId = rootProject.name
            from(components["java"])
            artifact(tasks.named("shadowJar"))
        }
    }
    repositories {
        maven {
            name = "AuxilorPrivate"
            url = uri("https://repo.auxilor.io/repository/maven-private/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        maven {
            name = "AuxilorReleases"
            url = uri("https://repo.auxilor.io/repository/maven-releases/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Auxilor/eco")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

// POM generation must run after clean (compileJava dependsOn clean)
tasks.matching { it.name.startsWith("generatePomFileFor") }.configureEach {
    mustRunAfter(tasks.named("clean"))
}

tasks.register("publishToAuxilor") {
    dependsOn(
        // Root plugin
        "publishPrivatePublicationToAuxilorPrivateRepository",
        "publishReleasePublicationToAuxilorReleasesRepository",
        "publishReleasePublicationToGitHubPackagesRepository",
        // eco-api
        ":eco-api:publishShadowPublicationToAuxilorRepository",
        ":eco-api:publishShadowPublicationToGitHubPackagesRepository",
    )
}
