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
