/*
 * This example is from _Java Examples in a Nutshell_. (http://www.oreilly.com)
 * Copyright (c) 1997 by David Flanagan This example is provided WITHOUT ANY
 * WARRANTY either expressed or implied. You may study, use, modify, and
 * distribute it for non-commercial purposes. For any commercial use, see
 * http://www.davidflanagan.com/javaexamples
 */
package gmgen.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

/**
 *  Misc Utilities, all static.  Will create and store a private static instance
 *  of itself that reads in properties and localization strings
 */
public final class MiscUtilities
{
	private MiscUtilities()
	{
	}

	public static void copy(File from_file, File requested_to_file)
			throws IOException
	{
		File to_file = requested_to_file;

		if (!from_file.exists())
		{
			throw new IOException("FileCopy: no such source file: " + from_file.getPath());
		}

		if (!from_file.isFile())
		{
			throw new IOException("FileCopy: can't copy directory: " + from_file.getPath());
		}

		if (!from_file.canRead())
		{
			throw new IOException("FileCopy: source file is unreadable: " + from_file.getPath());
		}

		// If the destination is a directory, use the source file name
		// as the destination file name
		if (to_file.isDirectory())
		{
			to_file = new File(to_file, from_file.getName());
		}

		// If the destination exists, make sure it is a writeable file
		// and ask before overwriting it.  If the destination doesn't
		// exist, make sure the directory exists and is writeable.
		if (to_file.exists())
		{
			if (!to_file.canWrite())
			{
				throw new IOException("FileCopy: destination file is unwriteable: " + to_file.getPath());
			}

			int choice = JOptionPane.showConfirmDialog(null,
					"Overwrite existing file " + to_file.getPath(), "File Exists", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE
			);

			if (choice != JOptionPane.YES_OPTION)
			{
				throw new IOException("FileCopy: existing file was not overwritten.");
			}
		}
		else
		{
			// If file doesn't exist, check if directory exists and is writeable.
			// If getParent() returns null, then the directory is the current dir.
			// so look up the user.dir system property to find out what that is.
			String parent = to_file.getParent(); // Get the destination directory

			if (parent == null)
			{
				parent = Paths.get(".").toAbsolutePath() .toString();
			}

			File dir = new File(parent);

			if (!dir.exists())
			{
				throw new IOException("FileCopy: destination directory doesn't exist: " + parent);
			}

			if (dir.isFile())
			{
				throw new IOException("FileCopy: destination is not a directory: " + parent);
			}

			if (!dir.canWrite())
			{
				throw new IOException("FileCopy: destination directory is unwriteable: " + parent);
			}
		}

		Files.copy(from_file.toPath(), to_file.toPath());
	}
}
