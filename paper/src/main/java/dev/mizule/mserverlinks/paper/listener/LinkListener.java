package dev.mizule.mserverlinks.paper.listener;

import dev.mizule.mserverlinks.paper.config.Link;
import dev.mizule.mserverlinks.paper.mServerLinksBootstrapper;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Map;

public class LinkListener {

    private final mServerLinksBootstrapper bootstrapper;

    public LinkListener(final mServerLinksBootstrapper bootstrapper) {
        this.bootstrapper = bootstrapper;
    }

    @EventHandler
    public void someEvent() {
        final Player player = null; // Get player
        final ServerLinks serverLinks = Bukkit.getServerLinks().copy();
        for (final Map.Entry<String, Link> entry : this.bootstrapper.config().get().playerLinks().entrySet()) {
            final String name = entry.getKey();
            final Link link = entry.getValue();
            final String permission = link.permission();
            final ServerLinks.Type type = link.type();
            if (link.permission() != null && player.hasPermission(permission)) {
                if (type == null) {
                    serverLinks.addLink(MiniMessage.miniMessage().deserialize(link.name()), link.uri());
                } else {
                    serverLinks.addLink(type, link.uri());
                }
            }
        }

        player.sendLinks(serverLinks);
    }
}
