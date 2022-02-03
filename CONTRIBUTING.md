# How to contribute to eco

## Codestyle

1. The eco checkstyle is in /config/checkstyle.xml

- The pull request must not have any checkstyle issues.
- Every method and field must have a javadoc attached.

2. Use JetBrains annotations

- Every parameter should be annotated with @NotNull or @Nullable

3. Imports

- No group (*) imports.
- No static imports.

4. Kotlin

- Kotlin should be the only language used in the backend, java should be the only language used in the frontend.
- Kotlin API extensions should only be for creating extension functions and extra niceties that aren't possible in java.
  Do not write API components in kotlin.
- Kotlin code should never be called directly from the frontend Java API. Kotlin API extensions should always rely on
  java, not the other way round.

## Dependency Injection

- eco uses Dependency Injection
- Any calls to Eco#getHandler#getEcoPlugin are code smells and should never be used unless **absolutely necessary**.
- NamespacedKeys, FixedMetadataValues, Runnables, and Schedules should be managed using AbstractEcoPlugin through DI.
- Any DI class should extend PluginDependent where possible. If the class extends another, then you **must** store the
  plugin instance in a private final variable called **plugin** with a private or protected getter.

## Other

- All drops **must** be sent through a DropQueue - calls to World#dropItem will get your PR rejected.
- eco is built with java 17.