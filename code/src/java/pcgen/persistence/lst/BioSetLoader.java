/*
 * Copyright 2002 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 */
package pcgen.persistence.lst;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.Region;
import pcgen.core.AgeSet;
import pcgen.core.BioSet;
import pcgen.core.GameMode;
import pcgen.core.SystemCollections;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

public final class BioSetLoader extends LstLineFileLoader
{
	/**
	 * The current Region being processed
	 */
	private Optional<Region> region = Optional.empty();

	BioSet bioSet = new BioSet();

	/**
	 * The age set (bracket) currently being processed. Used by the parseLine
	 * method to hold state between calls.
	 */
	int currentAgeSetIndex = 0;

	/**
	 * Clear the Region.
	 */
	public void clear()
	{
		region = Optional.empty();
	}

	@Override
	public void loadLstFile(LoadContext context, URI fileName) throws PersistenceLayerException
	{
		currentAgeSetIndex = 0;
		GameMode game = SystemCollections.getGameModeNamed(gameMode);
		bioSet = game.getBioSet();
		super.loadLstFile(context, fileName);
		game.setBioSet(bioSet);
	}

	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
	{
		if (lstLine.startsWith("#"))
		{
			//Is a comment
			return;
		}
		if (lstLine.startsWith("AGESET:"))
		{
			String line = lstLine.substring(7);
			int pipeLoc = line.indexOf('|');
			if (pipeLoc == -1)
			{
				Logging.errorPrint(
					"Found invalid AGESET " + "in Bio Settings " + sourceURI + ", was expecting a |: " + lstLine);
				return;
			}
			String ageIndexString = line.substring(0, pipeLoc);
			try
			{
				currentAgeSetIndex = Integer.parseInt(ageIndexString);
			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Illegal Index for AGESET " + "in Bio Settings " + sourceURI + ": " + ageIndexString
					+ " was not an integer");
			}
			StringTokenizer colToken = new StringTokenizer(line.substring(pipeLoc + 1), SystemLoader.TAB_DELIM);
			AgeSet ageSet = new AgeSet();
			ageSet.setSourceURI(sourceURI);
			ageSet.setAgeIndex(currentAgeSetIndex);
			ageSet.setName(colToken.nextToken());
			while (colToken.hasMoreTokens())
			{
				LstUtils.processToken(context, ageSet, sourceURI,
					colToken.nextToken());
			}

			ageSet = bioSet.addToAgeMap(region, ageSet, sourceURI);
			Integer oldIndex = bioSet.addToNameMap(ageSet);
			if (oldIndex != null && oldIndex != currentAgeSetIndex)
			{
				Logging.errorPrint("Incompatible Index for AGESET " + "in Bio Settings " + sourceURI + ": "
						+ oldIndex + " and " + currentAgeSetIndex + " for " + ageSet.getDisplayName());
			}
			
		}
		else if (lstLine.startsWith("REGION:"))
		{
			region = Optional.of(Region.getConstant(lstLine.substring(7)));
		}
		else if (lstLine.startsWith("RACENAME:"))
		{
			StringTokenizer colToken = new StringTokenizer(lstLine, SystemLoader.TAB_DELIM);
			String raceName = colToken.nextToken().substring(9);
			List<String> preReqList = null;

			while (colToken.hasMoreTokens())
			{
				String colString = colToken.nextToken();

				if (PreParserFactory.isPreReqString(colString))
				{
					if (preReqList == null)
					{
						preReqList = new ArrayList<>();
					}

					preReqList.add(colString);
				}
				else
				{
					String aString = colString;

					if (preReqList != null)
					{
						final StringBuilder sBuf = new StringBuilder(100 + colString.length());
						sBuf.append(colString);

						for (String aPreReqList : preReqList)
						{
							sBuf.append('[').append(aPreReqList).append(']');
						}

						aString = sBuf.toString();
					}

					bioSet.addToUserMap(region, raceName, aString, currentAgeSetIndex);
				}
			}
		}
		else if (!StringUtils.isEmpty(lstLine))
		{
			Logging.errorPrint("Unable to process line " + lstLine
				+ "in Bio Settings " + sourceURI);
		}
	}
}
