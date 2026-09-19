import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:v26_2", configuration = "shadow"))
    paperweight.paperDevBundle("26.3.build.+")
}

tasks {
    shadowJar {
        relocate(
            "com.willfp.eco.internal.spigot.proxy.v26_2",
            "com.willfp.eco.internal.spigot.proxy.v26_3"
        )

        exclude("com/willfp/eco/internal/spigot/proxy/v26_2/CommonsInitializer*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v26_2/DatapackCodec*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v26_2/Hologram*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v26_2/hologram/V26_2HologramHandle*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v26_2/common/ai/entity/CatLieOnBedGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v26_2/common/ai/entity/TryFindWaterGoalFactory*.class")

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

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}
