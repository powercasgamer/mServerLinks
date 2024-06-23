import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.paper

plugins {
    id(libs.plugins.mizule.platform.paper.get().pluginId)
    id(libs.plugins.mizule.kotlin.get().pluginId)
    id(libs.plugins.mizule.gremlin.get().pluginId)
    alias(libs.plugins.hangar.publish)
}

dependencies {
    api(projects.mserverlinksCore)
    compileOnly(paper(libs.versions.minecraft.get() + "-R0.1-SNAPSHOT"))
    runtimeDownloadOnlyApi(kotlin("stdlib-jdk8"))
    runtimeDownloadOnlyApi(configurate("hocon", "4.2.0-SNAPSHOT"))
    runtimeDownloadOnlyApi(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
    runtimeDownloadOnlyApi("org.bstats:bstats-bukkit:3.0.2")
    implementation(libs.desertwell) {
        exclude("org.json")
    }
}

mizule {
    shadowOptions.enableShadow.set(true)
    archiveFileName = "mServerLinks-Paper-{version}.jar"
    enableCopyTask = true

    versions {
        kotlin.set("1.9.24")
        includeCommitHash.set(true)
//        buildCommitsSinceLatestTag.set(true)
    }
}

tasks {
    afterEvaluate {
        named("shadowJar", ShadowJar::class) {
            manifest { // Make this efault in gradle plugin
                attributes("paperweight-mappings-namespace" to "mojang")
            }
        }
    }
}

mizulePaperPlatform {
    this.version.set(libs.versions.minecraft.get())
    this.commonPlugins.set(false)
}

afterEvaluate {
    hangarPublish {
        publications.register("plugin") {
            version = project.version as String
            id = "mServerLinks"
            channel = if (project.version.toString().contains("SNAPSHOT")) "Beta" else "Release"
            changelog.set(project.rootProject.file("CHANGELOG.md").readText())
            platforms {
                paper {
                    jar.set(tasks.named("copyJar", dev.mizule.mizulebuildlogic.task.FileCopyTask::class).get().destination)
                    platformVersions = listOf("1.21")
                }
            }
        }
    }
}
