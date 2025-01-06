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
  compileOnly(adventure("text-minimessage", "4.18.0"))
  compileOnly(adventure("text-serializer-legacy", "4.18.0"))
  compileOnly(configurate("core", "4.2.0-SNAPSHOT"))
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
