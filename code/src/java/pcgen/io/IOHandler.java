/*
 * IOHandler.java
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
 * Created on March 11, 2002, 8:30 PM
 */
package pcgen.io;

import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

import java.io.*;
import java.util.List;
import pcgen.core.Campaign;
import pcgen.core.GameMode;

/**
 * <code>IOHandler</code><br>
 * Abstract IO handler class<br>
 * An IO handler is responsible for reading and/or writing 
 * PlayerCharacters in a specific format from/to a stream
 *
 * @author Thomas Behr 11-03-02
 * @version $Revision$
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
	public final void readForPreview(final PlayerCharacter aPC,
		final String path)
	{
		internalRead(aPC, path, false);
	}

	private void internalRead(final PlayerCharacter aPC, final String path,
		final boolean validate)
	{
		InputStream in = null;

		try
		{
			in = new FileInputStream(path);
			read(aPC, in, validate);
		}
		catch (IOException ex)
		{
			Logging.errorPrint("Exception in IOHandler::read when reading", ex);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					Logging.errorPrint("Exception in IOHandler::read", e);
				}
				catch (NullPointerException e)
				{
					Logging.errorPrint(
						"Could not create file inputStream IOHandler::read", e);
				}
			}
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
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public final void write(PlayerCharacter aPC, String filename)
			throws IOException, NullPointerException
	{
		write(aPC, null, null, filename);
	}

	/**
	 * Writes the contents of the PlayerCharacter to a file.
	 *
	 * <br>author: Thomas Behr 11-03-02
	 *
	 * @param aPC        the PlayerCharacter to write
	 * @param filename   the name of the output file
	 * @throws IOException
	 * @throws NullPointerException
	 */
	public final void write(PlayerCharacter aPC, GameMode mode, List<Campaign> campaigns, String filename)
		throws IOException, NullPointerException
	{
		OutputStream out = null;
		File bakFile = null;

		try
		{
			// Make a backup of the old file, if it exists and isn't empty
			File outFile = new File(filename);
			if (SettingsHandler.getCreatePcgBackup() && outFile.exists()
				&& outFile.length() > 0)
			{
				String file = outFile.getName();
				File backupPcgPath = SettingsHandler.getBackupPcgPath();
				if (backupPcgPath != null && !backupPcgPath.getPath().equals(""))
				{
					bakFile =
							new File(backupPcgPath + File.separator + file
								+ ".bak");
				}
				else
				{
					bakFile = new File(filename + ".bak");
				}
				if (bakFile.exists())
				{
					bakFile.delete();
				}
				outFile.renameTo(bakFile);
			}
			outFile = null;

			out = new FileOutputStream(filename);
			write(aPC, mode, campaigns, out);
		}
		catch (IOException ex)
		{
			Logging
				.errorPrint("Exception in IOHandler::write when writing", ex);
			throw ex;
		}
		finally
		{
			if (out != null)
			{
				try
				{
					out.flush();
					out.close();
				}
				catch (IOException e)
				{
					Logging.errorPrint("Exception in IOHandler::write", e);
					throw e;
				}
				catch (NullPointerException e)
				{
					Logging
						.errorPrint(
							"Could not create FileOutputStream in IOHandler::write",
							e);
					throw e;
				}
			}
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
	protected abstract void read(PlayerCharacter aPC, InputStream in,
		final boolean validate);

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
