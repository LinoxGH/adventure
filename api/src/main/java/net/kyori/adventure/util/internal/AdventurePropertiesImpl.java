/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2021 KyoriPowered
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
package net.kyori.adventure.util.internal;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class AdventurePropertiesImpl {
  private static final String FILESYSTEM_DIRECTORY_NAME = "config";
  private static final String FILESYSTEM_FILE_NAME = "adventure.properties";
  private static final Properties PROPERTIES = new Properties();

  static {
    final Path path = Paths.get(FILESYSTEM_DIRECTORY_NAME, FILESYSTEM_FILE_NAME);
    if (Files.isRegularFile(path)) {
      try (final InputStream is = Files.newInputStream(path)) {
        PROPERTIES.load(is);
      } catch (final IOException e) {
        // Well, that's awkward.
        e.printStackTrace();
      }
    }
  }

  private AdventurePropertiesImpl() {
  }

  static final class PropertyImpl<T> implements AdventureProperties.Property<T> {
    private final String name;
    private final Function<String, T> parser;
    private final @Nullable T defaultValue;

    PropertyImpl(final @NotNull String name, final @NotNull Function<String, T> parser, final @Nullable T defaultValue) {
      this.name = name;
      this.parser = parser;
      this.defaultValue = defaultValue;
    }

    @Override
    public @Nullable T value() {
      final String property = String.join(".", "net", "kyori", "adventure", this.name);
      final String value = System.getProperty(property, PROPERTIES.getProperty(this.name));
      if (value != null) {
        return this.parser.apply(value);
      }
      return this.defaultValue;
    }

    @Override
    public boolean equals(final @Nullable Object that) {
      return this == that;
    }

    @Override
    public int hashCode() {
      return this.name.hashCode();
    }
  }
}
