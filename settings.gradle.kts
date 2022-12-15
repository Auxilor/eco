pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
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
include(":eco-core:core-proxy")
include(":eco-core:core-plugin")
include(":eco-core:core-backend")