# How to contribute to eco

## Codestyle
1. The eco checkstyle is in /config/checkstyle.xml
- The pull request must not have any checkstyle issues.
- Every method and field must have a javadoc attached.

2. Use JetBrains annotations
- Every parameter should be annotated with @NotNull or @Nullable
- Use @NotNull over lombok @NonNull

3. Imports
- No group (*) imports.
- No static imports.

## Dependency Injection
- eco uses Dependency Injection
- Any calls to Eco#getHandler#getEcoPlugin are code smells and should never be used unless **absolutely necessary**.
- NamespacedKeys, FixedMetadataValues, Runnables, and Schedules should be managed using AbstractEcoPlugin through DI.
- Any DI class should extend PluginDependent where possible. If the class extends another, then you **must** store the plugin instance in a private final variable called **plugin** with a private or protected getter.

## Other
- All drops **must** be sent through a DropQueue - calls to World#dropItem will get your PR rejected.
- eco is built with java 17.