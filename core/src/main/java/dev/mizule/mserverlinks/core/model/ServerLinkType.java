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
package dev.mizule.mserverlinks.core.model;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ServerLinkType {

  public static final ServerLinkType BUG_REPORT = new ServerLinkType("BUG_REPORT", "REPORT_BUG");
  public static final ServerLinkType COMMUNITY_GUIDELINES = new ServerLinkType("COMMUNITY_GUIDELINES");
  public static final ServerLinkType SUPPORT = new ServerLinkType("SUPPORT");
  public static final ServerLinkType STATUS = new ServerLinkType("STATUS");
  public static final ServerLinkType FEEDBACK = new ServerLinkType("FEEDBACK");
  public static final ServerLinkType COMMUNITY = new ServerLinkType("COMMUNITY");
  public static final ServerLinkType WEBSITE = new ServerLinkType("WEBSITE");
  public static final ServerLinkType FORUMS = new ServerLinkType("FORUMS");
  public static final ServerLinkType NEWS = new ServerLinkType("NEWS");
  public static final ServerLinkType ANNOUNCEMENTS = new ServerLinkType("ANNOUNCEMENTS");
  public static final ServerLinkType CUSTOM = new ServerLinkType("CUSTOM");

  private final String name;
  private final String[] aliases;

  private ServerLinkType(final String name, final String... aliases) {
    this.name = name;
    this.aliases = aliases != null ? aliases : new String[0];
  }

  public static ServerLinkType[] values() {
    return new ServerLinkType[]{
        BUG_REPORT,
        COMMUNITY_GUIDELINES,
        SUPPORT,
        STATUS,
        FEEDBACK,
        COMMUNITY,
        WEBSITE,
        FORUMS,
        NEWS,
        ANNOUNCEMENTS
    };
  }

  public static ServerLinkType fromName(String name) {
    for (ServerLinkType type : values()) {
      if (type.getName().equalsIgnoreCase(name)) {
        return type;
      }
      for (String alias : type.getAliases()) {
        if (alias.equalsIgnoreCase(name)) {
          return type;
        }
      }
    }
    throw new IllegalArgumentException("No type with name or alias " + name);
  }

  public String getName() {
    return name;
  }

  public String[] getAliases() {
    return aliases;
  }

  @Override
  public String toString() {
    return name;
  }
}
