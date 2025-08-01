pluginManagement {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "eco"

// Core
include(":eco-api")
include(":eco-core")
include(":eco-core:core-nms")
include(":eco-core:core-nms:common")
include(":eco-core:core-nms:modern")
include(":eco-core:core-nms:v1_17_R1")
include(":eco-core:core-nms:v1_18_R1")
include(":eco-core:core-nms:v1_18_R2")
include(":eco-core:core-nms:v1_19_R1")
include(":eco-core:core-nms:v1_19_R2")
include(":eco-core:core-nms:v1_19_R3")
include(":eco-core:core-nms:v1_20_R1")
include(":eco-core:core-nms:v1_20_R2")
include(":eco-core:core-nms:v1_20_R3")
include(":eco-core:core-nms:v1_21")
include(":eco-core:core-nms:v1_21_3")
include(":eco-core:core-nms:v1_21_4")
include(":eco-core:core-nms:v1_21_5")
include(":eco-core:core-nms:v1_21_7")
include(":eco-core:core-proxy")
include(":eco-core:core-plugin")
include(":eco-core:core-backend")
include(":eco-core:core-backend-modern")
