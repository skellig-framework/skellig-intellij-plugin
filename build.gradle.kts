import dev.detekt.gradle.Detekt
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    java
    kotlin("jvm") version "2.4.20"
    id("org.jetbrains.intellij.platform") version "2.19.0"
    id("org.jetbrains.intellij.platform.grammarkit") version "2.19.0"
    id("dev.detekt") version "2.0.0-alpha.6"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = providers.gradleProperty("pluginGroup").get()
version = providers.gradleProperty("pluginVersion").get()

val generateLexer =
    tasks.named<org.jetbrains.intellij.platform.gradle.tasks.GenerateLexerTask>("generateLexer") {
        sourceFile.set(layout.projectDirectory.file("src/main/kotlin/org/skellig/plugin/language/teststep/SkelligTestStepLexer.flex"))
    }
val generateParser =
    tasks.named<org.jetbrains.intellij.platform.gradle.tasks.GenerateParserTask>("generateParser") {
        sourceFile.set(layout.projectDirectory.file("src/main/kotlin/org/skellig/plugin/language/teststep/SkelligTestStepParser.bnf"))
    }

sourceSets["main"].java.srcDirs(
    generateLexer.flatMap { it.targetRootOutputDir },
    generateParser.flatMap { it.targetRootOutputDir },
)

tasks.withType<BaseKtLintCheckTask>().configureEach {
    dependsOn(generateLexer, generateParser)
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        val localPlatformPath = providers.gradleProperty("localPlatformPath").orNull
        if (localPlatformPath != null) {
            local(localPlatformPath)
        } else {
            intellijIdea(providers.gradleProperty("platformVersion").get())
        }
        bundledPlugins("com.intellij.java", "org.jetbrains.kotlin")
        plugin("org.jetbrains.plugins.hocon", providers.gradleProperty("hoconPluginVersion").get())
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation(platform("org.junit:junit-bom:5.14.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // IntelliJ's ParsingTestCase still extends junit.framework.TestCase.
    testImplementation("junit:junit:4.13.2")
    detektPlugins("dev.detekt:detekt-rules-ktlint-wrapper:2.0.0-alpha.6")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(27))
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.test {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

kotlin {
    jvmToolchain(27)
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(generateLexer, generateParser)
    compilerOptions {
        // IntelliJ IDEA 2026.2 runs on JBR 25, even when the build uses JDK 27.
        jvmTarget.set(JvmTarget.JVM_25)
        freeCompilerArgs.add("-Xjdk-release=25")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(25)
}

intellijPlatform {
    projectName.set(providers.gradleProperty("pluginName_"))
    instrumentCode.set(true)

    pluginConfiguration {
        version.set(providers.gradleProperty("pluginVersion"))
        ideaVersion {
            sinceBuild.set(providers.gradleProperty("pluginSinceBuild"))
            untilBuild.set(providers.gradleProperty("pluginUntilBuild"))
        }
    }

    pluginVerification {
        ides {
            current()
        }
    }

    publishing {
        token.set(providers.environmentVariable("PUBLISH_SKELLIG_PLUGIN_TOKEN"))
        channels.set(
            providers.gradleProperty("pluginChannels").orElse("default").map { channels ->
                channels.split(',').map(String::trim).filter(String::isNotEmpty)
            },
        )
    }

    signing {
        certificateChain.set(providers.environmentVariable("CERTIFICATE_CHAIN"))
        privateKey.set(providers.environmentVariable("PRIVATE_KEY"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }
}

val prepareMarketplaceRelease =
    tasks.register("prepareMarketplaceRelease") {
        group = "intellij platform"
        description = "Builds and verifies the plugin ZIP for JetBrains Marketplace without uploading it."
        dependsOn("test", "ktlintCheck", "verifyPluginProjectConfiguration", "verifyPluginStructure", "verifyPlugin", "buildPlugin")
    }

tasks.named("publishPlugin") {
    dependsOn(prepareMarketplaceRelease)
}

detekt {
    config.setFrom(files("detekt-config.yml"))
    buildUponDefaultConfig = true
    ignoreFailures = true
}

tasks.withType<Detekt>().configureEach {
    jvmTarget.set("25")
    reports {
        html.required.set(false)
        checkstyle.required.set(false)
        sarif.required.set(false)
        markdown.required.set(false)
    }
}
