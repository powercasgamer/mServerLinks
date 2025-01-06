/*
 * This file is part of mServerLinks, licensed under the MIT License.
 *
 * Copyright (c) 2024-2025 powercas_gamer
 * Copyright (c) 2024-2025 contributors
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
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.util.concurrent.TimeUnit

object TaskUtil {

  fun runTaskTimerAsync(
    plugin: Plugin,
    task: Runnable,
    delay: Long = 0,
    period: Long = 0,
    unit: TimeUnit = TimeUnit.SECONDS
  ): Task {
    if (VersionUtil.isFolia()) {
      return Task(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, { scheduledTask -> task.run() }, delay, period, unit), null)
    }
    return Task(
      null,
      plugin.server.scheduler.runTaskTimerAsynchronously(
        plugin,
        task,
        unit.toSeconds(delay) * 20,
        unit.toSeconds
          (period) * 20
      )
    )
  }
}

data class Task(val scheduledTask: ScheduledTask?, val bukkitTask: BukkitTask?)
