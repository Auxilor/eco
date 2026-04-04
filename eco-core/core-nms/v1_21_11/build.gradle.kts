plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:common"))
    implementation(project(":eco-core:core-nms:modern"))
    implementation(project(":eco-core:core-nms:v1_21_6", configuration = "shadow"))
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
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
            "com.willfp.eco.internal.spigot.proxy.v1_21_11"
        )
        relocate(
            "com.willfp.eco.internal.spigot.proxy.common",
            "com.willfp.eco.internal.spigot.proxy.v1_21_11.common"
        )

        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_6/PlayerHandler*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_6/TPS*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_6/CommonsInitializer*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_6/SNBTConverter*.class")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
