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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.asyncFx;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javafx.scene.web.WebEngine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

/** Verifies that preview documents live in private directories that other accounts cannot replace. */
class SourceInfoTempFileTest
{
	private final List<Path> directories = new ArrayList<>();

	private Path newDirectory() throws IOException
	{
		Path directory = SourceInfoRenderer.createPrivateDirectory();
		directories.add(directory);
		return directory;
	}

	@AfterEach
	void deleteDirectories() throws IOException
	{
		for (Path directory : directories)
		{
			Files.deleteIfExists(directory);
		}
	}

	private static void assumePosix()
	{
		Assumptions.assumeTrue(FileSystems.getDefault().supportedFileAttributeViews().contains("posix"),
				"POSIX permissions are not a concept on this filesystem");
	}

	@Test
	void privateDirectory_isReadableOnlyByItsOwner() throws IOException
	{
		assumePosix();

		assertEquals(PosixFilePermissions.fromString("rwx------"), Files.getPosixFilePermissions(newDirectory()));
	}

	@Test
	void eachCallMakesItsOwnDirectory() throws IOException
	{
		Path first = newDirectory();
		Path second = newDirectory();
		assertTrue(Files.isDirectory(first));
		assertTrue(Files.isDirectory(second));
		assertNotEquals(first, second, "each preview needs a directory this process created");
	}

	/** Only the renderer integration requires a JavaFX toolkit. */
	@Nested
	@ExtendWith(ApplicationExtension.class)
	class RenderedDocument
	{
		private SourceInfoRenderer renderer;

		@BeforeEach
		void createRenderer() throws Exception
		{
			renderer = asyncFx(() -> new SourceInfoRenderer(new WebEngine())).get(5, TimeUnit.SECONDS);
		}

		@AfterEach
		void closeRenderer() throws Exception
		{
			asyncFx(renderer::close).get(5, TimeUnit.SECONDS);
		}

		@Test
		void document_sitsInsideAPrivateDirectory() throws IOException
		{
			assumePosix();

			assertEquals(PosixFilePermissions.fromString("rwx------"),
					Files.getPosixFilePermissions(renderer.documentFile().getParent()));
		}

		@Test
		void document_isWritableOutsideTheSharedTempDirectory() throws IOException
		{
			Path file = renderer.documentFile();
			Path shared = Path.of(System.getProperty("java.io.tmpdir")).toRealPath();
			assertNotEquals(shared, file.toRealPath().getParent(),
					"a shared parent would let another account replace the document");

			renderer.writeDocument("<html></html>");
			assertEquals("<html></html>", Files.readString(file));
		}
	}
}
