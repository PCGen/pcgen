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
 */
package pcgen.gui3.sources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

/**
 * The source-info pane renders its HTML through a temp file rather than
 * {@code loadContent}, because a document needs a real {@code file:} origin
 * before WebKit will fetch the cover images it embeds.
 *
 * <p>That makes where the file lives a security question rather than a
 * housekeeping one. The file is cached and reused for every campaign the user
 * clicks, so anyone able to write to it between a render and the load that
 * follows chooses what the WebView displays — and the document is deliberately
 * given a {@code file:} origin, with scripting enabled, so what it displays can
 * read local files. The system temp directory is shared by every account on
 * the machine.
 *
 * <p>These tests pin the containing directory's permissions. They are written
 * against the directory rather than the file because a world-writable parent
 * lets an attacker replace the file outright, which no permission on the file
 * itself prevents.
 */
class SourceInfoTempFileTest
{
	private static void assumePosix()
	{
		Assumptions.assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"),
				"POSIX permissions are not a concept on this filesystem");
	}

	@Test
	void privateDirectory_isReadableOnlyByItsOwner() throws IOException
	{
		assumePosix();

		Set<PosixFilePermission> permissions =
				Files.getPosixFilePermissions(SourceInfoRenderer.createPrivateDirectory());

		assertEquals(PosixFilePermissions.fromString("rwx------"), permissions,
				"anyone who can enter the directory can swap the document the WebView loads");
	}

	@Test
	void privateDirectory_isADirectory() throws IOException
	{
		assertTrue(Files.isDirectory(SourceInfoRenderer.createPrivateDirectory()));
	}

	@Test
	void eachCallMakesItsOwnDirectory() throws IOException
	{
		assertNotEquals(SourceInfoRenderer.createPrivateDirectory(),
				SourceInfoRenderer.createPrivateDirectory(),
				"a fixed name would let anyone create the directory first, on their terms");
	}

	/**
	 * The point of the directory: the document the WebView loads must not sit
	 * anywhere another account can write.
	 */
	@Test
	void renderedDocument_sitsInsideAPrivateDirectory() throws IOException
	{
		assumePosix();

		Path file = SourceInfoRenderer.sharedTempFile();

		assertEquals(PosixFilePermissions.fromString("rwx------"),
				Files.getPosixFilePermissions(file.getParent()),
				"the file is only as safe as the directory holding it");
	}

	/**
	 * The assertion that holds on every platform.
	 *
	 * <p>The permission checks above only discriminate where the shared temp
	 * directory is actually shared. On macOS {@code java.io.tmpdir} is already
	 * a per-user directory with {@code rwx------}, so they pass whether or not
	 * the file is placed carefully; on Linux it is {@code /tmp}, mode 1777, and
	 * they bite. Owning the directory is the property the fix really provides,
	 * and it can be asserted anywhere.
	 */
	@Test
	void renderedDocument_doesNotSitDirectlyInTheSharedTempDirectory() throws IOException
	{
		Path shared = Path.of(System.getProperty("java.io.tmpdir")).toRealPath();

		Path parent = SourceInfoRenderer.sharedTempFile().toRealPath().getParent();

		assertNotEquals(shared, parent,
				"the document must live in a directory this process made, not one anybody may write to");
	}

	@Test
	void renderedDocument_isReusedBetweenRenders() throws IOException
	{
		assertSame(SourceInfoRenderer.sharedTempFile(), SourceInfoRenderer.sharedTempFile(),
				"a new file per render would leak one temp directory per click");
	}

	@Test
	void renderedDocument_isWritable() throws IOException
	{
		Path file = SourceInfoRenderer.sharedTempFile();

		Files.writeString(file, "<html></html>");

		assertTrue(Files.isReadable(file));
	}
}
