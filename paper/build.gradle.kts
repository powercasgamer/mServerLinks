import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.mizule.mizulebuildlogic.util.applyJarMetadata
import dev.mizule.mizulebuildlogic.util.cloud
import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.paper
import dev.mizule.mizulebuildlogic.util.versionString
import me.modmuss50.mpp.ReleaseType

plugins {
  id(libs.plugins.mizule.platform.paper.get().pluginId)
  id(libs.plugins.mizule.kotlin.get().pluginId)
  id(libs.plugins.mizule.gremlin.get().pluginId)
  alias(libs.plugins.hangar.publish)
  alias(libs.plugins.mod.publish)
}

dependencies {
  api(projects.mserverlinksBukkit)
  compileOnly(paper(libs.versions.minecraft.get()))
  runtimeDownloadOnlyApi(kotlin("stdlib-jdk8"))
  runtimeDownloadOnlyApi(configurate("hocon", "4.2.0-SNAPSHOT"))
  runtimeDownloadOnlyApi(configurate("yaml", "4.2.0-SNAPSHOT"))
  runtimeDownloadOnlyApi(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
  runtimeDownloadOnlyApi(cloud("paper", "2.0.0-SNAPSHOT"))
  runtimeDownloadOnlyApi("org.bstats:bstats-bukkit:3.1.0")
  implementation(libs.papertrail)
  implementation(libs.desertwell) {
    exclude("org.json")
  }
}

mizule {
  archiveFileName = "mServerLinks-Paper-{version}.jar"
  enableCopyTask = true

  shadowOptions {
    enableShadow.set(true)
    relocations.set(
      listOf(
        "org.bstats",
        "org.spongepowered.configurate",
        "org.incendo",
        "net.william278.desertwell",
        "io.leangen.geantyref",
        "io.papermc.papertrail",
      )
    )
  }

  versions {
    includeCommitHash.set(true)
  }
}

tasks {
  afterEvaluate {
    named("shadowJar", ShadowJar::class) {
      manifest {
        attributes("paperweight-mappings-namespace" to "mojang")
      }
    }
  }
}

applyJarMetadata("mserverlinks-paper")

mizulePaperPlatform {
  this.version.set(libs.versions.minecraft.get())
  this.commonPlugins.set(false)
  this.extraPlugins.add(
    "https://ci.extendedclip.com/job/PlaceholderAPI/lastSuccessfulBuild/artifact/build/libs/PlaceholderAPI-2.11.7-DEV-208.jar"
  )
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

  publishMods {
    changelog.set(project.rootProject.file("CHANGELOG.md").readText())
    file.set(tasks.named("copyJar", dev.mizule.mizulebuildlogic.task.FileCopyTask::class).get().destination)
    type = if (project.version.toString().contains("SNAPSHOT")) ReleaseType.BETA else ReleaseType.STABLE
    modLoaders.add("paper")

    modrinth {
      accessToken = providers.environmentVariable("MODRINTH_API_KEY")
      projectId = "5E2WANwL"
      minecraftVersions.addAll("1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6")
    }

    discord {
      webhookUrl = providers.environmentVariable("MIZULE_DISCORD_WEBHOOK_UPDATES")
      dryRunWebhookUrl = providers.environmentVariable("MIZULE_DISCORD_WEBHOOK_UPDATES")
      username.set("Mizule Updates")
      content = "# ${project.versionString()} of mServerLinks has been released!"
    }
  }
}
