pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "eco"

// Core
include(":eco-api")
include(":eco-core")
include(":eco-core:core-nms")
include(":eco-core:core-nms:common")
include(":eco-core:core-nms:modern")
include(":eco-core:core-nms:v1_21_4")
include(":eco-core:core-nms:v1_21_5")
include(":eco-core:core-nms:v1_21_6")
include(":eco-core:core-nms:v1_21_7")
include(":eco-core:core-nms:v1_21_8")
include(":eco-core:core-nms:v1_21_10")
include(":eco-core:core-nms:v1_21_11")
include(":eco-core:core-plugin")
include(":eco-core:core-backend")
