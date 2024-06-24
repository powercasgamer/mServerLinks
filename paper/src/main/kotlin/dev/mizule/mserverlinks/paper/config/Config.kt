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
package dev.mizule.mserverlinks.paper.config

import org.bukkit.ServerLinks
import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import org.spongepowered.configurate.objectmapping.meta.Setting
import java.net.URI

@ConfigSerializable
@JvmRecord
data class Config(

    @Comment(
        """Possible values Type:
 - REPORT_BUG
 - COMMUNITY_GUIDELINES
 - SUPPORT
 - STATUS
 - FEEDBACK
 - COMMUNITY
 - WEBSITE
 - FORUMS
 - NEWS
 - ANNOUNCEMENTS"""
    )
    val links: Map<String, Link> = mapOf(
        "example" to Link("<red>Example", URI.create("https://example.com")),
        "bug" to Link("Report a Bug", URI.create("https://example.com"), ServerLinks.Type.REPORT_BUG),
    ),

    @Comment(
        "Links that are only visible to players with the specified permission\nThis currently works but is not recommended" +
            " to use due to Spigot's implementation of the event."
    )
    val playerLinks: Map<String, Link> = mapOf(
        "staff-guide" to Link("Staff Guide", URI.create("https://example.com"), permission = "mserverlinks.staff"),
    ),

    val updateChecker: Boolean = true,
    @Setting(value = "bStats")
    val bStats: Boolean = true,
)

@ConfigSerializable
@JvmRecord
data class Link(
    val name: String,
    val uri: URI,
    val type: ServerLinks.Type? = null,
    val permission: String? = null,
)
