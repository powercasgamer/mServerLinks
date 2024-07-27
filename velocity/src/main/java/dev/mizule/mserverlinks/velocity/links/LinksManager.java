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
package dev.mizule.mserverlinks.velocity.links;

import com.velocitypowered.api.util.ServerLink;
import dev.mizule.mserverlinks.velocity.config.Link;
import dev.mizule.mserverlinks.velocity.mServerLinks;
import io.github.miniplaceholders.api.MiniPlaceholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinksManager {

    private final mServerLinks plugin;
    private final ComponentLogger logger;
    private final List<ServerLink> links;
    private final Map<String, ServerLink> playerLinks;

    public LinksManager(final mServerLinks plugin) {
        this.plugin = plugin;
        this.logger = plugin.logger();
        this.links = new ArrayList<>();
        this.playerLinks = new HashMap<>();
    }

    public void unregisterLinks() {
        for (final ServerLink link : links) {
            logger.info(
                "Unregistering link: {}",
                link.getCustomLabel().orElse(Component.text(link.getBuiltInType().orElseThrow().name()))
            );
        }
    }

    public void registerLinks() {
        for (final Map.Entry<String, Link> entry : this.plugin.config().get().links().entrySet()) {
            final String name = entry.getKey();
            final Link link = entry.getValue();
            final ServerLink.Type type = link.type();

            logger.info("Registering link: {}", name);

            if (type == null) {
                links.add(ServerLink.serverLink(MiniMessage.miniMessage().deserialize(link.name(), resolvers()), link.url()));
            } else {
                links.add(ServerLink.serverLink(type, link.url()));
            }
        }
    }

    public void registerPlayerLinks() {
        for (final Map.Entry<String, Link> entry : this.plugin.config().get().playerLinks().entrySet()) {
            final String name = entry.getKey();
            final Link link = entry.getValue();
            final ServerLink.Type type = link.type();
            final String permission = link.permission();

            logger.info("Registering player link: {}", name);

            if (type == null) {
                playerLinks.put(permission, ServerLink.serverLink(
                    MiniMessage.miniMessage().deserialize(link.name(), resolvers()),
                    link.url()
                ));
            } else {
                playerLinks.put(permission, ServerLink.serverLink(type, link.url()));
            }
        }
    }

    public List<ServerLink> links() {
        return links;
    }

    public Map<String, ServerLink> playerLinks() {
        return playerLinks;
    }

    private TagResolver resolvers() {
        final TagResolver.Builder builder = TagResolver.builder();

        if (plugin.proxy().getPluginManager().isLoaded("miniplaceholders")) {
            builder.resolver(MiniPlaceholders.getGlobalPlaceholders());
        }
        return builder.build();
    }
}
