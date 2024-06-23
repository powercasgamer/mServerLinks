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
    runtimeDownloadApi(kotlin("stdlib-jdk8"))
    runtimeDownloadApi(configurate("hocon", "4.2.0-SNAPSHOT"))
    runtimeDownloadApi(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
    runtimeDownloadApi("org.bstats:bstats-bukkit:3.0.2")
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

mizulePaperPlatform {
    this.version.set(libs.versions.minecraft.get())
    this.commonPlugins.set(false)
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String
        id = "mServerLinks"
        channel = if (project.version.toString().contains("SNAPSHOT")) "Beta" else "Release"
        changelog.set(project.rootProject.file("CHANGELOG.md").readText())
        platforms {
            paper {
                jar = mizule.archiveFile
                platformVersions = listOf("1.21")
            }
        }
    }
}
