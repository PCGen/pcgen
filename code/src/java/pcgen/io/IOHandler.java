/*
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
package pcgen.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import pcgen.core.Campaign;
import pcgen.core.GameMode;
import pcgen.core.PlayerCharacter;
import pcgen.system.PCGenSettings;
import pcgen.util.Logging;

/**
 * {@code IOHandler}<br>
 * Abstract IO handler class<br>
 * An IO handler is responsible for reading and/or writing 
 * PlayerCharacters in a specific format from/to a stream
 *
 */
public abstract class IOHandler
{
	/**
	 * Fills the contents of the given graph from a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC        the PlayerCharacter to store the read data
	 * @param path   the name of the input file, i.e. the file to be read
	 */
	public final void read(PlayerCharacter aPC, String path)
	{
		internalRead(aPC, path, true);
	}

	/**
	 * Reads a player character from a character (PCG) file suitable for
	 * preview.
	 *
	 * @param aPC a player character
	 *
	 * @param path a character (PCG) file path
	 */
	public final void readForPreview(final PlayerCharacter aPC, final String path)
	{
		internalRead(aPC, path, false);
	}

	private void internalRead(final PlayerCharacter aPC, final String path, final boolean validate)
	{
		try (InputStream inputStream = new FileInputStream(path))
		{
			read(aPC, inputStream, validate);
		}
		catch (IOException ex)
		{
			Logging.errorPrint("Exception in IOHandler::read when reading", ex);
		}
	}

	/////////////////////////////////////////////////////////////////////////////
	////////////////////////////// Convenience //////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the contents of the PlayerCharacter to a file.
	 * 
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC        the PlayerCharacter to write
	 * @param filename   the name of the output file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final void write(PlayerCharacter aPC, String filename) throws FileNotFoundException, IOException
	{
		write(aPC, null, null, filename);
	}

	/**
	 * Writes the contents of the PlayerCharacter to a file.
	 *
	 * @param aPC        the PlayerCharacter to write
	 * @param mode  the mode
	 * @param campaigns  the campaigns
	 * @param filename   the name of the output file
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public final void write(PlayerCharacter aPC, GameMode mode, List<Campaign> campaigns, String filename)
			throws FileNotFoundException, IOException
		
	{
		try (OutputStream out = new FileOutputStream(filename))
		{
			File outFile = new File(filename);
			createBackupForFile(outFile);
			write(aPC, mode, campaigns, out);
		}
	}

	/**
	 * Create a backup of the specified file, but only if backups are enabled, 
	 * the file exists and the file is not empty.
	 *  
	 * @param outFile The file to be backed up.
	 */
	public void createBackupForFile(File outFile)
	{
		// Make a backup of the old file, if it exists and isn't empty
		if (PCGenSettings.getCreatePcgBackup() && outFile.exists() && outFile.length() > 0)
		{
			String file = outFile.getName();
			String backupPcgPath = PCGenSettings.getBackupPcgDir();
			if (backupPcgPath == null || backupPcgPath.isEmpty())
			{
				backupPcgPath = outFile.getParent();
			}
			final String BAK_PREFIX = ".bak"; //$NON-NLS-1$
			File bakFile = new File(backupPcgPath, file + BAK_PREFIX);

			if (bakFile.exists() && outFile.exists() && outFile.length() > 0)
			{
				bakFile.delete();
			}
			outFile.renameTo(bakFile);
		}
	}

	/**
	 * Reads the contents of the given PlayerCharacter from a stream
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to store the read data
	 * @param in    the stream to be read from
	 * @param validate
	 */
	protected abstract void read(PlayerCharacter aPC, InputStream in, boolean validate);

	/////////////////////////////////////////////////////////////////////////////
	////////////////////////////// Abstract /////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////

	/**
	 * Writes the contents of the given PlayerCharacter to a stream
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC   the PlayerCharacter to write
	 * @param out   the stream to be written to
	 */
	protected abstract void write(PlayerCharacter aPC, GameMode mode, List<Campaign> campaigns, OutputStream out);
}
