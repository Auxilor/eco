group = "com.willfp"
version = rootProject.version

subprojects {
    dependencies {
        compileOnly(project(":eco-core:core-proxy"))
        compileOnly(project(":eco-core:core-plugin"))
        compileOnly(project(":eco-core:core-backend"))
        // libraries.minecraft.net machine broke
        compileOnly("com.github.Mojang:brigadier:1.0.18")
    }
}