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
package dev.mizule.mserverlinks.paper.listener;

import dev.mizule.mserverlinks.bukkit.util.LinkUtil;
import dev.mizule.mserverlinks.bukkit.util.PlaceholderUtil;
import dev.mizule.mserverlinks.core.config.Link;
import dev.mizule.mserverlinks.paper.mServerLinksBootstrapper;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLinksSendEvent;

import java.net.URI;
import java.util.Map;

public class LinkListener implements Listener {

    private final mServerLinksBootstrapper bootstrapper;

    public LinkListener(final mServerLinksBootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
    }

    @EventHandler
    public void someEvent(final PlayerLinksSendEvent event) {
        final Player player = event.getPlayer();
        final ServerLinks serverLinks = event.getLinks();
        for (final Map.Entry<String, Link> entry : this.bootstrapper.config().get().playerLinks().entrySet()) {
            final Link link = entry.getValue();
            final String permission = link.permission();
            final ServerLinks.Type type = LinkUtil.toBukkitLink(link.type());
            final URI uri = URI.create(link.url());
            if (link.permission() != null && player.hasPermission(permission)) {
                if (type == null) {
                    serverLinks.addLink(MiniMessage.miniMessage().deserialize(link.name(), PlaceholderUtil.INSTANCE.tags(player)),
                        uri);
                } else {
                    serverLinks.addLink(type, uri);
                }
            }
        }

    }
}
