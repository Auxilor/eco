plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:common"))
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

    testRuntimeOnly("net.kyori:adventure-platform-bukkit:4.4.1")
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    reobfJar {
        mustRunAfter(shadowJar)
    }

    test {
        workingDir = layout.buildDirectory.dir("test-run").get().asFile
        doFirst { workingDir.mkdirs() }
    }

    shadowJar {
        relocate(
            "com.willfp.eco.internal.spigot.proxy.common",
            "com.willfp.eco.internal.spigot.proxy.v1_21_8.common"
        )
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
