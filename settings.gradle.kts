pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
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
include(":eco-core:core-nms:nms-common")
include(":eco-core:core-nms:v1_17_R1")
include(":eco-core:core-nms:v1_18_R1")
include(":eco-core:core-nms:v1_18_R2")
include(":eco-core:core-nms:v1_19_R1")
include(":eco-core:core-nms:v1_19_R2")
include(":eco-core:core-nms:v1_19_R3")
include(":eco-core:core-nms:v1_20_R1")
include(":eco-core:core-nms:v1_20_R2")
include(":eco-core:core-nms:v1_20_R3")
include(":eco-core:core-nms:v1_20_6")
include(":eco-core:core-nms:v1_21")
include(":eco-core:core-proxy")
include(":eco-core:core-plugin")
include(":eco-core:core-backend")