import dev.mizule.mizulebuildlogic.util.adventure
import dev.mizule.mizulebuildlogic.util.applyJarMetadata
import dev.mizule.mizulebuildlogic.util.configurate

plugins {
  id(libs.plugins.mizule.blossom.get().pluginId)
  id(libs.plugins.mizule.kotlin.get().pluginId)
}

dependencies {
  api(projects.mserverlinksApi)
  api(libs.event.api) // compileOnlyApi
  api(libs.eventchain.kyori) // compileOnlyApi
  api(libs.caffeine) // compileOnlyApi
  api(libs.guava) // compileOnlyApi
  compileOnly(configurate("hocon", "4.2.0-SNAPSHOT"))
  compileOnly(configurate("yaml", "4.2.0-SNAPSHOT"))
  compileOnly(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
  compileOnly(adventure("api", "4.17.0"))
  compileOnly(libs.slf4j)
  compileOnly(libs.gson)
}

mizule {
  shadowOptions.enableShadow.set(true)
  enableCopyTask = false
}

applyJarMetadata("mserverlinks-core")
