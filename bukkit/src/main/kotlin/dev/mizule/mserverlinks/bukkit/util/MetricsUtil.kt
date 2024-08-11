/*
 * This file is part of mServerLinks, licensed under the MIT License.
 *
 * Copyright (c) 2024 powercas_gamer
 * Copyright (c) 2024 contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package dev.mizule.mserverlinks.bukkit.util

import dev.mizule.mserverlinks.core.util.VersionUtil
import org.bstats.bukkit.Metrics
import org.bstats.charts.DrilldownPie
import org.bstats.charts.SimplePie
import org.bstats.charts.SingleLineChart
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.Callable
import java.util.function.Supplier

object MetricsUtil {

    private var metrics: Metrics? = null

    @JvmStatic
    fun init(plugin: JavaPlugin) {
        if (VersionUtil.isDev()) {
            plugin.logger.warning("You are running a development build of mServerLinks, metrics are disabled!")
            return
        }

        metrics = Metrics(plugin, 22368).apply {
            val onlineMode = Bukkit.getOnlineMode()
            val bungeecord = Bukkit.spigot().spigotConfig.getBoolean("bungeecord", false)
            val bungeecordOnlineMode =
                if (VersionUtil.isPaper()) {
                    Bukkit.spigot().paperConfig.getBoolean("proxies.bungee-cord.online-mode", false)
                } else {
                    false
                }
            val velocity = if (VersionUtil.isPaper()) {
                Bukkit.spigot().paperConfig.getBoolean("proxies.velocity.enabled", false)
            } else {
                false
            }
            val velocityOnlineMode = if (VersionUtil.isPaper()) {
                Bukkit.spigot().paperConfig.getBoolean("proxies.velocity.online-mode", false)
            } else {
                false
            }

            addDrilldownPieChart(
                "authentication",
                Callable {
                    val map = mutableMapOf<String, Map<String, Int>>()

                    val mode: String
                    val details: String

                    if (onlineMode) {
                        mode = "Online Mode"
                        details = "Standalone"
                    } else {
                        if (velocity) {
                            mode = "Proxied"
                            details = if (velocityOnlineMode) "Velocity (Online Mode)" else "Velocity (Offline Mode)"
                        } else if (bungeecord) {
                            mode = "Proxied"
                            details = if (bungeecordOnlineMode) "BungeeCord (Online Mode)" else "BungeeCord (Offline Mode)"
                        } else {
                            mode = "Offline Mode"
                            details = "Standalone"
                        }
                    }

                    map[mode] = mapOf(details to 1)
                    map
                }
            )
        }
    }

    @JvmStatic
    fun shutdown() {
        metrics?.shutdown()
    }

    fun addLineChart(name: String, callable: Callable<Int>) {
        metrics?.addCustomChart(SingleLineChart(name, callable))
    }

    fun addDrilldownPieChart(name: String, callable: Callable<Map<String, Map<String, Int>>>) {
        metrics?.addCustomChart(DrilldownPie(name, callable))
    }

    fun addSimplePieChart(name: String, callable: Callable<String>) {
        metrics?.addCustomChart(SimplePie(name, callable))
    }

    fun buildMetricsInfo(plugin: Any?, versionGetter: Supplier<String>): Map<String, Map<String, Int>> {
        val version = versionGetter.getOrNull() ?: "unknown"
        val status = if (plugin == null) "absent" else "present"

        return mapOf(
            status to mapOf(version to 1)
        )
    }

    private fun Supplier<String>.getOrNull(): String? = try {
        this.get()
    } catch (e: Exception) {
        null
    }
}
