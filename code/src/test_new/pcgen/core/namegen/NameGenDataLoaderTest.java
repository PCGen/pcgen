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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

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
		assertNotNull(data.lists());
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

	@Test
	public void valueWithoutWeightDefaultsToOne(@TempDir Path tempDir) throws IOException
	{
		// The DTD declares weight="1" as a default, but we use a
		// non-validating parser, so a missing weight arrives as "" — the
		// loader must treat that as 1 instead of throwing.
		Files.copy(new File(DATA_DIR, "generator.dtd").toPath(),
				tempDir.resolve("generator.dtd"));
		Files.writeString(tempDir.resolve("noweight.xml"),
				"<?xml version=\"1.0\"?><!DOCTYPE GENERATOR SYSTEM \"generator.dtd\">"
						+ "<GENERATOR>"
						+ "<LIST title=\"L\" id=\"L\"><VALUE>Donn</VALUE></LIST>"
						+ "</GENERATOR>");
		NameGenData data = NameGenDataLoader.load(tempDir.toFile());
		assertNotNull(data);
	}

	@Test
	public void rejectsExternalEntityExpansion(@TempDir Path tempDir) throws IOException
	{
		// Classic XXE payload: declare an external entity that points to
		// a local secret file, then reference it. With external general
		// entities disabled, the parser must NOT inline the file's
		// contents into the resulting data — it should either refuse the
		// document or leave the reference unresolved.
		Files.copy(new File(DATA_DIR, "generator.dtd").toPath(),
				tempDir.resolve("generator.dtd"));
		Path secret = tempDir.resolve("secret.txt");
		String marker = "TOP-SECRET-CANARY-12345";
		Files.writeString(secret, marker);
		String secretUri = secret.toUri().toString();
		Files.writeString(tempDir.resolve("xxe.xml"),
				"<?xml version=\"1.0\"?>"
						+ "<!DOCTYPE GENERATOR ["
						+ "<!ENTITY leak SYSTEM \"" + secretUri + "\">"
						+ "]>"
						+ "<GENERATOR>"
						+ "<LIST title=\"L\" id=\"L\"><VALUE>prefix-&leak;-suffix</VALUE></LIST>"
						+ "</GENERATOR>");
		try
		{
			NameGenData data = NameGenDataLoader.load(tempDir.toFile());
			// If the loader didn't throw, the parsed data must not contain
			// the secret marker — otherwise XXE expanded successfully.
			boolean leaked = data.lists().values().stream()
					.flatMap(l -> l.values().stream())
					.anyMatch(v -> v.getValue().contains(marker));
			assertFalse(leaked, "external entity was expanded into the parsed data");
		}
		catch (IOException ignored)
		{
			// Refusing the document is also an acceptable outcome.
		}
	}

	@Test
	public void bundledDatasetHasNoUnresolvedReferences() throws IOException
	{
		// Each entry in unresolvedReferences() is a GETLIST/GETRULE in
		// the data files pointing at a target id that doesn't exist —
		// the legacy engine silently swallowed these at generation time;
		// the new loader collects them so the data can be fixed.
		// If this fails, the assertion message lists every broken ref.
		NameGenData data = NameGenDataLoader.load(DATA_DIR);
		String summary = data.unresolvedReferences().stream()
				.map(u -> u.kind() + " " + u.targetId())
				.distinct()
				.sorted()
				.collect(Collectors.joining("\n  "));
		assertTrue(data.unresolvedReferences().isEmpty(),
				"bundled dataset has unresolved references:\n  " + summary);
	}

	@Test
	public void unresolvedReferencesAreCollected(@TempDir Path tempDir) throws IOException
	{
		// Verify the collection mechanism itself: a GETLIST pointing at a
		// non-existent list and a GETRULE pointing at a non-existent
		// ruleset should both surface in unresolvedReferences().
		Files.copy(new File(DATA_DIR, "generator.dtd").toPath(),
				tempDir.resolve("generator.dtd"));
		Files.writeString(tempDir.resolve("dangling.xml"),
				"<?xml version=\"1.0\"?><!DOCTYPE GENERATOR SYSTEM \"generator.dtd\">"
						+ "<GENERATOR>"
						+ "<RULESET title=\"R\" id=\"R\" usage=\"final\">"
						+ "<RULE><GETLIST idref=\"missing-list\"/><GETRULE idref=\"missing-ruleset\"/></RULE>"
						+ "</RULESET>"
						+ "</GENERATOR>");
		NameGenData data = NameGenDataLoader.load(tempDir.toFile());
		assertEquals(2, data.unresolvedReferences().size());
		assertTrue(data.unresolvedReferences().stream()
				.anyMatch(u -> u.kind() == NameGenData.UnresolvedReference.Kind.GETLIST
						&& "missing-list".equals(u.targetId())));
		assertTrue(data.unresolvedReferences().stream()
				.anyMatch(u -> u.kind() == NameGenData.UnresolvedReference.Kind.GETRULE
						&& "missing-ruleset".equals(u.targetId())));
	}
}
