import dev.mizule.mizulebuildlogic.MizuleExtension
import dev.mizule.mizulebuildlogic.publish.MizulePublishExtension
import dev.mizule.mizulebuildlogic.util.configureIndraJavaVersion

plugins {
    alias(libs.plugins.mizule.root)
    alias(libs.plugins.mizule.publishing) apply false
    alias(libs.plugins.mizule.spotless) apply false
    alias(libs.plugins.mizule.platform.paper) apply false
    alias(libs.plugins.mizule.kotlin) apply false
    alias(libs.plugins.mizule.gremlin) apply false
    alias(libs.plugins.mizule.blossom) apply false

}

subprojects {
    plugins.apply("dev.mizule.mizule-build-logic")
    plugins.apply("dev.mizule.mizule-build-logic.publishing")
    plugins.apply("dev.mizule.mizule-build-logic.spotless")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://maven.mizule.dev/")
        maven("https://jitpack.io") {
            mavenContent {
                includeGroup("net.william278")
            }
        }
    }

    configureIndraJavaVersion(21)

    extensions.configure(MizulePublishExtension::class) {
        this.applyJavadocLinker = false // Doesn't like compileOnlyApi dependencies
    }

    extensions.configure(MizuleExtension::class.java) {
        versions {
            this.kotlin = "1.9.24"
        }
    }

}
