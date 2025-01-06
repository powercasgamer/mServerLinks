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
package dev.mizule.mserverlinks.velocity.commands;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.CommandSource;
import dev.mizule.mserverlinks.velocity.mServerLinks;
import org.incendo.cloud.Command;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.setting.ManagerSetting;
import org.incendo.cloud.velocity.CloudInjectionModule;
import org.incendo.cloud.velocity.VelocityCommandManager;


public class Commands {

  private final mServerLinks plugin;
  private final VelocityCommandManager<CommandSource> commandManager;

  public Commands(mServerLinks plugin) {
    this.plugin = plugin;

    final Injector childInjector = this.plugin.injector().createChildInjector(
      new CloudInjectionModule<>(
        CommandSource.class,
        ExecutionCoordinator.simpleCoordinator(),
        SenderMapper.identity()
      )
    );
    final VelocityCommandManager<CommandSource> commandManager = childInjector.getInstance(
      Key.get(new TypeLiteral<VelocityCommandManager<CommandSource>>() {
      })
    );

    this.commandManager = commandManager;

    commandManager.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
    commandManager.command(rootBuilder()
      .literal("reload")
      .permission(Permission.permission("mserverlinks.reload"))
      .handler(ctx -> {
        ctx.sender().sendRichMessage("<blue>[mServerLinks] Reloading... this may take a few seconds.");
        this.plugin.config().reload().whenComplete((success, throwable) -> {
          if (throwable != null) {
            plugin.logger().error("Failed to reload config", throwable);
            return;
          }
          if (success) {
            plugin.logger().info("unregistering links");
            this.plugin.linksManager().unregisterLinks();
            this.plugin.linksManager().registerLinks();
            ctx.sender().sendRichMessage("Config reloaded!");
            ctx.sender().sendRichMessage("<i>Note: You need to relog for changes to take effect.");
          } else {
            ctx.sender().sendRichMessage("Config not reloaded :(");
          }
        });
      })
    );
  }

  private Command.Builder<CommandSource> rootBuilder() {
    return commandManager.commandBuilder("vmserverlinks", "vserverlinks")
      .commandDescription(CommandDescription.commandDescription("Main command for mServerLinks"));
  }
}
