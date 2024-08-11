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

import dev.mizule.mserverlinks.core.model.ServerLinkType
import org.bukkit.ServerLinks

object LinkUtil {

    @JvmStatic
    fun toBukkitLink(serverLinkType: ServerLinkType?): ServerLinks.Type? = when (serverLinkType) {
        ServerLinkType.BUG_REPORT -> ServerLinks.Type.REPORT_BUG
        ServerLinkType.COMMUNITY_GUIDELINES -> ServerLinks.Type.COMMUNITY_GUIDELINES
        ServerLinkType.SUPPORT -> ServerLinks.Type.SUPPORT
        ServerLinkType.STATUS -> ServerLinks.Type.STATUS
        ServerLinkType.FEEDBACK -> ServerLinks.Type.FEEDBACK
        ServerLinkType.COMMUNITY -> ServerLinks.Type.COMMUNITY
        ServerLinkType.WEBSITE -> ServerLinks.Type.WEBSITE
        ServerLinkType.FORUMS -> ServerLinks.Type.FORUMS
        ServerLinkType.NEWS -> ServerLinks.Type.NEWS
        ServerLinkType.ANNOUNCEMENTS -> ServerLinks.Type.ANNOUNCEMENTS
        ServerLinkType.CUSTOM -> null
        else -> null // throw IllegalArgumentException("Unknown ServerLinkType: ${serverLinkType.name}")
    }
}
