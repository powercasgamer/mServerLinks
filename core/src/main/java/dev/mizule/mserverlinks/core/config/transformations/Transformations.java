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
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.transformation.ConfigurationTransformation;

import java.net.URI;
import java.nio.file.Paths;

import static org.spongepowered.configurate.NodePath.path;

/**
 * An example of how to use transformations to migrate a configuration to a
 * newer schema version.
 *
 * <p>It's like DFU but not hot garbage! (and probably less PhD-worthy)</p>
 */
public final class Transformations {

    public static final int VERSION_LATEST = 2; // easy way to track the latest version, update as more revisions are added

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
            .addVersion(VERSION_LATEST, test()) // syntax: target version, latest version
            .addVersion(1, zeroToOne())
            .addVersion(0, initialTransform())
            .versionKey(path(ConfigurationContainer.VERSION_FIELD))
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
            // Move the node at `serverVersion` to the location <code>{"server", "version"}</code>
            .addAction(path(), (path, value) -> {
                if (value.node(ConfigurationContainer.VERSION_FIELD).virtual()) {
                    // Add the "config-version" field and set it to 1
                    value.node(ConfigurationContainer.VERSION_FIELD).set(Integer.class, 1);
                }
                return null;
            })
            .build();
    }

    public static ConfigurationTransformation zeroToOne() {
        return ConfigurationTransformation.builder()
            // oh, turns out we want to use a different format for this, so we'll change it again
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

    public static ConfigurationTransformation test() {
        return ConfigurationTransformation.builder()
            // oh, turns out we want to use a different format for this, so we'll change it again
            .addAction(path("update-checker"), (path, value) -> {
                return null;
            })
            .addAction(path("update-checker-links"), (path, value) -> {
                return null;
            })
            .build();
    }

    private static void transformUriToUrl(ConfigurationNode node) {
        // Iterate over each child node (each Link entry in links or playerLinks map)
        for (ConfigurationNode entry : node.childrenMap().values()) {
            // Check if "uri" exists and "url" does not
            if (!entry.node("uri").virtual() && entry.node("url").virtual()) {
                try {
                    // Get the URI and convert it to a String, set the "url" field
                    String uriString = entry.node("uri").get(URI.class).toString();
                    entry.node("url").set(uriString);

                    // Remove the "uri" field to clean up the config
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
        if (!node.virtual()) { // we only want to migrate existing data
            final ConfigurationTransformation.Versioned trans = create();
            final int startVersion = trans.version(node);
            System.out.println(trans.latestVersion()); // prints 2
            System.out.println(trans.versionKey().toString()); // prints [[_version]]
            trans.apply(node);
            final int endVersion = trans.version(node);
            if (startVersion != endVersion) { // we might not have made any changes
                System.out.println("Updated config schema from " + startVersion + " to " + endVersion);
            }
        }
        return node;
    }

    public static void main(final String[] args) throws ConfigurateException {
        if (args.length != 1) {
            System.err.println("Not enough arguments, usage: transformations <file>");
            System.err.println("Apply the test transformations to a single file");
        }
        final HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
            .path(Paths.get(args[0]))
            .build();

        try {
            loader.save(updateNode(loader.load())); // tada
        } catch (final ConfigurateException ex) {
            // We try to update as much as possible, so could theoretically save a partially updated file here
            System.err.println("Failed to fully update node: " + ex.getMessage());
            ex.printStackTrace();
            System.exit(1);
        }
    }

}
