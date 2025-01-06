import dev.mizule.mizulebuildlogic.util.adventure
import dev.mizule.mizulebuildlogic.util.applyJarMetadata

plugins {
  id(libs.plugins.mizule.blossom.get().pluginId)
}

dependencies {
  compileOnly(adventure("api", "4.17.0"))
  compileOnly(libs.slf4j)
  api(libs.event.api)
}

mizule {
  shadowOptions.enableShadow.set(false)
  enableCopyTask = false
}

applyJarMetadata("mserverlinks-api")
