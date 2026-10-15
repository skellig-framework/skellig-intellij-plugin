<!-- Plugin description -->
**Skellig Framework Plugin (BETA)** 

Provides integration with Skellig test steps (.sts) and feature files (.skellig):
* References to test steps from Skellig feature files
* Code format
* Syntax highlighter with colour customization
* Syntax check for .sts files
* Expand/Collapse blocks of code in .sts files
* Comment code

<!-- Plugin description end -->

Build with JDK 27 and the included Gradle 9.8.1 wrapper:

The build automatically generates parser/PSI classes from `SkelligTestStepParser.bnf`
and the lexer from `SkelligTestStepLexer.flex`. Generated sources live under
`build/generated/sources` and are regenerated after `clean`.

```shell
./gradlew build
./gradlew buildPlugin
./gradlew runIde
```

The build targets IntelliJ IDEA 2026.2.3, Kotlin 2.4.20, and HOCON 2026.2.0.
It emits Java 25 bytecode to match the IDE's bundled JetBrains Runtime.
The plugin archive is written to `build/distributions`.

To use an existing IDE installation instead of downloading it:

```shell
./gradlew buildPlugin -PlocalPlatformPath="/Applications/IntelliJ IDEA.app"
```

Run `./gradlew verifyPlugin` to check binary compatibility with the target IDE.

## Publishing to JetBrains Marketplace

Use JDK 27. Set `pluginVersion` in `gradle.properties` to an unused release version;
`platformVersion` controls the IDE used to build the plugin and is independent.
The plugin ID remains `org.skellig.plugin.language` so uploads update the existing listing.
The minimum supported IDE build remains `262.10968.63`. The upper compatibility
limit is unset following JetBrains' recommendation for current platforms; set
`pluginUntilBuild` explicitly if a future release needs a limit.

Prepare the ZIP and run unit tests, style checks, descriptor validation, and binary
compatibility verification without uploading anything:

```shell
./gradlew prepareMarketplaceRelease
```

Create a Marketplace token in your JetBrains account and provide it as the
`PUBLISH_TOKEN` environment variable in your terminal or IntelliJ Gradle run configuration.
Keep the token out of project files and version control.

```shell
./gradlew publishPlugin
```

Publishing runs the same release checks before uploading to the stable `default`
channel. To upload to a custom channel:

```shell
./gradlew publishPlugin -PpluginChannels=beta
```

You can append `-PlocalPlatformPath="/Applications/IntelliJ IDEA.app/Contents"` to
these commands to build and verify against your installed macOS IDE.
The verification tools may need to be downloaded on the first run.

Optional author signing is configured through `CERTIFICATE_CHAIN`, `PRIVATE_KEY`,
and `PRIVATE_KEY_PASSWORD` environment variables. The first two contain the PEM
certificate chain and private key. When supplied, Gradle signs the ZIP before uploading;
otherwise signing is skipped. See [JetBrains plugin signing documentation](https://plugins.jetbrains.com/docs/intellij/plugin-signing.html).

For a plugin's first Marketplace publication, upload the ZIP from `build/distributions`
manually to create the listing. Later versions can use `publishPlugin`.
See [JetBrains publishing documentation](https://plugins.jetbrains.com/docs/intellij/publishing-plugin.html).
