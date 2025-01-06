import dev.mizule.mizulebuildlogic.util.applyJarMetadata
import dev.mizule.mizulebuildlogic.util.cloud
import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.versionString
import me.modmuss50.mpp.ReleaseType

plugins {
  id(libs.plugins.mizule.platform.velocity.get().pluginId)
  id(libs.plugins.mizule.kotlin.get().pluginId)
  id(libs.plugins.mizule.gremlin.get().pluginId)
  alias(libs.plugins.hangar.publish)
  alias(libs.plugins.mod.publish)
}

dependencies {
  api(projects.mserverlinksCore)
  compileOnly("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
  annotationProcessor("com.velocitypowered:velocity-api:3.4.0-SNAPSHOT")
  runtimeDownloadOnlyApi(kotlin("stdlib-jdk8"))
  runtimeDownloadOnlyApi(configurate("hocon", "4.2.0-SNAPSHOT"))
  runtimeDownloadOnlyApi(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
  runtimeDownloadOnlyApi(cloud("velocity", "2.0.0-beta.8"))
  implementation(libs.bstats.velocity)
  compileOnly(libs.miniplaceholders)
}

mizule {
  archiveFileName = "mServerLinks-Velocity-{version}.jar"
  enableCopyTask = true

  shadowOptions {
    enableShadow.set(true)
    relocations.set(
      listOf(
        "org.bstats",
        "org.spongepowered.configurate",
        "org.incendo",
        "io.leangen.geantyref",
      )
    )
  }

  versions {
    includeCommitHash.set(true)
  }
}

applyJarMetadata("mserverlinks-velocity")

mizuleVelocityPlatform {
  this.version.set("3.4.0-SNAPSHOT")
  this.commonPlugins.set(true)
}

afterEvaluate {
  hangarPublish {
    publications.register("plugin") {
      version = project.version as String
      id = "mServerLinks"
      channel = if (project.version.toString().contains("SNAPSHOT")) "Beta" else "Release"
      changelog.set(project.rootProject.file("CHANGELOG.md").readText())
      platforms {
        velocity {
          jar.set(tasks.named("copyJar", dev.mizule.mizulebuildlogic.task.FileCopyTask::class).get().destination)
          platformVersions = listOf("3.3", "3.4")
        }
      }
    }
  }

  publishMods {
    changelog.set(project.rootProject.file("CHANGELOG.md").readText())
    file.set(tasks.named("copyJar", dev.mizule.mizulebuildlogic.task.FileCopyTask::class).get().destination)
    type = if (project.version.toString().contains("SNAPSHOT")) ReleaseType.BETA else ReleaseType.STABLE
    modLoaders.add("velocity")

    modrinth {
      accessToken = providers.environmentVariable("MODRINTH_API_KEY")
      projectId = "5E2WANwL"
      minecraftVersions.add("1.21")
    }

    discord {
      webhookUrl = providers.environmentVariable("MIZULE_DISCORD_WEBHOOK_UPDATES")
      dryRunWebhookUrl = providers.environmentVariable("MIZULE_DISCORD_WEBHOOK_UPDATES")
      username.set("Mizule Updates")
      content = "# ${project.versionString()} of mServerLinks has been released!"
    }
  }
}
