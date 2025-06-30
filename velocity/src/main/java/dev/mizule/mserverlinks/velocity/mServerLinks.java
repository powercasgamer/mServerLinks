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
package dev.mizule.mserverlinks.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginManager;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import dev.mizule.mserverlinks.core.Constants;
import dev.mizule.mserverlinks.core.config.Config;
import dev.mizule.mserverlinks.core.config.ConfigurationContainer;
import dev.mizule.mserverlinks.core.util.VersionUtil;
import dev.mizule.mserverlinks.velocity.commands.Commands;
import dev.mizule.mserverlinks.velocity.links.LinksManager;
import dev.mizule.mserverlinks.velocity.listener.LinkListener;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.velocity.Metrics;
import xyz.jpenilla.gremlin.runtime.DependencyCache;
import xyz.jpenilla.gremlin.runtime.DependencyResolver;
import xyz.jpenilla.gremlin.runtime.DependencySet;
import xyz.jpenilla.gremlin.runtime.logging.Slf4jGremlinLogger;
import xyz.jpenilla.gremlin.runtime.platformsupport.VelocityClasspathAppender;

import java.nio.file.Path;

@Plugin(
    id = "mserverlinks",
    name = Constants.NAME,
    version = Constants.VERSION,
    authors = Constants.AUTHOR,
    description = Constants.DESCRIPTION,
    dependencies = {@Dependency(id = "miniplaceholders", optional = true)}
)
public class mServerLinks {

  private final Path dataDirectory;
  private final ComponentLogger logger;
  private final PluginManager pluginManager;
  private final EventManager eventManager;
  private final ProxyServer proxy;
  private final Metrics.Factory metricsFactory;
  private final Injector injector;
  private ConfigurationContainer<Config> config;
  private LinksManager linksManager;

  @Inject
  public mServerLinks(
      ProxyServer proxy, ComponentLogger logger, final @DataDirectory Path datDirectory,
      final PluginManager pluginManager, final EventManager eventManager,
      final Metrics.Factory metricsFactory, final Injector injector
  ) {
    this.proxy = proxy;
    this.logger = logger;
    this.dataDirectory = datDirectory;
    this.eventManager = eventManager;
    this.pluginManager = pluginManager;
    this.metricsFactory = metricsFactory;
    this.injector = injector;
  }

  @Subscribe
  public void onProxyInitialization(final ProxyInitializeEvent event) {
    dependencies();
    logger.warn("""

        =======================================================================================================

        This plugin will not work properly if you have it installed on both your backend and the proxy.
        Please only install it on your proxy OR your backend, not both.
        If you run into any issues with both installed, you will not receive support.

        =======================================================================================================
        """);

    this.config = ConfigurationContainer.load(
        logger,
        dataDirectory.resolve("config.conf"),
        Config.class
    );
    this.linksManager = new LinksManager(this);
    this.linksManager.registerLinks();
    this.linksManager.registerPlayerLinks();

    if (this.config().get().bStats()) {
      logger.info("bStats has been enabled, to disable it set 'bStats' to false in the config!");
      if (!VersionUtil.isDev()) {
        final Metrics metrics = this.metricsFactory.make(this, 22401);
      } else {
        logger.warn("You are running a development build of mServerLinks, metrics are disabled!");
      }
    }

    new Commands(this);

    this.eventManager.register(this, new LinkListener(this.linksManager));
  }

  private void dependencies() {
    final DependencySet deps = DependencySet.readDefault(this.getClass().getClassLoader());
    final DependencyCache cache = new DependencyCache(this.dataDirectory.resolve("libraries"));
    try (final DependencyResolver downloader = new DependencyResolver(new Slf4jGremlinLogger(logger))) {
      new VelocityClasspathAppender(this.proxy, this).append(downloader.resolve(deps, cache).jarFiles());
    }
    cache.cleanup();
  }

  public Path dataDirectory() {
    return dataDirectory;
  }

  public ComponentLogger logger() {
    return logger;
  }

  public ConfigurationContainer<Config> config() {
    return config;
  }

  public ProxyServer proxy() {
    return proxy;
  }

  public Injector injector() {
    return injector;
  }

  public LinksManager linksManager() {
    return linksManager;
  }

  public EventManager eventManager() {
    return eventManager;
  }

  public PluginManager pluginManager() {
    return pluginManager;
  }
}
