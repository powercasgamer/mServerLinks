import dev.mizule.mizulebuildlogic.util.adventure
import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.paper

plugins {
  id(libs.plugins.mizule.kotlin.get().pluginId)
}

dependencies {
  api(projects.mserverlinksCore)
  compileOnly(paper(libs.versions.minecraft.get()))
  compileOnly(kotlin("stdlib-jdk8"))
  compileOnly(libs.placeholderapi)
  compileOnly(libs.miniplaceholders)
  compileOnly(libs.bstats.bukkit)
  compileOnly(adventure("text-minimessage", libs.versions.adventure))
  compileOnly(adventure("text-serializer-legacy", libs.versions.adventure))
  compileOnly(configurate("core", libs.versions.configurate))
}

mizule {
  archiveFileName = "mServerLinks-Bukkit-{version}.jar"
  enableCopyTask = false

  shadowOptions {
    enableShadow.set(true)
    relocations.set(
      listOf(
        "org.spongepowered.configurate",
        "io.leangen.geantyref"
      )
    )
  }

  versions {
    includeCommitHash.set(true)
  }
}
