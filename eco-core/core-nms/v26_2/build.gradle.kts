import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:v26_1_2", configuration = "shadow"))
    paperweight.paperDevBundle("26.2.build.+")
}

tasks {
    shadowJar {
        relocate(
            "com.willfp.eco.internal.spigot.proxy.v26_1_2",
            "com.willfp.eco.internal.spigot.proxy.v26_2"
        )

        exclude("com/willfp/eco/internal/spigot/proxy/v26_1_2/common/recipes/RecipeManager*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v26_1_2/SNBTConverter*.class")

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
