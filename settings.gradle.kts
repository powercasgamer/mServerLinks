pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://repo.stellardrift.ca/repository/snapshots/")
    maven("https://central.sonatype.com/repository/maven-snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.mizule.dev/testing")
    maven("https://repo.mizule.dev/releases")
    maven("https://repo.mizule.dev/snapshots")
    maven("https://repo.jpenilla.xyz/snapshots")
  }
}

plugins {
  id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "mserverlinks-parent"

sequenceOf(
    "core",
    "bukkit",
//    "spigot",
    "paper",
    "velocity",
).forEach {
  include("mserverlinks-$it")
  project(":mserverlinks-$it").projectDir = file(it)
}
