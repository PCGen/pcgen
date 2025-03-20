/*
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
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
 *
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.LoadContext;

/**
 * This class is an extension of the LstFileLoader that loads items
 * that are typically not PObjects, or are PObjects but do not have
 * a campaign associated with them.  System items such as paper size,
 * size adjustments, etc. are extensions of this class because they
 * do not need the MOD/COPY/FORGET funcationality of core PObjects used
 * to directly create characters.
 *
 * <p>
 *
 */
public abstract class LstLineFileLoader extends Observable
{
	/**
	 * Stores what game mode the objects loaded by this loader should be
	 * associated with.
	 */
	// TODO - Should be a constant.
	protected String gameMode = "*"; //$NON-NLS-1$

	/**
	 * This method loads a single LST formatted file.
	 *
	 * @param context the context
	 * @param uri String containing the absolute file path
	 * or the URL from which to read LST formatted data.
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void loadLstFile(LoadContext context, URI uri) throws PersistenceLayerException
	{
		String dataBuffer = LstFileLoader.readFromURI(uri)
				.orElseThrow(() -> new PersistenceLayerException("Failed to read from URI: " + uri));
		if (context != null)
		{
			context.setSourceURI(uri);
		}
		loadLstString(context, uri, dataBuffer);
	}

	/**
	 * This method loads a single LST formatted file.
	 *
	 * @param context the context
	 * @param uri String containing the absolute file path
	 * or the URL from which the LST formatted data was read.
	 * @param aString The LST formatted data
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void loadLstString(LoadContext context, URI uri, final String aString) throws PersistenceLayerException
	{
		final String newlinedelim = "\r\n";
		final StringTokenizer fileLines = new StringTokenizer(aString, newlinedelim);

		while (fileLines.hasMoreTokens())
		{
			String line = fileLines.nextToken().trim();

			// check for comments and blank lines
			if ((line.isEmpty()) || (line.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR))
			{
				continue;
			}

			parseLine(context, line, uri);
		}
	}

	/**
	 * This method loads a single LST formatted file in a game mode file.
	 *
	 * @param context the context
	 * @param fileName String containing the absolute file path
	 * @param game the game mode
	 * @throws PersistenceLayerException the persistence layer exception
	 */
	public void loadLstFile(LoadContext context, URI fileName, String game) throws PersistenceLayerException
	{
		gameMode = game;
		loadLstFile(context, fileName);
	}

	/**
	 * This method loads the given list of LST files.
	 * @param fileList containing the list of files to read
	 * @throws PersistenceLayerException if there is a problem with the
	 *         LST syntax
	 */
	public void loadLstFiles(LoadContext context, List<CampaignSourceEntry> fileList) throws PersistenceLayerException
	{
		// Track which sources have been loaded already
		Set<CampaignSourceEntry> loadedFiles = new HashSet<>();

		// Load the files themselves as thoroughly as possible
		for (CampaignSourceEntry cse : fileList)
		{
			// Check if the CSE has already been loaded before loading it
			if (!loadedFiles.contains(cse))
			{
				loadLstFile(context, cse.getURI());
				loadedFiles.add(cse);
			}
		}
	}

	/**
	 * This method parses the LST file line, applying it to the provided target
	 * object.  If the line indicates the start of a new target object, a new
	 * PObject of the appropriate type will be created prior to applying the
	 * line contents.  Because of this behavior, it is necessary for this
	 * method to return the new object.  Implementations of this method also
	 * MUST call finishObject with the original target prior to returning the
	 * new value.
	 *
	 * @param lstLine String LST formatted line read from the source URL
	 * @param sourceURI URI that the line was read from, for error reporting
	 *         purposes
	 * @throws PersistenceLayerException if there is a problem with the LST syntax
	 */
	public abstract void parseLine(LoadContext context, String lstLine, URI sourceURI) throws PersistenceLayerException;

	/**
	 * @return Returns the gameMode.
	 */
	public String getGameMode()
	{
		return gameMode;
	}

	/**
	 * @param gameMode The gameMode to set.
	 */
	public void setGameMode(String gameMode)
	{
		this.gameMode = gameMode;
	}
}
