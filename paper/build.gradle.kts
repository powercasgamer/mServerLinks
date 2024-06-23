import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.paper

plugins {
    id(libs.plugins.mizule.platform.paper.get().pluginId)
    id(libs.plugins.mizule.kotlin.get().pluginId)
    id(libs.plugins.mizule.gremlin.get().pluginId)
}

dependencies {
    api(projects.mserverlinksCore)
    compileOnly(paper(libs.versions.minecraft.get() + "-R0.1-SNAPSHOT"))
    runtimeDownloadApi(kotlin("stdlib-jdk8"))
    runtimeDownloadApi(configurate("hocon", "4.2.0-SNAPSHOT"))
    runtimeDownloadApi(configurate("extra-kotlin", "4.2.0-SNAPSHOT"))
}

mizule {
    shadowOptions.enableShadow.set(true)
    archiveFileName = "mServerLinks-Paper.jar"
    enableCopyTask = true
}

mizulePaperPlatform {
    this.version.set(libs.versions.minecraft.get())
    this.commonPlugins.set(false)
}
