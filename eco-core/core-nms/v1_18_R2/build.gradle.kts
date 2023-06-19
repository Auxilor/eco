plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:nms-common"))
    paperweight.paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-text-minimessage:4.14.0") {
        version {
            strictly("4.10.0")
        }
        exclude(group = "net.kyori", module = "adventure-api")
    }
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
            "com.willfp.eco.internal.spigot.proxy.common",
            "com.willfp.eco.internal.spigot.proxy.v1_18_R2.common"
        )
        relocate(
            "net.kyori.adventure.text.minimessage",
            "com.willfp.eco.internal.spigot.proxy.v1_18_R2.minimessage"
        )
    }
}
