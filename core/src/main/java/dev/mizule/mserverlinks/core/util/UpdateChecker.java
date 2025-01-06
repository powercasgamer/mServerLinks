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
package dev.mizule.mserverlinks.core.util;


/*
 * This file is part of MiniMOTD, licensed under the MIT License.
 *
 * Copyright (c) 2020-2024 Jason Penilla
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

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import dev.mizule.mserverlinks.core.Constants;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class UpdateChecker {

  private final JsonParser parser = new JsonParser();
  private final HttpClient client = HttpClient.newHttpClient();

  public @NonNull List<String> checkVersion() {
    final JsonArray result;
    final String url = String.format("https://api.github.com/repos/%s/%s/releases", Constants.GIT_USER,
        Constants.GIT_REPO
    );
    final HttpResponse<String> response;
    try {
      response = this.client.send(
          HttpRequest.newBuilder(URI.create(url)).GET().build(),
          HttpResponse.BodyHandlers.ofString()
      );
    } catch (final IOException | InterruptedException ex) {
      return Collections.singletonList("Cannot look for updates: " + ex.getMessage());
    }
    try {
      result = this.parser.parse(response.body()).getAsJsonArray();
    } catch (final JsonSyntaxException ex) {
      return Collections.singletonList("Cannot look for updates: " + ex.getMessage());
    }

    int rateLimitRemaining = Integer.parseInt(response.headers().firstValue("X-RateLimit-Remaining").orElse("0"));
    System.out.println("ratelimit: " + rateLimitRemaining);
    if (rateLimitRemaining == 0) {
      long resetTime = response.headers().firstValueAsLong("X-RateLimit-Reset").orElse(0) * 1000;
      long currentTime = System.currentTimeMillis();
      long waitTime = resetTime - currentTime;
      return Collections.singletonList("Rate limit exceeded. Please wait " + (waitTime / 1000) + " seconds.");
    }

    final Map<String, String> versionMap = new LinkedHashMap<>();
    result.forEach(element -> versionMap.put(
        element.getAsJsonObject().get("tag_name").getAsString(),
        element.getAsJsonObject().get("html_url").getAsString()
    ));
    final List<String> versionList = new LinkedList<>(versionMap.keySet());
    final String currentVersion = "v" + Constants.VERSION;
    if (versionList.getFirst().equals(currentVersion)) {
      return Collections.emptyList(); // Up to date, do nothing
    }
    if (VersionUtil.isDev()) {
      // TODO: check for commits
      return Arrays.asList(
          "This server is running a development build of " + Constants.NAME + "! (" + currentVersion + ")",
          "The latest official release is " + versionList.getFirst()
      );
    }
    final int versionsBehind = versionList.indexOf(currentVersion);
    return Arrays.asList(
        "There is an update available for " + Constants.NAME + "!",
        "This server is running version " + currentVersion + ", which is " + (versionsBehind == -1
            ? "UNKNOWN"
            : versionsBehind) + " versions outdated.",
        "Download the latest version, " + versionList.getFirst() + " from GitHub at the link below:",
        versionMap.get(versionList.getFirst())
    );
  }
}
