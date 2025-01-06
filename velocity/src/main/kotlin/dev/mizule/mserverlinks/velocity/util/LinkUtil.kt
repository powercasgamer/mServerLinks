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
package dev.mizule.mserverlinks.velocity.util

import dev.mizule.mserverlinks.api.link.ServerLinkType
import com.velocitypowered.api.util.ServerLink as VelocityServerLink
import dev.mizule.mserverlinks.api.link.ServerLink as mServerLink

object LinkUtil {

  @JvmStatic
  fun toVelocityLink(serverLinkType: ServerLinkType?): VelocityServerLink.Type? = when (serverLinkType) {
    ServerLinkType.BUG_REPORT -> VelocityServerLink.Type.BUG_REPORT
    ServerLinkType.COMMUNITY_GUIDELINES -> VelocityServerLink.Type.COMMUNITY_GUIDELINES
    ServerLinkType.SUPPORT -> VelocityServerLink.Type.SUPPORT
    ServerLinkType.STATUS -> VelocityServerLink.Type.STATUS
    ServerLinkType.FEEDBACK -> VelocityServerLink.Type.FEEDBACK
    ServerLinkType.COMMUNITY -> VelocityServerLink.Type.COMMUNITY
    ServerLinkType.WEBSITE -> VelocityServerLink.Type.WEBSITE
    ServerLinkType.FORUMS -> VelocityServerLink.Type.FORUMS
    ServerLinkType.NEWS -> VelocityServerLink.Type.NEWS
    ServerLinkType.ANNOUNCEMENTS -> VelocityServerLink.Type.ANNOUNCEMENTS
    ServerLinkType.CUSTOM -> null
    else -> null // throw IllegalArgumentException("Unknown ServerLinkType: ${serverLinkType.name}")
  }

  @JvmStatic
  fun fromVelocityLink(serverLinkType: VelocityServerLink.Type?): ServerLinkType? = when (serverLinkType) {
    VelocityServerLink.Type.BUG_REPORT -> ServerLinkType.BUG_REPORT
    VelocityServerLink.Type.COMMUNITY_GUIDELINES -> ServerLinkType.COMMUNITY_GUIDELINES
    VelocityServerLink.Type.SUPPORT -> ServerLinkType.SUPPORT
    VelocityServerLink.Type.STATUS -> ServerLinkType.STATUS
    VelocityServerLink.Type.FEEDBACK -> ServerLinkType.FEEDBACK
    VelocityServerLink.Type.COMMUNITY -> ServerLinkType.COMMUNITY
    VelocityServerLink.Type.WEBSITE -> ServerLinkType.WEBSITE
    VelocityServerLink.Type.FORUMS -> ServerLinkType.FORUMS
    VelocityServerLink.Type.NEWS -> ServerLinkType.NEWS
    VelocityServerLink.Type.ANNOUNCEMENTS -> ServerLinkType.ANNOUNCEMENTS
    else -> null // throw IllegalArgumentException("Unknown ServerLinkType: ${serverLinkType?.name}")
  }

  @JvmStatic
  fun toVelocity(serverLink: mServerLink): VelocityServerLink = if (serverLink.type != null) {
    VelocityServerLink.serverLink(toVelocityLink(serverLink.type), serverLink.uri().toString())
  } else {
    VelocityServerLink.serverLink(serverLink.displayName(), serverLink.uri().toString())
  }

  @JvmStatic
  fun fromVelocity(serverLink: VelocityServerLink): mServerLink = if (serverLink.builtInType.isPresent) {
    mServerLink(null, serverLink.url, fromVelocityLink(serverLink.builtInType.get()))
  } else {
    mServerLink(serverLink.customLabel.orElseThrow(), serverLink.url, null)
  }
}
