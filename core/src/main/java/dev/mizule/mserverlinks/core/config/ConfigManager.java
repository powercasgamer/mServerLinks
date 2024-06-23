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
package dev.mizule.mserverlinks.core.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import java.nio.file.Path;

public class ConfigManager {

    public static <T, C extends ConfigurationNode> T loadConfig(final @NotNull Path path, final @NotNull ConfigurationLoader<C> loader, final @NotNull Class<T> clazz) {
        return loadConfig(path, loader, clazz, null);
    }

    public static <T, C extends ConfigurationNode> T loadConfig(final @NotNull Path path, final @NotNull ConfigurationLoader<C> loader, final @NotNull Class<T> clazz, final @Nullable String header) {
        final ConfigurationOptions options = ConfigurationOptions.defaults()
                .shouldCopyDefaults(true)
                .header(header)
                .serializers(builder -> {
                    builder.registerAnnotatedObjects(org.spongepowered.configurate.kotlin.ObjectMappingKt.objectMapperFactory());
                });

        try {
            ConfigurationNode configNode = loader.load(options);
            T config = configNode.get(clazz);
            if (config == null) {
                throw new IllegalStateException("Could not read configuration");
            }

//            if (!path.toFile().exists()) {
                configNode.set(config); // update the backing node to add defaults
                loader.save(configNode);
//            }

            return config;
        } catch (final Exception exception) {
            // cry
            throw new RuntimeException((exception));
        }
    }
}
