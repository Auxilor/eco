plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:nms-common"))
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-text-minimessage:4.11.0") {
        version {
            strictly("4.11.0")
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
            "com.willfp.eco.internal.spigot.proxy.v1_21.common"
        )
        relocate(
            "net.kyori.adventure.text.minimessage",
            "com.willfp.eco.internal.spigot.proxy.v1_21.minimessage"
        )
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        withSourcesJar()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}
