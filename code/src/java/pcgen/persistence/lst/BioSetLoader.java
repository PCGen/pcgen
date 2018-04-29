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
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.AgeSet;
import pcgen.core.BioSet;
import pcgen.core.GameMode;
import pcgen.core.Kit;
import pcgen.core.PObject;
import pcgen.core.SystemCollections;
import pcgen.core.bonus.BonusObj;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;


public final class BioSetLoader extends LstLineFileLoader
{
	private static String regionName = Constants.NONE;
	BioSet bioSet = new BioSet();
	/**
	 * The age set (bracket) currently being processed. Used by the parseLine
	 * method to hold state between calls.
	 */
	int currentAgeSetIndex = 0;

	/**
	 * clear the regionName
	 */
	public static void clear()
	{
		regionName = Constants.NONE;
	}

	@Override
	public void loadLstFile(LoadContext context, URI fileName)
			throws PersistenceLayerException
	{
		currentAgeSetIndex = 0;
		final GameMode game = SystemCollections.getGameModeNamed(gameMode);
		bioSet = game.getBioSet();
		super.loadLstFile(context, fileName);
		game.setBioSet(bioSet);
	}

	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI)
	{
		if (lstLine.startsWith("AGESET:"))
		{
			String line = lstLine.substring(7);
			int pipeLoc = line.indexOf('|');
			if (pipeLoc == -1)
			{
				Logging.errorPrint("Found invalid AGESET " + "in Bio Settings "
						+ sourceURI + ", was expecting a |: " + lstLine);
				return;
			}
			String ageIndexString = line.substring(0, pipeLoc);
			try
			{
				currentAgeSetIndex = Integer.parseInt(ageIndexString);
				StringTokenizer colToken = new StringTokenizer(line
						.substring(pipeLoc + 1), SystemLoader.TAB_DELIM);
				AgeSet ageSet = new AgeSet(colToken.nextToken().intern(),
						currentAgeSetIndex);
				while (colToken.hasMoreTokens())
				{
					parseTokens(context, ageSet, colToken);
				}

				ageSet = bioSet.addToAgeMap(regionName, ageSet, sourceURI);
				Integer oldIndex = bioSet.addToNameMap(ageSet);
				if (oldIndex != null && oldIndex != currentAgeSetIndex)
				{
					Logging.errorPrint("Incompatible Index for AGESET "
							+ "in Bio Settings " + sourceURI + ": " + oldIndex
							+ " and " + currentAgeSetIndex + " for "
							+ ageSet.getName());
				}

			}
			catch (NumberFormatException e)
			{
				Logging.errorPrint("Illegal Index for AGESET "
						+ "in Bio Settings " + sourceURI + ": "
						+ ageIndexString + " was not an integer");
			}
		}
		else
		{
			final StringTokenizer colToken = new StringTokenizer(lstLine,
					SystemLoader.TAB_DELIM);
			String colString;
			String raceName = "";
			List<String> preReqList = null;

			while (colToken.hasMoreTokens())
			{
				colString = colToken.nextToken();

				if (colString.startsWith("RACENAME:"))
				{
					raceName = colString.substring(9);
				}
				else if (colString.startsWith("REGION:"))
				{
					regionName = colString.substring(7).intern();
				}
				else if (PreParserFactory.isPreReqString(colString))
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
						final StringBuilder sBuf = new StringBuilder(100+colString.length());
						sBuf.append(colString);

						for (String aPreReqList : preReqList)
						{
							sBuf.append('[').append(aPreReqList).append(
									']');
						}

						aString = sBuf.toString();
					}

					bioSet.addToUserMap(regionName, raceName.intern(), 
							aString.intern(), currentAgeSetIndex);
				}
			}
		}
	}

	private void parseTokens(LoadContext context, AgeSet ageSet, StringTokenizer tok)
	{
		final PObject dummy = new PObject();
		try
		{
			while (tok.hasMoreTokens())
			{
				// in the code below, I use "new String()" to unlink the string from the containing file to save memory,
				// but I don't intern() the string because it's not fully parsed yet so don't want to add permgen overhead
				// to a string that's just going to get GC'd eventually
				//
				// This pessimization might be removable if we get all impls of CDOMToken.parseToken() to intern. But right
				// now there are too many of them...

				String currentTok = tok.nextToken();
				if (currentTok.startsWith("BONUS:"))
				{
					if (context.processToken(dummy, "BONUS", new String(currentTok.substring(6))))
					{
						context.commit();
					}
					else
					{
						context.rollback();
						Logging.errorPrint("Error in BONUS parse: " + currentTok);
						Logging.replayParsedMessages();
					}
				}
				else if (currentTok.startsWith("KIT:"))
				{
					if (context.processToken(dummy, "KIT", new String(currentTok.substring(4))))
					{
						context.commit();
					}
					else
					{
						context.rollback();
						Logging.errorPrint("Error in KIT parse: " + currentTok);
						Logging.replayParsedMessages();
					}
				}
				else
				{
					Logging.errorPrint("Unexpected token in AGESET: " + currentTok);
				}
			}
			List<BonusObj> bonuses = dummy.getListFor(ListKey.BONUS);
			if (bonuses != null)
			{
				ageSet.addBonuses(bonuses);
			}
			List<TransitionChoice<Kit>> kits = dummy.getListFor(ListKey.KIT_CHOICE);
			if (kits != null)
			{
				ageSet.addKits(kits);
			}
		}
		catch (PersistenceLayerException e)
		{
			Logging.errorPrint("Error in token parse: "
					+ e.getLocalizedMessage());
		}
	}
}
