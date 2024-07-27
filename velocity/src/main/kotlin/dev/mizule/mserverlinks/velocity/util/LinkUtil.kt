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
package dev.mizule.mserverlinks.velocity.util

import com.velocitypowered.api.util.ServerLink
import dev.mizule.mserverlinks.core.model.ServerLinkType

object LinkUtil {

    @JvmStatic
    fun toVelocityLink(serverLinkType: ServerLinkType?): ServerLink.Type? = when (serverLinkType) {
        ServerLinkType.BUG_REPORT -> ServerLink.Type.BUG_REPORT
        ServerLinkType.COMMUNITY_GUIDELINES -> ServerLink.Type.COMMUNITY_GUIDELINES
        ServerLinkType.SUPPORT -> ServerLink.Type.SUPPORT
        ServerLinkType.STATUS -> ServerLink.Type.STATUS
        ServerLinkType.FEEDBACK -> ServerLink.Type.FEEDBACK
        ServerLinkType.COMMUNITY -> ServerLink.Type.COMMUNITY
        ServerLinkType.WEBSITE -> ServerLink.Type.WEBSITE
        ServerLinkType.FORUMS -> ServerLink.Type.FORUMS
        ServerLinkType.NEWS -> ServerLink.Type.NEWS
        ServerLinkType.ANNOUNCEMENTS -> ServerLink.Type.ANNOUNCEMENTS
        ServerLinkType.CUSTOM -> null
        else -> null // throw IllegalArgumentException("Unknown ServerLinkType: ${serverLinkType.name}")
    }
}
