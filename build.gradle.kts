import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

plugins {
    // Java support
    id("java")
    // Kotlin support
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    // gradle-intellij-plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.13.1"
    // detekt linter - read more: https://detekt.github.io/detekt/gradle.html
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
    // ktlint linter - read more: https://github.com/JLLeitschuh/ktlint-gradle
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

// Import variables from gradle.properties file
val pluginGroup: String by project
// `pluginName_` variable ends with `_` because of the collision with Kotlin magic getter in the `intellij` closure.
// Read more about the issue: https://github.com/JetBrains/intellij-platform-plugin-template/issues/29
val pluginName_: String by project
val pluginVersion: String by project
val pluginSinceBuild: String by project
val pluginUntilBuild: String by project
val pluginVerifierIdeVersions: String by project

val platformType: String by project
val platformVersion: String by project
val platformPlugins: String by project
val platformDownloadSources: String by project

group = pluginGroup
version = pluginVersion

sourceSets["main"].java.srcDirs("src/main/gen")

// Configure project's dependencies
repositories {
    mavenCentral()
    jcenter()
    maven {
        url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        url = URI("https://dl.bintray.com/jetbrains/intellij-plugin-service")
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.15.0")
}

// Configure gradle-intellij-plugin plugin.
// Read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(pluginName_)
    version.set(platformVersion)
}

// Configure detekt plugin.
// Read more: https://detekt.github.io/detekt/kotlindsl.html
detekt {
    config = files("./detekt-config.yml")
    buildUponDefaultConfig = true
    ignoreFailures = true

    reports {
        html.enabled = false
        xml.enabled = false
        txt.enabled = false
    }
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Detekt> {
        jvmTarget = "11"
    }

    patchPluginXml {
        version.set(pluginVersion)
        sinceBuild.set("203")
        untilBuild.set("232.*")

        changeNotes.set("""
            <b>Skellig Framework Plugin (BETA)</b>
            <br/>
            Provides integration with Skellig test steps (.sts) and feature files (.skellig):
            <br/>
        <ul> 
            <li> References to test steps from Skellig feature files</li>
            <li> Code format</li>
            <li> Syntax highlighter with colour customization</li>
            <li> Syntax check for .sts files</li>
            <li> Expand/Collapse blocks of code in .sts files</li>
            <li> Comment code</li>
        </ul> 
        """.trimIndent())
    }

    runPluginVerifier {
        ideVersions.set(listOf(pluginVerifierIdeVersions))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
