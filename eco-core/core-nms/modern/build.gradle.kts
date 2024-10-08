plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly(project(":eco-core:core-nms:common"))
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release = 21
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "21"
        }
    }
}
