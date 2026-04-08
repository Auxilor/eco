import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:common"))
    implementation(project(":eco-core:core-nms:v1_21_8", configuration = "shadow"))
    paperweight.paperDevBundle("26.1.1.build.+")
}

tasks {
    shadowJar {
        relocate(
            "com.willfp.eco.internal.spigot.proxy.v1_21_8",
            "com.willfp.eco.internal.spigot.proxy.v26_1_1"
        )
        relocate(
            "com.willfp.eco.internal.spigot.proxy.common",
            "com.willfp.eco.internal.spigot.proxy.v26_1_1.common"
        )

        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/PlayerHandler*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/TPS*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/CommonsInitializer*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/SNBTConverter*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/packet/PacketContainerClick*.class")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    compileJava {
        options.release.set(25)
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_25)
        }
    }
}
