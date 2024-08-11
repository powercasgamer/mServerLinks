import dev.mizule.mizulebuildlogic.util.adventure
import dev.mizule.mizulebuildlogic.util.applyJarMetadata
import dev.mizule.mizulebuildlogic.util.configurate

plugins {
    id(libs.plugins.mizule.blossom.get().pluginId)
    id(libs.plugins.mizule.kotlin.get().pluginId)
}

dependencies {
    compileOnly(configurate("hocon", "4.2.0-SNAPSHOT"))
    compileOnly(configurate("yaml", "4.2.0-SNAPSHOT"))
    compileOnly(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
    compileOnly(adventure("api", "4.17.0"))
    compileOnly(libs.slf4j)
}

mizule {
    shadowOptions.enableShadow.set(true)
    enableCopyTask = false
}

applyJarMetadata("mserverlinks-core")
