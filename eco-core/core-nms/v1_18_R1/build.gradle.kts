plugins {
    id("io.papermc.paperweight.userdev") version "1.3.0-SNAPSHOT"
}

group = "com.willfp"
version = rootProject.version

dependencies {
    paperDevBundle("1.18-rc3-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}