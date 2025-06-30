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
package dev.mizule.mserverlinks.core.config;

import dev.mizule.mserverlinks.core.config.serializer.ServerLinkTypeSerializer;
import dev.mizule.mserverlinks.core.config.transformations.Transformations;
import dev.mizule.mserverlinks.core.model.ServerLinkType;
import io.leangen.geantyref.TypeToken;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.kotlin.ObjectMappingKt;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/*
 * KickRedirect
 * Copyright (c) 4drian3d
 *.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * KickRedirect License: https://github.com/4drian3d/KickRedirect/blob/main/LICENSE
 * KickRedirect: https://github.com/4drian3d/KickRedirect/
 */
public final class ConfigurationContainer<C> {

  private final AtomicReference<C> config;
  private final ConfigurationLoader<? extends ConfigurationNode> loader;
  private final Class<C> clazz;
  private final Logger logger;
  public static final String VERSION_FIELD = "config-version";

  private ConfigurationContainer(
      final C config,
      final Class<C> clazz,
      final ConfigurationLoader<? extends @NotNull ConfigurationNode> loader,
      final Logger logger
  ) {
    this.config = new AtomicReference<>(config);
    this.loader = loader;
    this.clazz = clazz;
    this.logger = logger;
  }

  public static void verifyConfigVersion(final Logger logger, final ConfigurationNode globalNode, final int latestVersion) {
    final ConfigurationNode version = globalNode.node(ConfigurationContainer.VERSION_FIELD);
    if (version.virtual()) {
      logger.warn("The config file didn't have a version set, assuming latest");
      version.raw(latestVersion);
    } else if (version.getInt() > latestVersion) {
      logger.error(
          "Loading a newer configuration than is supported ({} > {})! You may have to backup & delete your config file to start the server.",
          version.getInt(),
          latestVersion
      );
    }
  }

  public CompletableFuture<Boolean> reload() {
    return CompletableFuture.supplyAsync(() -> {
      try {
        final ConfigurationNode node = loader.load();
        C newConfig = node.get(clazz);
        node.set(clazz, newConfig);
        loader.save(node);
        config.set(newConfig);
        return true;
      } catch (ConfigurateException exception) {
        logger.error("Could not reload {} configuration file", clazz.getSimpleName(), exception);
        return false;
      }
    });
  }

  public @NotNull C get() {
    return this.config.get();
  }

  public static <C> ConfigurationContainer<C> load(
      final Logger logger,
      final Path path,
      final Class<C> clazz
  ) {
    final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
        .indent(2)
        .prettyPrinting(true)
        .defaultOptions(opts -> opts
            .shouldCopyDefaults(true)
            .header("mServerLinks | by powercas_gamer\n")
            .serializers(builder -> {
              builder.register(TypeToken.get(ServerLinkType.class), new ServerLinkTypeSerializer());
              builder.registerAnnotatedObjects(ObjectMappingKt.objectMapperFactory());
            })
        )
        .path(path)
        .build();

    try {
      ConfigurationNode node;
      if (Files.notExists(path)) {
        node = loader.load();
        node.node(ConfigurationContainer.VERSION_FIELD).raw(Transformations.VERSION_LATEST);
      } else {
        node = Transformations.updateNode(loader.load());
        verifyConfigVersion(logger, node, Transformations.VERSION_LATEST);
      }
      final C config = node.get(clazz);
      node.set(clazz, config);
      loader.save(node);
      return new ConfigurationContainer<>(config, clazz, loader, logger);
    } catch (ConfigurateException exception) {
      logger.error("Could not load {} configuration file", clazz.getSimpleName(), exception);
      return null;
    }
  }

  public static <C> ConfigurationContainer<C> loadYaml(
      final Logger logger,
      final Path path,
      final Class<C> clazz
  ) {
    final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
        .indent(2)
        .nodeStyle(NodeStyle.BLOCK)
        .defaultOptions(opts -> opts
            .shouldCopyDefaults(true)
            .header("mServerLinks | by powercas_gamer\n")
            .serializers(builder -> {
              builder.register(TypeToken.get(ServerLinkType.class), new ServerLinkTypeSerializer());
              builder.registerAnnotatedObjects(org.spongepowered.configurate.kotlin.ObjectMappingKt.objectMapperFactory());
            })
        )
        .path(path)
        .build();

    try {
      ConfigurationNode node;
      if (Files.notExists(path)) {
        node = loader.load();
        node.node(ConfigurationContainer.VERSION_FIELD).raw(Transformations.VERSION_LATEST);
      } else {
        node = Transformations.updateNode(loader.load());
        verifyConfigVersion(logger, node, Transformations.VERSION_LATEST);
      }
      final C config = node.get(clazz);
      node.set(clazz, config);
      loader.save(node);
      return new ConfigurationContainer<>(config, clazz, loader, logger);
    } catch (ConfigurateException exception) {
      logger.error("Could not load {} configuration file", clazz.getSimpleName(), exception);
      return null;
    }
  }

  public static <C> ConfigurationContainer<C> migrateYamlToHocon(
      final Logger logger,
      final Path yamlPath,
      final Path hoconPath,
      final Class<C> clazz
  ) {

    final YamlConfigurationLoader oldFormat = YamlConfigurationLoader.builder()
        .indent(2)
        .nodeStyle(NodeStyle.BLOCK)
        .defaultOptions(opts -> opts
            .shouldCopyDefaults(true)
            .header("mServerLinks | by powercas_gamer\n")
            .serializers(builder -> {
              builder.registerAnnotatedObjects(org.spongepowered.configurate.kotlin.ObjectMappingKt.objectMapperFactory());
            })
        ).path(yamlPath)
        .build();

    final HoconConfigurationLoader newFormat = HoconConfigurationLoader.builder()
        .indent(2)
        .prettyPrinting(true)
        .defaultOptions(opts -> opts
            .shouldCopyDefaults(true)
            .header("mServerLinks | by powercas_gamer\n")
            .serializers(builder -> {
              builder.registerAnnotatedObjects(org.spongepowered.configurate.kotlin.ObjectMappingKt.objectMapperFactory());
            })
        )

        .path(hoconPath)
        .build();

    try {
      ConfigurationNode node;
      node = Transformations.updateNode(oldFormat.load());
      verifyConfigVersion(logger, node, Transformations.VERSION_LATEST);
      final C config = node.get(clazz);
      node.set(clazz, config);
      newFormat.save(node);
      return new ConfigurationContainer<>(config, clazz, newFormat, logger);
    } catch (ConfigurateException exception) {
      logger.error("Could not load {} configuration file", clazz.getSimpleName(), exception);
      return null;
    }
  }
}
