import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.paper

plugins {
    id(libs.plugins.mizule.kotlin.get().pluginId)
}

dependencies {
    api(projects.mserverlinksCore)
    compileOnly(paper(libs.versions.minecraft.get()))
    compileOnly(kotlin("stdlib-jdk8"))
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
        kotlin.set("1.9.24")
        includeCommitHash.set(true)
    }
}
