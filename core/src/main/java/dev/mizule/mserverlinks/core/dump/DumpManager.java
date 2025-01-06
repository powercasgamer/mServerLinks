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
package dev.mizule.mserverlinks.core.dump;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.mizule.mserverlinks.core.Constants;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class DumpManager {

  private final HttpClient client = HttpClient.newHttpClient();
  private final Gson gson;

  public DumpManager() {
    this.gson = new GsonBuilder()
        .setPrettyPrinting()
        .create();

  }

  public CompletableFuture<URI> dump() {
    final JsonObject dump = new JsonObject();
    dump.add("plugin", pluginDump());
    dump.add("environment", environmentDump());

    System.out.println(this.gson.toJson(dump));

    return this.client.sendAsync(
        HttpRequest.newBuilder()
            .POST(HttpRequest.BodyPublishers.ofString(this.gson.toJson(dump)))
            .uri(URI.create("https://api.pastes.dev/post"))
            .header("Content-Type", "text/json")
            .header("User-Agent", "mServerLinks")
            .build(), HttpResponse.BodyHandlers.ofString()
    ).thenApply(response -> {
      if (response.statusCode() == 400) {
        throw new RuntimeException("Failed to dump information to pastes.dev (%s)".formatted(response.statusCode()));
      }

      return URI.create("https://pastes.dev/%s".formatted(response.headers().firstValue("Location").orElseThrow()));
    });
  }

  private JsonObject pluginDump() {
    final JsonObject pluginInfo = new JsonObject();
    pluginInfo.addProperty("name", Constants.NAME);
    final JsonObject versionInfo = new JsonObject();
    versionInfo.addProperty("version", Constants.VERSION);
    versionInfo.addProperty("branch", Constants.GIT_BRANCH);
    versionInfo.addProperty("commit", Constants.GIT_COMMIT);
    versionInfo.addProperty("tag", Constants.GIT_TAG);
    pluginInfo.add("version", versionInfo);

    return pluginInfo;
  }

  private JsonObject environmentDump() {
    JsonObject envInfo = new JsonObject();
    envInfo.addProperty("operatingSystemType", System.getProperty("os.name"));
    envInfo.addProperty("operatingSystemVersion", System.getProperty("os.version"));
    envInfo.addProperty("operatingSystemArchitecture", System.getProperty("os.arch"));
    envInfo.addProperty("javaVersion", System.getProperty("java.version"));
    envInfo.addProperty("javaVendor", System.getProperty("java.vendor"));
    envInfo.addProperty("javaVersion", System.getProperty("java.version"));
    envInfo.addProperty("javaRuntimeName", System.getProperty("java.runtime.name"));
    envInfo.addProperty("javaRuntimeVersion", System.getProperty("java.runtime.version"));
    envInfo.addProperty("javaVmName", System.getProperty("java.vm.name"));
    envInfo.addProperty("javaVmVersion", System.getProperty("java.vm.version"));
    envInfo.addProperty("javaSpecificationVersion", System.getProperty("java.specification.version"));
    envInfo.addProperty("javaSpecificationVendor", System.getProperty("java.specification.vendor"));
    envInfo.addProperty("javaSpecificationName", System.getProperty("java.specification.name"));
    envInfo.addProperty("javaImplementationVersion", System.getProperty("java.implementation.version"));
    envInfo.addProperty("javaImplementationVendor", System.getProperty("java.implementation.vendor"));
    envInfo.addProperty("javaImplementationName", System.getProperty("java.implementation.name"));
    envInfo.addProperty("javaVendorUrl", System.getProperty("java.vendor.url"));
    envInfo.addProperty("javaVendorVersion", System.getProperty("java.vendor.version"));
    return envInfo;
  }
}
