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
package dev.mizule.mserverlinks.paper;

import dev.mizule.mserverlinks.bukkit.config.Config;
import dev.mizule.mserverlinks.core.Constants;
import dev.mizule.mserverlinks.core.config.ConfigurationContainer;
import dev.mizule.mserverlinks.paper.links.LinksManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import net.kyori.adventure.text.Component;
import net.william278.desertwell.about.AboutMenu;
import net.william278.desertwell.util.Version;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.Command;
import org.incendo.cloud.description.CommandDescription;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;
import org.incendo.cloud.permission.Permission;
import org.incendo.cloud.setting.ManagerSetting;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;

public class mServerLinksBootstrapper implements PluginBootstrap {

    private final mServerLinksBootstrapper instance = this;
    private ConfigurationContainer<Config> config;
    private PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager;
    private LinksManager linksManager;

    public void setupConfigs(@NotNull final BootstrapContext context) {
        // check for a Spigot config first.
        final Path spigotConfig = context.getDataDirectory().resolve("config.yml");
        final Path paperConfig = context.getDataDirectory().resolve("config.conf");

        if (Files.exists(spigotConfig) && !Files.exists(paperConfig)) {
            // migrate
            this.config = ConfigurationContainer.migrateYamlToHocon(
                context.getLogger(),
                spigotConfig,
                paperConfig,
                Config.class
            );
        } else {
            this.config = ConfigurationContainer.load(
                context.getLogger(),
                paperConfig,
                Config.class
            );
        }
    }

    @Override
    public void bootstrap(@NotNull final BootstrapContext context) {
        setupConfigs(context);

        final PaperCommandManager.Bootstrapped<CommandSourceStack> mgr =
            PaperCommandManager.builder()
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildBootstrapped(context);

        this.commandManager = mgr;
        this.linksManager = new LinksManager(this);

        mgr.settings().set(ManagerSetting.ALLOW_UNSAFE_REGISTRATION, true);
        mgr.command(rootBuilder()
            .literal("reload")
            .permission(Permission.permission("mserverlinks.reload"))
            .handler(ctx -> {
                this.config.reload().thenAccept(success -> {
                    if (success) {
                        ctx.sender().getSender().sendRichMessage("Config reloaded!");
                        ctx.sender().getSender().sendRichMessage("<i>Note: You need to relog for changes to take effect.");
                    } else {
                        ctx.sender().getSender().sendRichMessage("Config not reloaded :(");
                    }
                    this.linksManager.unregisterLinks();
                    this.linksManager.registerLinks();
                });
            })
        );
        mgr.command(rootBuilder()
            .handler(ctx -> {
                final AboutMenu menu = AboutMenu.builder()
                    .title(Component.text(Constants.NAME))
                    .description(Component.text(Constants.DESCRIPTION))
                    .version(Version.fromString(Constants.VERSION))
                    .credits(
                        "Author",
                        AboutMenu.Credit.of("powercas_gamer").description("Click to visit github").url("https://github" +
                            ".com/powercasgamer")
                    )
                    .buttons(
                        AboutMenu.Link.of(Constants.GIT_URL).text("GitHub").icon("⛏")
                    )
                    .build();
                ctx.sender().getSender().sendMessage(menu.toComponent());
            })
        );
    }

    private Command.Builder<CommandSourceStack> rootBuilder() {
        return commandManager.commandBuilder("mserverlinks", "serverlinks")
            .commandDescription(CommandDescription.commandDescription("Main command for mServerLinks"));
    }

    public mServerLinksBootstrapper instance() {
        return instance;
    }

    public ConfigurationContainer<Config> config() {
        return config;
    }

    public PaperCommandManager.Bootstrapped<CommandSourceStack> commandManager() {
        return commandManager;
    }

    public LinksManager linksManager() {
        return linksManager;
    }

    @Override
    public JavaPlugin createPlugin(final PluginProviderContext context) {
        return new mServerLinks(this);
    }
}
