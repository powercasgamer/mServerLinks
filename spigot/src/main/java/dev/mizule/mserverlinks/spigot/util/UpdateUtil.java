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
package dev.mizule.mserverlinks.spigot.util;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * From Paper, contributed by Techcable Techcable@outlook.com in PaperMC/Paper/GH-65
 */
public class UpdateUtil {

    public static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER =
        LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR
        ).hexColors().useUnusualXRepeatedCharacterHexFormat().build();

    public static final int DISTANCE_ERROR = -1;
    public static final int DISTANCE_UNKNOWN = -2;
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUtil.class);

    public static int fetchDistanceFromGitHub(final String repo, final String branch, final String hash) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) URI
                .create("https://api.github.com/repos/%s/compare/%s...%s".formatted(repo, branch, hash)).toURL().openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                return DISTANCE_UNKNOWN; // Unknown commit
            }
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(),
                Charsets.UTF_8
            ))) {
                final JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                final String status = obj.get("status").getAsString();
                return switch (status) {
                    case "identical" -> 0;
                    case "behind" -> obj.get("behind_by").getAsInt();
                    default -> DISTANCE_ERROR;
                };
            } catch (final JsonSyntaxException | NumberFormatException e) {
                LOGGER.error("Error parsing json from GitHub's API", e);
                return DISTANCE_ERROR;
            }
        } catch (final IOException e) {
            LOGGER.error("Error while parsing version", e);
            return DISTANCE_ERROR;
        }
    }
}
