plugins {
    id("io.papermc.paperweight.userdev")
}

group = "com.willfp"
version = rootProject.version

dependencies {
    paperweight.paperDevBundle("1.17.1-R0.1-SNAPSHOT")
    pluginRemapper("net.fabricmc:tiny-remapper:0.10.3:fat")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
