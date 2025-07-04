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
package dev.mizule.mserverlinks.paper;

import dev.mizule.mserverlinks.bukkit.util.MetricsUtil;
import dev.mizule.mserverlinks.core.Constants;
import dev.mizule.mserverlinks.core.util.UpdateUtil;
import dev.mizule.mserverlinks.paper.listener.LinkListener;
import io.papermc.paper.ServerBuildInfo;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.paper.PaperCommandManager;

import java.util.concurrent.TimeUnit;

public class mServerLinks extends JavaPlugin {

    private final PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager;
    private final mServerLinksBootstrapper bootstrapper;
    private boolean shouldDisable = false;
    private final String[] messages = {
      "*".repeat(64),
      "This server is running an unsupported version of Paper!",
      "This version of mServerLinks only supports Paper 1.21 up to 1.21.6!",
      "If you are running a newer version, please update mServerLinks to the latest version!",
      "*".repeat(64)
  };

    public mServerLinks(
        final mServerLinksBootstrapper bootstrapper
    ) {
        this.bootstrapper = bootstrapper;
        this.commandManager = bootstrapper.commandManager();
    }

    @Override
    public void onLoad() {
      if (Bukkit.getUnsafe().getDataVersion() > 4435) {
        for (final String message : messages) {
          getSLF4JLogger().error(message);
        }
        shouldDisable = true;
      }
    }

    @Override
    public void onEnable() {
      if ((Bukkit.getUnsafe().getDataVersion() > 4435) || shouldDisable) {
        for (final String message : messages) {
          getSLF4JLogger().error(message);
        }
        Bukkit.getPluginManager().disablePlugin(this);
        return;
      }

        this.commandManager.onEnable();
        getLogger().info("mServerLinks has been enabled!");
        if (this.bootstrapper.config().get().bStats()) {
            getSLF4JLogger().info("bStats has been enabled, to disable it set 'bStats' to false in the config!");
            MetricsUtil.init(this);
        }

        if (false) {
            if (this.bootstrapper.config().get().updateChecker()) {
                getSLF4JLogger().info("UpdateChecker has been enabled, to disable it set 'update-checker' to false in the config!");
                Bukkit.getAsyncScheduler().runAtFixedRate(this, (task) -> {
                    checkUpdate();
                }, 0, 3, TimeUnit.HOURS);
            }
        }
        Bukkit.getPluginManager().registerEvents(new LinkListener(this.bootstrapper), this);

        this.bootstrapper.linksManager().registerLinks();
    }

    @Override
    public void onDisable() {
        MetricsUtil.shutdown();
        getLogger().info("mServerLinks has been disabled!");
    }

    private void checkUpdate() {
        final int distance = UpdateUtil.fetchDistanceFromGitHub("powercasgamer/mServerLinks", Constants.GIT_BRANCH,
            Constants.GIT_COMMIT
        );
        if (distance == UpdateUtil.DISTANCE_ERROR) {
            getLogger().warning("Failed to check for updates!");
            return;
        } else if (distance == UpdateUtil.DISTANCE_UNKNOWN) {
            getLogger().warning("Failed to check for updates!");
            return;
        }

        if (distance > 0) {
            getLogger().info("A new version of mServerLinks is available! (Distance: " + distance + ")");
            getLogger().info("Download it at: " + Constants.GIT_URL + "/releases");
        }
    }
}
