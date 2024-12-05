import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "com.willfp"
version = rootProject.version

dependencies {
    compileOnly(project(":eco-core:core-backend"))
    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.release.set(21)
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}
