plugins {
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

group = "com.willfp"
version = rootProject.version

dependencies {
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}