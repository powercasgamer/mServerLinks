pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.stellardrift.ca/repository/snapshots/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://repo.mizule.dev/testing") // remove
        maven("https://repo.jpenilla.xyz/snapshots")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "mserverlinks-parent"

sequenceOf(
    "core",
    "paper",
).forEach {
    include("mserverlinks-$it")
    project(":mserverlinks-$it").projectDir = file(it)
}
