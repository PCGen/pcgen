/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2008-10 (C) Tom Parker <thpr@users.sourceforge.net>
 * Copyright 2003 (C) David Hibbs <sage_sam@users.sourceforge.net>
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.cdom.content.DatasetVariable;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.rules.context.LoadContext;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class VariableLoader extends Observable
{

	public final void parseLine(LoadContext context, String lstLine, SourceEntry source)
	{
		final StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);

		//Need the IF so that it is not an empty line causing issues
		if (colToken.hasMoreTokens())
		{
			String tok = colToken.nextToken();
			if (tok.indexOf(':') == -1)
			{
				tok = "GLOBAL:" + tok;
			}
			DatasetVariable po = new DatasetVariable();
			boolean success = LstUtils.processToken(context, po, source, tok);
			if (!success)
			{
				Logging.errorPrint("Failed to parse first token on Variable Line: " + "ignoring rest of line");
				return;
			}
			po.setSourceURI(source.getURI());
			while (colToken.hasMoreTokens())
			{
				LstUtils.processToken(context, po, source, colToken.nextToken());
			}
		}
	}

	/**
	 * This method loads the given list of LST files.
	 *
	 * @param context the context
	 * @param fileList containing the list of files to read.
	 */
	public void loadLstFiles(LoadContext context, List<CampaignSourceEntry> fileList)
	{
		// Track which sources have been loaded already
		Set<CampaignSourceEntry> loadedFiles = new HashSet<>();

		// Load the files themselves as thoroughly as possible
		for (CampaignSourceEntry sourceEntry : fileList)
		{
			// Check if the CSE has already been loaded before loading it
			if (!loadedFiles.contains(sourceEntry))
			{
				loadLstFile(context, sourceEntry);
				loadedFiles.add(sourceEntry);
			}
		}
	}

	/**
	 * This method loads a single LST formatted file.
	 * 
	 * @param sourceEntry
	 *            CampaignSourceEntry containing the absolute file path or the
	 *            URL from which to read LST formatted data.
	 */
	protected void loadLstFile(LoadContext context, CampaignSourceEntry sourceEntry)
	{
		setChanged();
		URI uri = sourceEntry.getURI();
		notifyObservers(uri);

		String dataBuffer;

		try
		{
			dataBuffer = LstFileLoader.readFromURI(uri);
		}
		catch (PersistenceLayerException ple)
		{
			String message = LanguageBundle.getFormattedString("Errors.LstFileLoader.LoadError", //$NON-NLS-1$
				uri, ple.getMessage());
			Logging.errorPrint(message);
			setChanged();
			return;
		}

		String aString = dataBuffer;
		if (context != null)
		{
			context.setSourceURI(uri);
		}

		String[] fileLines = aString.split(LstFileLoader.LINE_SEPARATOR_REGEXP);

		for (int i = 0; i < fileLines.length; i++)
		{
			String line = fileLines[i];
			if ((line.isEmpty()) || (line.charAt(0) == LstFileLoader.LINE_COMMENT_CHAR))
			{
				continue;
			}

			if (line.trim().isEmpty())
			{
				// Ignore the line
			}
			else
			{
				try
				{
					parseLine(context, line, sourceEntry);
				} catch (Throwable t)
				{
					String message = LanguageBundle.getFormattedString("Errors.LstFileLoader.ParseError", //$NON-NLS-1$
						uri, i + 1, t.getMessage());
					Logging.errorPrint(message, t);
					setChanged();
					Logging.errorPrint(LanguageBundle.getString("Errors.LstFileLoader.Ignoring: " + t.getMessage()));
					if (Logging.isDebugMode())
					{
						Logging.errorPrint(LanguageBundle.getString("Errors.LstFileLoader.Ignoring"), t);
					}
				}
			}
		}
	}
}
