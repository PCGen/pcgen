/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package pcgen.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests how {@link Logging} locates its {@code logging.properties}
 * configuration file.
 */
class LoggingConfigTest
{
	private static Path writeConfig(Path dir) throws IOException
	{
		Files.createDirectories(dir);
		Path file = dir.resolve("logging.properties");
		Files.writeString(file, ".level = INFO\n");
		return file;
	}

	private static void assertFound(Path expected, Optional<File> found) throws IOException
	{
		assertEquals(expected.toFile().getCanonicalFile(), found.orElseThrow().getCanonicalFile());
	}

	@Test
	void findsConfigInWorkingDirectory(@TempDir Path workingDir) throws IOException
	{
		Path expected = writeConfig(workingDir);

		assertFound(expected, Logging.findLoggingConfig(workingDir.toString(), null));
	}

	/**
	 * A packaged launch leaves the working directory as "/", so the install
	 * directory has to be derived from java.home instead. Without this the JDK
	 * default logging configuration was silently used, losing the formatter,
	 * the pcgen.log recorder and the contents of the Debug dialog.
	 *
	 * @param layout the packaging layout being exercised
	 * @param javaHomeDir java.home, relative to the install image
	 * @param configDir directory holding logging.properties, relative to the image
	 * @param image the install image root
	 * @throws IOException if the layout cannot be created
	 */
	@ParameterizedTest(name = "{0}")
	@CsvSource({
		"portable image, runtime, ''",
		"linux and windows bundle, lib/runtime, lib/app",
		"mac bundle, Contents/runtime/Contents/Home, Contents/app"
	})
	void findsConfigFromJavaHome(String layout, String javaHomeDir, String configDir, @TempDir Path image)
			throws IOException
	{
		Path expected = writeConfig(image.resolve(configDir));
		Path javaHome = Files.createDirectories(image.resolve(javaHomeDir));

		assertFound(expected, Logging.findLoggingConfig(null, javaHome.toString()));
	}

	/** The working directory wins, so a dev or portable run overrides the install. */
	@Test
	void prefersTheWorkingDirectory(@TempDir Path workingDir, @TempDir Path image) throws IOException
	{
		Path expected = writeConfig(workingDir);
		writeConfig(image);
		Path javaHome = Files.createDirectories(image.resolve("runtime"));

		assertFound(expected, Logging.findLoggingConfig(workingDir.toString(), javaHome.toString()));
	}

	@Test
	void returnsEmptyWhenNothingIsFound(@TempDir Path empty)
	{
		assertTrue(Logging.findLoggingConfig(empty.toString(), empty.toString()).isEmpty());
	}
}
