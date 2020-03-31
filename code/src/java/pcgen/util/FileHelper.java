/**
 * Copyright 2003 (C) John Watson <john@sleazyweasel.com>
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
 *
 */
package pcgen.util;

import java.io.File;
import java.io.IOException;

/**
 * Assorted path and filename methods.
 *
 **/
public final class FileHelper
{
	/** Private constructor to disable instantiation. */
	private FileHelper()
	{
	}

	/**
	 * Find the relative path between two files.
	 * @param base The base file.
	 * @param relative The file to be made relative. 
	 * @return relative path 
	 */
	public static String findRelativePath(File base, File relative)
	{
		File testFile;
		File relativeCanon;
		try
		{
			testFile = base.getCanonicalFile();
			relativeCanon = relative.getCanonicalFile();
		}
		catch (IOException e)
		{
			// The file can't be worked with so return it as is.
			return relative.getAbsolutePath();
		}
		if (!testFile.isDirectory())
		{
			testFile = testFile.getParentFile();
		}

		// Cope with files on different drives in Windows.
		if (!testFile.toPath().getRoot().equals(relativeCanon.toPath().getRoot()))
		{
			return relativeCanon.getAbsolutePath();
		}

		String relativePath = stripOffRoot(relativeCanon);

		StringBuilder dots = new StringBuilder();

		do
		{
			if (testFile.getParentFile() == null)
			{
				//we're at the root...
				return dots.append(relativePath).toString();
			}

			String testPath = stripOffRoot(testFile);

			if (relativePath.indexOf(testPath) == 0)
			{
				if (testPath.length() >= relativePath.length())
				{
					Logging.log(Logging.WARNING,
						"Unable to get path for " + relative + " relative to " + base + ". Using absolute path.");
					return relative.getAbsolutePath();
				}
				String pieceToKeep = relativePath.substring(testPath.length() + 1);

				return dots.append(pieceToKeep).toString();
			}
			dots.append("..").append(File.separator);

			testFile = testFile.getParentFile();
		}
		while (testFile != null);

		return dots + relativePath;
	}

	private static String stripOffRoot(File relative)
	{
		String root = relative.toPath().getRoot().toString();

		return relative.getAbsolutePath().substring(root.length());
	}
}
