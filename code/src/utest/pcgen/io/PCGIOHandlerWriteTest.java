/*
 * Copyright 2026 (C) Vest <Vest@users.noreply.github.com>
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
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * PCGIOHandlerWriteTest checks the party-file format produced by
 * {@link PCGIOHandler#write(File, List)}.
 */
class PCGIOHandlerWriteTest
{

	/**
	 * Test method for {@link PCGIOHandler#write(File, List)}: verifies that the
	 * resulting file contains a VERSION line followed by a comma-separated list
	 * of character file paths.
	 */
	@Test
	public void testWriteProducesVersionAndFilesLines(@TempDir Path tmp) throws IOException
	{
		File party = tmp.resolve("test.pcp").toFile();
		File charA = tmp.resolve("a.pcg").toFile();
		File charB = tmp.resolve("b.pcg").toFile();
		Files.writeString(charA.toPath(), "x");
		Files.writeString(charB.toPath(), "y");

		PCGIOHandler.write(party, List.of(charA, charB));

		List<String> lines = Files.readAllLines(party.toPath(), StandardCharsets.UTF_8);
		assertEquals(2, lines.size(), "expected exactly two lines");
		assertTrue(lines.get(0).startsWith("VERSION:"), "first line should start with VERSION:");
		assertTrue(lines.get(1).contains("a.pcg"), "second line should reference a.pcg");
		assertTrue(lines.get(1).contains("b.pcg"), "second line should reference b.pcg");
		assertTrue(lines.get(1).contains(","), "character paths should be comma-separated");
	}

	/**
	 * Test method for {@link PCGIOHandler#write(File, List)}: verifies that the
	 * file ends with the platform line separator.
	 */
	@Test
	public void testWriteEndsWithLineSeparator(@TempDir Path tmp) throws IOException
	{
		File party = tmp.resolve("test.pcp").toFile();
		File charA = tmp.resolve("a.pcg").toFile();
		Files.writeString(charA.toPath(), "x");

		PCGIOHandler.write(party, List.of(charA));

		String content = Files.readString(party.toPath(), StandardCharsets.UTF_8);
		assertTrue(content.endsWith(System.lineSeparator()),
				"file should end with the platform line separator");
	}

	/**
	 * Test method for {@link PCGIOHandler#write(File, List)}: verifies that the
	 * file is encoded in UTF-8 by round-tripping a non-ASCII filename.
	 */
	@Test
	public void testWriteIsUtf8Encoded(@TempDir Path tmp) throws IOException
	{
		File party = tmp.resolve("test.pcp").toFile();
		File charA = tmp.resolve("é.pcg").toFile();
		Files.writeString(charA.toPath(), "x");

		PCGIOHandler.write(party, List.of(charA));

		byte[] bytes = Files.readAllBytes(party.toPath());
		String asUtf8 = new String(bytes, StandardCharsets.UTF_8);
		assertTrue(asUtf8.contains("é.pcg"),
				"non-ASCII filename should be readable when decoded as UTF-8");
	}
}
