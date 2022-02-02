pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "eco"

// Core
include(":eco-api")
include(":eco-api:api-java")
include(":eco-api:api-kotlin")
include(":eco-core")
include(":eco-core:core-nms")
include(":eco-core:core-nms:v1_16_R3")
include(":eco-core:core-nms:v1_17_R1")
include(":eco-core:core-nms:v1_18_R1")
include(":eco-core:core-proxy")
include(":eco-core:core-plugin")
include(":eco-core:core-backend")