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
package dev.mizule.mserverlinks.core.config.transformations;

import dev.mizule.mserverlinks.core.config.ConfigurationContainer;
import org.slf4j.LoggerFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.net.URI;

import static org.spongepowered.configurate.NodePath.path;

public final class Transformations {

    public static final int VERSION_LATEST = 1;

    private Transformations() {}

    /**
     * Create a new builder for versioned configurations. This builder uses a
     * field in the node (by default {@code schema-version}) to determine the
     * current schema version (using -1 for no version present).
     *
     * @return versioned transformation
     */
    public static ConfigurationTransformation.Versioned create() {
        return ConfigurationTransformation.versionedBuilder()
            .addVersion(VERSION_LATEST, zeroToOne())
            .addVersion(0, initialTransform())
            .versionKey(ConfigurationContainer.VERSION_FIELD)
            .build();
    }

    /**
     * A transformation. This one has multiple actions, and demonstrates how
     * wildcards work.
     *
     * @return created transformation
     */
    public static ConfigurationTransformation initialTransform() {
        return ConfigurationTransformation.builder()
            .addAction(path(), (path, value) -> {
                if (value.node(ConfigurationContainer.VERSION_FIELD).virtual()) {
                    value.node(ConfigurationContainer.VERSION_FIELD).set(Integer.class, 1);
                }
                return null;
            })
            .build();
    }

    public static ConfigurationTransformation zeroToOne() {
        return ConfigurationTransformation.builder()
            .addAction(path("links"), (path, value) -> {
                transformUriToUrl(value);
                return null;
            })
            .addAction(path("player-links"), (path, value) -> {
                transformUriToUrl(value);
                return null;
            })
            .build();
    }

    private static void transformUriToUrl(ConfigurationNode node) {
        for (ConfigurationNode entry : node.childrenMap().values()) {
            if (!entry.node("uri").virtual() && entry.node("url").virtual()) {
                try {
                    String uriString = entry.node("uri").get(URI.class).toString();
                    entry.node("url").set(uriString);

                    entry.node("uri").set(null);
                } catch (final SerializationException e) {
                    throw new RuntimeException("Error converting URI to URL in node " + node, e);
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException("Invalid URI in node " + node, e);
                }
            }
        }
    }

    /**
     * Apply the transformations to a node.
     *
     * <p>This method also prints information about the version update that
     * occurred</p>
     *
     * @param node the node to transform
     * @param <N> node type
     * @return provided node, after transformation
     */
    public static <N extends ConfigurationNode> N updateNode(final N node) throws ConfigurateException {
        if (!node.virtual()) {
            final ConfigurationTransformation.Versioned trans = create();
            final int startVersion = trans.version(node);
            trans.apply(node);
            final int endVersion = trans.version(node);
            if (startVersion != endVersion) {
                LoggerFactory.getLogger("mServerLinks").info("Updated config schema from {} to {}", startVersion, endVersion);
            }
        }
        return node;
    }
}
