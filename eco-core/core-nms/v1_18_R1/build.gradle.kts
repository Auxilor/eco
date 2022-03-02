plugins {
    id("io.papermc.paperweight.userdev") version "1.3.4"
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:nms-common"))
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
}

tasks {
    reobfJar {
        dependsOn(shadowJar)
    }

    build {
        dependsOn(reobfJar)
    }

    shadowJar {
        relocate(
            "com.willfp.internal.spigot.proxy.common",
            "com.willfp.internal.spigot.proxy.v1_18_R1"
        )
    }
}
