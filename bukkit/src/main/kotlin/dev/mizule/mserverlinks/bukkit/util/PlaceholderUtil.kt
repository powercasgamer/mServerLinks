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

import io.github.miniplaceholders.api.MiniPlaceholders
import me.clip.placeholderapi.PlaceholderAPI
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlaceholderUtil {

  fun tags(player: Player? = null): TagResolver {
    val builder = TagResolver.builder()

    if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      builder.resolver(papiTag(player))
    }
    if (Bukkit.getPluginManager().isPluginEnabled("MiniPlaceholders")) {
      builder.resolver(MiniPlaceholders.getGlobalPlaceholders())
    }
    return builder.build()
  }

  fun papiTag(player: Player?): TagResolver = TagResolver.resolver(
    "papi"
  ) { argumentQueue: ArgumentQueue, context: Context ->
    // Get the string placeholder that they want to use.
    val papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value()

    // Then get PAPI to parse the placeholder for the given player.
    val parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, "%$papiPlaceholder%")

    // We need to turn this ugly legacy string into a nice component.
    val componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder)
    Tag.selfClosingInserting(componentPlaceholder)
  }
}
