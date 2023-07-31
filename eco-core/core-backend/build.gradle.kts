group = "com.willfp"
version = rootProject.version

dependencies {
    // Libraries
    implementation("org.reflections:reflections:0.9.12")
    implementation("org.objenesis:objenesis:3.2")

    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("net.kyori:adventure-text-minimessage:4.10.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.1.0")
    compileOnly("org.yaml:snakeyaml:1.33")
    compileOnly("com.moandjiezana.toml:toml4j:0.7.2")
}
