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
package dev.mizule.mserverlinks.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import dev.mizule.mserverlinks.core.util.UpdateChecker;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

public class UpdateListener {

    @Subscribe
    public void onPostLogin(final PostLoginEvent event) {
        final Player player = event.getPlayer();
        if (player.hasPermission("mserverlinks.update")) {
          final List<String> notes = new UpdateChecker().checkVersion();
          if (!notes.isEmpty()) {
            for (String note : notes) {
              player.sendRichMessage("<prefix> <message>",
                  Placeholder.parsed("prefix", "<dark_gray>[<gradient:#f421ff:#00bbff>mServerLinks</gradient>]"),
                  Placeholder.unparsed("message", note)
              );
            }
          }
        }
    }

}
