plugins {
    id("io.papermc.paperweight.userdev") version "1.1.14"
    id("xyz.jpenilla.run-paper") version "1.0.4"
}

group = "com.willfp"
version = rootProject.version

dependencies {
    paperDevBundle("1.17.1-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.9.1")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.8.1")
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = "UTF_8"
        options.release.set(16)
    }

    javadoc {
        options.encoding = "UTF_8"
    }

    processResources {
        filteringCharset = "UTF_8"
    }
}
