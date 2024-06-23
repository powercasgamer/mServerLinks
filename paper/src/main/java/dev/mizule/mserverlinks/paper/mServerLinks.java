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

import dev.mizule.mserverlinks.core.Constants;
import dev.mizule.mserverlinks.core.config.ConfigManager;
import dev.mizule.mserverlinks.paper.config.Config;
import dev.mizule.mserverlinks.paper.config.Link;
import dev.mizule.mserverlinks.paper.util.UpdateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.william278.desertwell.about.AboutMenu;
import net.william278.desertwell.util.Version;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class mServerLinks extends JavaPlugin {

    private Config config;

    @Override
    public void onLoad() {
        final Path dataFolder = getDataFolder().toPath();
        this.config = ConfigManager.loadConfig(
            dataFolder.resolve("config.conf"),
            HoconConfigurationLoader.builder()
                .path(dataFolder.resolve("config.conf"))
                .indent(2)
                .prettyPrinting(true)
                .build(),
            Config.class
        );
    }

    @Override
    public void onEnable() {
        getLogger().info("mServerLinks has been enabled!");
        if (!Constants.VERSION.endsWith("-SNAPSHOT")) {
            final Metrics metrics = new Metrics(this, 22368);
        } else {
            getSLF4JLogger().warn("You are running a development build of mServerLinks, metrics are disabled!");
        }

        Bukkit.getAsyncScheduler().runAtFixedRate(this, (task) -> {
            checkUpdate();
        }, 0, 1, TimeUnit.MINUTES);

        for (final Map.Entry<String, Link> entry : this.config.links().entrySet()) {
            final String name = entry.getKey();
            final Link link = entry.getValue();

            getLogger().info("Registering link: " + name);

            if (link.type() == null) {
                Bukkit.getServerLinks().addLink(MiniMessage.miniMessage().deserialize(link.name()), link.uri());
            } else {
                Bukkit.getServerLinks().addLink(link.type(), link.uri());
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("mServerLinks has been disabled!");
    }

    private void showAboutMenu(final CommandSender sender) {
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
    }

    private void checkUpdate() {
        final int distance = UpdateUtil.fetchDistanceFromGitHub("powercas_gamer", "mServerLinks", Constants.GIT_COMMIT);
        if (distance > 0) {
            getLogger().info("A new version of mServerLinks is available! (Distance: " + distance + ")");
            getLogger().info("Download it at: " + Constants.GIT_URL);
        } else {
            // test
            getLogger().info("mServerLinks is up to date!");
        }
    }
}
