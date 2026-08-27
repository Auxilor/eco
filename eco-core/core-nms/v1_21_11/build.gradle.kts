plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    implementation(project(":eco-core:core-nms:common"))
    implementation(project(":eco-core:core-nms:v1_21_8", configuration = "shadow"))
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
            "com.willfp.eco.internal.spigot.proxy.v1_21_8",
            "com.willfp.eco.internal.spigot.proxy.v1_21_11"
        )
        relocate(
            "com.willfp.eco.internal.spigot.proxy.common",
            "com.willfp.eco.internal.spigot.proxy.v1_21_11.common"
        )

        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/PlayerHandler*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/TPS*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/CommonsInitializer*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/SNBTConverter*.class")

        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/Hologram*.class")

        // 1.21.11 moved the entity classes these goals use.
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/common/ai/entity/CatLieOnBedGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/common/ai/entity/CatSitOnBedGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/common/ai/entity/IllusionerBlindnessSpellGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/common/ai/entity/IllusionerMirrorSpellGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/v1_21_8/common/ai/target/DefendVillageGoalFactory*.class")

        exclude("com/willfp/eco/internal/spigot/proxy/common/ai/entity/CatLieOnBedGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/common/ai/entity/CatSitOnBedGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/common/ai/entity/IllusionerBlindnessSpellGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/common/ai/entity/IllusionerMirrorSpellGoalFactory*.class")
        exclude("com/willfp/eco/internal/spigot/proxy/common/ai/target/DefendVillageGoalFactory*.class")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
