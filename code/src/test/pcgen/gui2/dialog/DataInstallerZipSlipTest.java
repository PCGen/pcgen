/*
 * Copyright 2026 Vest <Vest@users.noreply.github.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package pcgen.gui2.dialog;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression tests for the Zip Slip fix in {@link DataInstaller}. Reflection
 * is used so the same test class compiles against both the pre-fix
 * (private, no checked exception) and post-fix (package-private,
 * throws IOException) signatures of {@code correctFileName}, which lets
 * the test be exercised against either code state during the
 * before/after verification dance.
 */
class DataInstallerZipSlipTest
{
	private static String invokeCorrectFileName(File destDir, String entry) throws IOException
	{
		try
		{
			Method m = DataInstaller.class.getDeclaredMethod(
					"correctFileName", File.class, String.class);
			m.setAccessible(true);
			return (String) m.invoke(null, destDir, entry);
		}
		catch (InvocationTargetException e)
		{
			Throwable cause = e.getCause();
			if (cause instanceof IOException ioe)
			{
				throw ioe;
			}
			throw new RuntimeException(cause);
		}
		catch (NoSuchMethodException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Test
	void benignEntryStaysUnderDestDir(@TempDir Path destDirPath) throws IOException
	{
		File destDir = destDirPath.toFile();

		String resolved = invokeCorrectFileName(destDir, "data/sub/file.lst");

		Path resolvedPath = new File(resolved).getCanonicalFile().toPath();
		Path baseCanonical = destDir.getCanonicalFile().toPath();
		assertTrue(resolvedPath.startsWith(baseCanonical),
				"benign entry should resolve under destDir, got: " + resolvedPath);
	}

	@Test
	void parentSegmentEscapeIsRejected(@TempDir Path destDirPath)
	{
		File destDir = destDirPath.toFile();

		assertThrows(IOException.class,
				() -> invokeCorrectFileName(destDir, "data/../../escape.txt"));
	}

	@Test
	void buriedParentSegmentEscapeIsRejected(@TempDir Path destDirPath)
	{
		File destDir = destDirPath.toFile();

		// Even when the '..' segments only escape after several levels of
		// traversal, the canonical comparison should still catch them.
		assertThrows(IOException.class,
				() -> invokeCorrectFileName(destDir, "data/foo/bar/../../../../escape.txt"));
	}
}
