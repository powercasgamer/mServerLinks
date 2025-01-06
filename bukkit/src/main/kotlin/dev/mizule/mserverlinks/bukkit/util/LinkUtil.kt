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

import dev.mizule.mserverlinks.api.link.ServerLinkType as ServerLinkType
import org.bukkit.ServerLinks as BukkitServerLink

object LinkUtil {

  @JvmStatic
  fun toBukkitLink(serverLinkType: ServerLinkType?): BukkitServerLink.Type? = when (serverLinkType) {
    ServerLinkType.BUG_REPORT -> BukkitServerLink.Type.REPORT_BUG
    ServerLinkType.COMMUNITY_GUIDELINES -> BukkitServerLink.Type.COMMUNITY_GUIDELINES
    ServerLinkType.SUPPORT -> BukkitServerLink.Type.SUPPORT
    ServerLinkType.STATUS -> BukkitServerLink.Type.STATUS
    ServerLinkType.FEEDBACK -> BukkitServerLink.Type.FEEDBACK
    ServerLinkType.COMMUNITY -> BukkitServerLink.Type.COMMUNITY
    ServerLinkType.WEBSITE -> BukkitServerLink.Type.WEBSITE
    ServerLinkType.FORUMS -> BukkitServerLink.Type.FORUMS
    ServerLinkType.NEWS -> BukkitServerLink.Type.NEWS
    ServerLinkType.ANNOUNCEMENTS -> BukkitServerLink.Type.ANNOUNCEMENTS
    ServerLinkType.CUSTOM -> null
    else -> null // throw IllegalArgumentException("Unknown ServerLinkType: ${serverLinkType.name}")
  }

  @JvmStatic
  fun fromBukkitLink(serverLinkType: BukkitServerLink.Type?): ServerLinkType? = when (serverLinkType) {
    BukkitServerLink.Type.REPORT_BUG -> ServerLinkType.BUG_REPORT
    BukkitServerLink.Type.COMMUNITY_GUIDELINES -> ServerLinkType.COMMUNITY_GUIDELINES
    BukkitServerLink.Type.SUPPORT -> ServerLinkType.SUPPORT
    BukkitServerLink.Type.STATUS -> ServerLinkType.STATUS
    BukkitServerLink.Type.FEEDBACK -> ServerLinkType.FEEDBACK
    BukkitServerLink.Type.COMMUNITY -> ServerLinkType.COMMUNITY
    BukkitServerLink.Type.WEBSITE -> ServerLinkType.WEBSITE
    BukkitServerLink.Type.FORUMS -> ServerLinkType.FORUMS
    BukkitServerLink.Type.NEWS -> ServerLinkType.NEWS
    BukkitServerLink.Type.ANNOUNCEMENTS -> ServerLinkType.ANNOUNCEMENTS
    else -> null // throw IllegalArgumentException("Unknown ServerLinkType: ${serverLinkType?.name}")
  }
}

//
//  @JvmStatic
//  fun toBukkit(serverLink: mServerLink): BukkitServerLink = if (serverLink.type != null) {
//    BukkitServerLink.(toBukkitLink(serverLink.type), serverLink.uri().toString())
//  } else {
//    BukkitServerLink.serverLink(serverLink.displayName(), serverLink.uri().toString())
//  }
//
//  @JvmStatic
//  fun fromBukkit(serverLink: BukkitServerLink): mServerLink = if (serverLink.builtInType.isPresent) {
//    mServerLink(null, serverLink.url, fromBukkitLink(serverLink.builtInType.get()))
//  } else {
//    mServerLink(serverLink.customLabel.orElseThrow(), serverLink.url, null)
//  }
// }
//
// }
