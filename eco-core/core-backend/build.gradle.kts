import org.jetbrains.kotlin.gradle.dsl.JvmTarget

group = "com.willfp"
version = rootProject.version

dependencies {
    // Libraries
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.objenesis:objenesis:3.2")

    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.kyori:adventure-text-minimessage:4.10.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.0")
    compileOnly("org.yaml:snakeyaml:1.33")
    compileOnly("com.moandjiezana.toml:toml4j:0.7.2")
}

tasks {
    compileJava {
        options.release.set(17)
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
}
