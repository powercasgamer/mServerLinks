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
package dev.mizule.mserverlinks.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.configuration.PlayerEnterConfigurationEvent;
import com.velocitypowered.api.network.ProtocolState;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.ServerLink;
import dev.mizule.mserverlinks.velocity.links.LinksManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinkListener {

    private final LinksManager linksManager;

    private final ComponentLogger logger = ComponentLogger.logger();

    public LinkListener(final LinksManager linksManager) {
        this.linksManager = linksManager;
    }

    @Subscribe
    public void onPostLogin(final PostLoginEvent event) {
        final Player player = event.getPlayer();
        logger.debug("[PLE] Player: {} State: {}", player.getUsername(), player.getProtocolState());
        final ProtocolState protocolState = player.getProtocolState();
        if (protocolState != ProtocolState.CONFIGURATION && protocolState != ProtocolState.PLAY) return;
        final List<ServerLink> serverLinks = this.linksManager.links();
        final Map<String, ServerLink> playerLinks = this.linksManager.playerLinks();

        final List<ServerLink> allLinks = new ArrayList<>(serverLinks);

        for (final Map.Entry<String, ServerLink> entry : playerLinks.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                allLinks.add(entry.getValue());
            }
        }
        player.setServerLinks(allLinks);
    }

    @Subscribe
    public void confi(final PlayerEnterConfigurationEvent event) {
        final Player player = event.player();
        logger.debug("[PECE] Player: {} State: {}", player.getUsername(), player.getProtocolState());
    }

}
