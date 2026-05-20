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
package pcgen.core.namegen;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Smoke tests for {@link NameGenDataLoader} against the bundled
 * {@code plugins/Random Names} dataset.
 */
public class NameGenDataLoaderTest
{
	private static final File DATA_DIR =
			new File(System.getProperty("user.dir"), "plugins/Random Names");

	@Test
	public void loadsBundledDatasetWithoutThrowing() throws IOException
	{
		NameGenData data = NameGenDataLoader.load(DATA_DIR);
		assertNotNull(data);
		assertNotNull(data.allVars());
		assertFalse(data.categories().isEmpty(), "no categories were loaded");
	}

	@Test
	public void categoriesIncludeSexBucketsAndAll() throws IOException
	{
		// Loader-level view: raw categories include the Sex: buckets and
		// the All pseudo-category. The NameGenerator facade hides them;
		// here we just verify the loader sees them.
		NameGenData data = NameGenDataLoader.load(DATA_DIR);
		assertTrue(data.categories().containsKey("All"),
				"loader should expose the 'All' pseudo-category");
		assertTrue(data.categories().keySet().stream().anyMatch(k -> k.startsWith("Sex:")),
				"loader should expose at least one 'Sex:' bucket");
	}

	@Test
	public void rejectsNonDirectory() throws IOException
	{
		File notADir = File.createTempFile("namegen", ".tmp");
		notADir.deleteOnExit();
		assertThrows(IOException.class, () -> NameGenDataLoader.load(notADir));
	}

	@Test
	public void surfacesParseErrorsAsIoException(@TempDir Path tempDir) throws IOException
	{
		// generator.dtd must exist for the resolver, but the actual XML is broken.
		Files.copy(new File(DATA_DIR, "generator.dtd").toPath(),
				tempDir.resolve("generator.dtd"));
		Files.writeString(tempDir.resolve("broken.xml"),
				"<?xml version=\"1.0\"?><!DOCTYPE GENERATOR SYSTEM \"generator.dtd\">"
						+ "<GENERATOR><not-closed></GENERATOR>");
		assertThrows(IOException.class, () -> NameGenDataLoader.load(tempDir.toFile()));
	}

	@Test
	public void emptyDirectoryYieldsEmptyData(@TempDir Path tempDir) throws IOException
	{
		NameGenData data = NameGenDataLoader.load(tempDir.toFile());
		assertTrue(data.categories().isEmpty());
	}
}
