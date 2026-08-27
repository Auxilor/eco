plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
}


group = "com.willfp"
version = rootProject.version

subprojects {
    dependencies {
        compileOnly(project(":eco-core:core-plugin"))
        compileOnly(project(":eco-core:core-backend"))
        // libraries.minecraft.net machine broke
        compileOnly("com.github.Mojang:brigadier:1.0.18")
    }

    // Keep reobf tooling consistent across all NMS modules.
    pluginManager.withPlugin("io.papermc.paperweight.userdev") {
        dependencies {
            add("pluginRemapper", "net.fabricmc:tiny-remapper:0.13.1")
        }
    }

    // NMS modules are compiled once and relocated into jars for newer versions, so a member that
    // Mojang or Paper removed or made less visible only blows up at runtime. Catch it at build time.
    pluginManager.withPlugin("io.papermc.paperweight.userdev") {
        val compileClasspath = configurations.named("compileClasspath")

        val checkNmsLinkage = tasks.register<com.willfp.eco.gradle.NmsLinkageCheckTask>("checkNmsLinkage") {
            group = "verification"
            description = "Verifies every platform member used by the shaded jar exists on this version."

            jar.set(tasks.named<Jar>("shadowJar").flatMap { it.archiveFile })
            classpath.from(compileClasspath)
            report.set(layout.buildDirectory.file("reports/nms-linkage.txt"))
        }

        tasks.named("check") {
            dependsOn(checkNmsLinkage)
        }
    }
}
