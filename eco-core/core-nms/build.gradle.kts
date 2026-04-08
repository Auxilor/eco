plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-SNAPSHOT" apply false
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
}
