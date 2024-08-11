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
package dev.mizule.mserverlinks.spigot.links;

import dev.mizule.mserverlinks.bukkit.util.LinkUtil;
import dev.mizule.mserverlinks.core.config.Link;
import dev.mizule.mserverlinks.spigot.mServerLinks;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinksManager {

    private final mServerLinks bootstrapper;
    private final Logger logger = LoggerFactory.getLogger(LinksManager.class);
    private final List<ServerLinks.ServerLink> links;

    public LinksManager(final mServerLinks bootstrapper) {
        this.bootstrapper = bootstrapper;
        this.links = new ArrayList<>();
    }

    public void unregisterLinks() {
        for (final ServerLinks.ServerLink link : links) {
            logger.info("Unregistering link: {}", link.getDisplayName());
            Bukkit.getServerLinks().removeLink(link);
        }
    }

    public void registerLinks() {
        for (final Map.Entry<String, Link> entry : this.bootstrapper.config().get().links().entrySet()) {
            final String name = entry.getKey();
            final Link link = entry.getValue();
            final ServerLinks.Type type = LinkUtil.toBukkitLink(link.type());
            final URI uri = URI.create(link.url());

            logger.info("Registering link: {}", name);

            if (type == null) {
                links.add(Bukkit
                    .getServerLinks()
                    .addLink(mServerLinks.LEGACY_SERIALIZER
                        .serialize(MiniMessage.miniMessage().deserialize(link.name())), uri));
            } else {
                links.add(Bukkit.getServerLinks().addLink(type, uri));
            }
        }
    }
}
