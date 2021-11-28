plugins {
    id("io.papermc.paperweight.userdev") version "1.3.0-SNAPSHOT"
}

group = "com.willfp"
version = rootProject.version

dependencies {
    paperDevBundle("1.18-rc3-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.9.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.8.1")
}

tasks {
    build {
        dependsOn(reobfJar)
    }
}