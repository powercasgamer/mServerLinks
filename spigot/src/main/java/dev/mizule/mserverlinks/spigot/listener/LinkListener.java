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
package dev.mizule.mserverlinks.spigot.listener;

import dev.mizule.mserverlinks.api.event.user.UserSendLinksEvent;
import dev.mizule.mserverlinks.api.link.ServerLink;
import dev.mizule.mserverlinks.bukkit.util.LinkUtil;
import dev.mizule.mserverlinks.core.api.impl.ApiUser;
import dev.mizule.mserverlinks.core.config.Link;
import dev.mizule.mserverlinks.spigot.mServerLinks;
import net.kyori.adventure.text.minimessage.MiniMessage;
import ninja.egg82.events.KyoriEvents;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLinksSendEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LinkListener implements Listener {

  private final mServerLinks bootstrapper;

  public LinkListener(final mServerLinks bootstrapper) {
    this.bootstrapper = bootstrapper;
  }

  @EventHandler
  public void someEvent(final PlayerLinksSendEvent event) {
    final Player player = event.getPlayer();
    final ServerLinks serverLinks = event.getLinks();

    final Map<String, ServerLink> playerLinks = new HashMap<>();

    for (final Map.Entry<String, Link> entry : this.bootstrapper.config().get().playerLinks().entrySet()) {
      final String name = entry.getKey();
      final Link link = entry.getValue();
      final String permission = link.permission();
      final ServerLinks.Type type = LinkUtil.toBukkitLink(link.type());
      final URI uri = URI.create(link.url());

      if (link.permission() != null && player.hasPermission(permission)) {
        if (type == null) {
          playerLinks.put(
            name,
            new ServerLink(
              MiniMessage.miniMessage().deserialize(link.name()),
              uri,
              null
            )
          );
        } else {
          playerLinks.put(
            name,
            new ServerLink(
              MiniMessage.miniMessage().deserialize(link.name()),
              uri,
              LinkUtil.fromBukkitLink(type)
            )
          );
        }
      }
    }

    UserSendLinksEvent userSendLinksEvent = new UserSendLinksEvent(
      new ApiUser(player.getUniqueId(), player.getName()),
      new ArrayList<>(playerLinks.values())
    );
    KyoriEvents.call(this.bootstrapper.apiProvider().eventBus(), userSendLinksEvent);

    for (final ServerLink link : userSendLinksEvent.links()) {
      if (link.type() == null) {
        serverLinks.addLink(mServerLinks.LEGACY_SERIALIZER.serialize(link.displayName()), link.uri());
      } else {
        serverLinks.addLink(LinkUtil.toBukkitLink(link.type()), link.uri());
      }
    }
  }
}
