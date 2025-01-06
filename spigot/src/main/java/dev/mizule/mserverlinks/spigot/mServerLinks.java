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
package dev.mizule.mserverlinks.spigot;

import dev.mizule.mserverlinks.bukkit.util.MetricsUtil;
import dev.mizule.mserverlinks.bukkit.util.TaskUtil;
import dev.mizule.mserverlinks.core.Constants;
import dev.mizule.mserverlinks.core.api.ApiRegistrationUtil;
import dev.mizule.mserverlinks.core.api.mServerLinksAPIProvider;
import dev.mizule.mserverlinks.core.config.Config;
import dev.mizule.mserverlinks.core.config.ConfigurationContainer;
import dev.mizule.mserverlinks.core.task.UpdateTask;
import dev.mizule.mserverlinks.core.util.VersionUtil;
import dev.mizule.mserverlinks.spigot.links.LinksManager;
import dev.mizule.mserverlinks.spigot.listener.LinkListener;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.incendo.cloud.permission.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class mServerLinks extends JavaPlugin {

  public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
    .character(LegacyComponentSerializer.SECTION_CHAR)
    .hexColors()
    .useUnusualXRepeatedCharacterHexFormat()
    .build();
  private static final Logger LOGGER = LoggerFactory.getLogger(mServerLinks.class);
  private mServerLinksAPIProvider mServerLinksProvider;
  private LegacyPaperCommandManager<CommandSender> commandManager;
  private ConfigurationContainer<Config> config;
  private LinksManager linksManager;

  @Override
  public void onLoad() {
    this.config = ConfigurationContainer.loadYaml(LOGGER, this.getDataFolder().toPath().resolve("config.yml"), Config.class);
    this.linksManager = new LinksManager(this);
    this.mServerLinksProvider = new mServerLinksAPIProvider();

    if (VersionUtil.isFolia()) {
      getLogger().info("It appears you're using Folia, this platform has not been thoroughly tested with mServerLinks. " +
        "Proceed with caution!");
    }

    if (VersionUtil.isPaper()) {
      getLogger().info("It appears you're using Paper, please download the Paper jar from " + Constants.GIT_URL +
        "/releases/latest");
    }
  }

  @Override
  public void onEnable() {
    this.commandManager = LegacyPaperCommandManager.createNative(this, ExecutionCoordinator.simpleCoordinator());
    getLogger().info("mServerLinks has been enabled!");
    if (this.config.get().bStats()) {
      getLogger().info("bStats has been enabled, to disable it set 'bStats' to false in the config!");
      MetricsUtil.init(this);
    }

    if (this.config.get().updateChecker()) {
      getLogger().info("UpdateChecker has been enabled, to disable it set 'update-checker' to false in the config!");
      TaskUtil.INSTANCE.runTaskTimerAsync(this, new UpdateTask(this.getLogger()), 0, 3, TimeUnit.HOURS);
    }

    commands();

    Bukkit.getPluginManager().registerEvents(new LinkListener(this), this);

    this.linksManager.registerLinks();
    ApiRegistrationUtil.registerProvider(mServerLinksProvider);
  }

  public mServerLinksAPIProvider apiProvider() {
    return mServerLinksProvider;
  }

  @Override
  public void onDisable() {
    MetricsUtil.shutdown();
    ApiRegistrationUtil.unregisterProvider();
    getLogger().info("mServerLinks has een disabled!");
  }

  private Command.Builder<CommandSender> rootBuilder() {
    return commandManager.commandBuilder("mserverlinks", "serverlinks")
      .commandDescription(CommandDescription.commandDescription("Main command for mServerLinks"));
  }

  private void commands() {
    this.commandManager.command(rootBuilder()
      .literal("reload")
      .permission(Permission.permission("mserverlinks.reload"))
      .handler(ctx -> {
        this.config.reload().thenAccept(success -> {
          if (success) {
            ctx.sender().sendMessage("Config reloaded!");
            ctx.sender().sendMessage("Note: You need to relog for changes to take effect.");
          } else {
            ctx.sender().sendMessage("Config not reloaded :(");
          }
          this.linksManager.unregisterLinks();
          this.linksManager.registerLinks();
        });
      })
    );
  }

  public LegacyPaperCommandManager<CommandSender> commandManager() {
    return commandManager;
  }

  public ConfigurationContainer<Config> config() {
    return config;
  }

  public LinksManager linksManager() {
    return linksManager;
  }
}
