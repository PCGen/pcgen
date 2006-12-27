/*
 * LstLineFileLoader.java
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
 * Created on November 17, 2003, 12:00 PM
 *
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 */
package pcgen.persistence.lst;

import pcgen.persistence.PersistenceLayerException;

import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class is an extension of the LstFileLoader that loads items
 * that are typically not PObjects, or are PObjects but do not have
 * a campaign associated with them.  System items such as paper size,
 * size adjustments, etc. are extensions of this class because they
 * do not need the MOD/COPY/FORGET funcationality of core PObjects used
 * to directly create characters.
 *
 * <p>
 * Current Ver: $Revision$ <br>
 * Last Editor: $Author$ <br>
 * Last Edited: $Date$
 *
 * @author sage_sam
 */
public abstract class LstLineFileLoader extends LstFileLoader
{
	/** 
	 * Stores what game mode the objects loaded by this loader should be 
	 * associated with.
	 */
	// TODO - Should be a constant.
	protected String gameMode = "*"; //$NON-NLS-1$

	/**
	 * Constructor
	 */
	public LstLineFileLoader()
	{
		super();
	}

	/**
	 * This method loads a single LST formatted file.
	 * @param fileName String containing the absolute file path
	 * or the URL from which to read LST formatted data.
	 * @throws PersistenceLayerException
	 */
	public void loadLstFile(String fileName) throws PersistenceLayerException
	{
		StringBuffer dataBuffer = new StringBuffer();

		URL fileURL = readFileGetURL(fileName, dataBuffer);

		if (fileURL == null)
		{
			throw new PersistenceLayerException("File '" + fileName
				+ "' not found!");
		}

		final String newlinedelim = "\r\n";
		final String aString = dataBuffer.toString();
		final StringTokenizer fileLines =
				new StringTokenizer(aString, newlinedelim);

		while (fileLines.hasMoreTokens())
		{
			String line = fileLines.nextToken().trim();

			// check for comments and blank lines
			if (isComment(line))
			{
				continue;
			}

			parseLine(line, fileURL);
		}
	}

	/**
	 * This method loads a single LST formatted file in a game mode file.
	 * @param fileName String containing the absolute file path
	 * @param game the game mode
	 * @throws PersistenceLayerException
	 */
	public void loadLstFile(String fileName, String game)
		throws PersistenceLayerException
	{
		gameMode = game;
		loadLstFile(fileName);
	}

	/**
	 * This method loads the given list of LST files.
	 * @param fileList containing the list of files to read
	 * @throws PersistenceLayerException if there is a problem with the
	 *         LST syntax
	 */
	public void loadLstFiles(List<?> fileList) throws PersistenceLayerException
	{
		// Load the files themselves as thoroughly as possible
		for (Object cse : fileList)
		{
			loadLstFile(cse.toString());
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
	 * @param sourceURL URL that the line was read from, for error reporting
	 *         purposes
	 * @throws PersistenceLayerException if there is a problem with the LST syntax
	 */
	public abstract void parseLine(String lstLine, URL sourceURL)
		throws PersistenceLayerException;

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
