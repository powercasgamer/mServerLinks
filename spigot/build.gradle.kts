import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.mizule.mizulebuildlogic.util.adventure
import dev.mizule.mizulebuildlogic.util.applyJarMetadata
import dev.mizule.mizulebuildlogic.util.cloud
import dev.mizule.mizulebuildlogic.util.configurate
import dev.mizule.mizulebuildlogic.util.spigot

plugins {
    id(libs.plugins.mizule.platform.spigot.get().pluginId)
    id(libs.plugins.mizule.kotlin.get().pluginId)
    id(libs.plugins.mizule.gremlin.get().pluginId)
}

dependencies {
    api(projects.mserverlinksBukkit)
    compileOnly(spigot(libs.versions.minecraft.get()))
    compileOnly(kotlin("stdlib-jdk8"))
    api(configurate("yaml", "4.2.0-SNAPSHOT"))
    api(configurate("extra-kotlin", "4.2.0-SNAPSHOT")) {
        isTransitive = false
    }
    api(cloud("paper", "2.0.0-beta.8"))
    api("org.bstats:bstats-bukkit:3.0.2")
    api(adventure("text-minimessage", "4.17.0"))
    api(adventure("text-serializer-legacy", "4.17.0"))
}

mizule {
    archiveFileName = "mServerLinks-Spigot-{version}.jar"
    enableCopyTask = true

    shadowOptions {
        enableShadow.set(true)
        relocations.set(
            listOf(
                "org.bstats",
                "org.spongepowered.configurate",
                "org.incendo",
                "io.leangen.geantyref",
                "net.kyori"
            )
        )
    }

    versions {
        includeCommitHash.set(true)
    }
}

applyJarMetadata("mserverlinks-spigot")

tasks {
    afterEvaluate {
        named("shadowJar", ShadowJar::class) {
            manifest {
                attributes("paperweight-mappings-namespace" to "mojang")
            }
        }
    }
}

mizuleSpigotPlatform {
    this.commonPlugins.set(false)
    this.version.set("1.21")
}
