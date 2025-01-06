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
package dev.mizule.mserverlinks.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class mServerLinksProvider {

  private static mServerLinks instance = null;

  public static @NotNull mServerLinks get() {
    mServerLinks instance = mServerLinksProvider.instance;
    if (instance == null) {
      throw new NotLoadedException();
    }
    return instance;
  }

  @ApiStatus.Internal
  static void register(mServerLinks instance) {
    mServerLinksProvider.instance = instance;
  }

  @ApiStatus.Internal
  static void unregister() {
    mServerLinksProvider.instance = null;
  }

  @ApiStatus.Internal
  private mServerLinksProvider() {
    throw new UnsupportedOperationException("This class cannot be instantiated.");
  }

  /**
   * Exception thrown when the API is requested before it has been loaded.
   */
  private static final class NotLoadedException extends IllegalStateException {
    private static final String MESSAGE = "The mServerLinks API isn't loaded yet!\n" +
        "This could be because:\n" +
        "  a) the mServerLinks plugin is not installed or it failed to enable\n" +
        "  b) the plugin in the stacktrace does not declare a dependency on mServerLinks\n" +
        "  c) the plugin in the stacktrace is retrieving the API before the plugin 'enable' phase\n" +
        "     (call the #get method in onEnable, not the constructor!)\n";

    NotLoadedException() {
      super(MESSAGE);
    }
  }

}
