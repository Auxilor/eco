plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:v1_21_6", configuration = "shadow"))
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    reobfJar {
        mustRunAfter(shadowJar)
    }

    shadowJar {
        relocate(
            "com.willfp.eco.internal.spigot.proxy.v1_21_6",
            "com.willfp.eco.internal.spigot.proxy.v1_21_10"
        )
    }
}
