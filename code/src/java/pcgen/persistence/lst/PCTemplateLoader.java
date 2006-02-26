/*
 * PCTemplateLoader.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
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
 * Created on February 22, 2002, 10:29 PM
 *
 *
 * Current Ver: $Revision: 1.70 $
 * Last Editor: $Author: boomer70 $
 * Last Edited: $Date: 2005/11/04 05:54:28 $
 *
 */
package pcgen.persistence.lst;

import pcgen.core.PCTemplate;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.SystemLoader;
import pcgen.util.Logging;

import java.net.URL;
import java.util.StringTokenizer;

/**
 *
 * @author  David Rice <david-pcgen@jcuz.com>
 * @version $Revision: 1.70 $
 */
final class PCTemplateLoader
{
	/** Creates a new instance of PCTemplateLoader */
	private PCTemplateLoader()
	{
		// Empty Constructor
	}

	public static void parseLine(PCTemplate template, String inputLine, URL sourceURL, int lineNum)
		throws PersistenceLayerException
	{
		if (template == null)
		{
			return;
		}

		final StringTokenizer colToken = new StringTokenizer(inputLine, SystemLoader.TAB_DELIM);
		String templName = "None";
		int col = 0;

		if (!template.isNewItem())
		{
			col = 1; // .MOD skip required fields (name in this case)
			colToken.nextToken();
		}

		while (colToken.hasMoreTokens())
		{
			final String colString = colToken.nextToken().trim();

			if (colString.startsWith("REGION:"))
			{
				String region = colString.substring(7);

				if ("Yes".equalsIgnoreCase(region))
				{
					region = templName;
				}

				template.setRegion(region);

				continue;
			}

			if (PObjectLoader.parseTag(template, colString))
			{
				continue;
			}

			if (col == 0)
			{
				template.setName(colString);
				templName = template.getName();
			}
			else if (colString.startsWith("AGE:"))
			{
				template.setAgeString(colString.substring(4));
			}
			 // replaces racial age
			else if (colString.startsWith("BONUSFEATS:"))
			{
				template.setBonusInitialFeats(Integer.parseInt(colString.substring(11)));
			}
			 // number of additional feats to spend
			else if (colString.startsWith("BONUSSKILLPOINTS:"))
			{
				template.setBonusSkillsPerLevel(Integer.parseInt(colString.substring(17)));
			}
			 // additional skill points per level
			else if (colString.startsWith("CHOOSE:LANGAUTO:"))
			{
				template.setChooseLanguageAutos(colString.substring(16));
			}
			else if (colString.startsWith("COST:"))
			{
				template.setCost(colString.substring(5));
			}
			else if (colString.startsWith("CR:"))
			{
				template.setCR(Integer.parseInt(colString.substring(3)));
			}
			else if (colString.startsWith("FAVOREDCLASS:"))
			{
				template.setFavoredClass(colString.substring(13));
			}
			else if (colString.startsWith("FEAT:"))
			{
				template.addFeatString(colString.substring(5));
			}
			else if (colString.startsWith("GENDERLOCK:"))
			{
				template.setGenderLock(colString.substring(11));
			}
			 // set and lock character gender, disabling pulldown menu in description section.
			else if (colString.startsWith("GOLD:"))
			{
				Logging.errorPrint("GOLD: tag in " + sourceURL.toString()
					+ " no longer supported due to OGL compliance");
			}
			else if (colString.startsWith("HANDEDLOCK:"))
			{
				template.setHandedLock(colString.substring(11));
			}
			 // set and lock character handedness, disabling pulldown menu in description section.
			else if (colString.startsWith("HD:"))
			{
				template.addHitDiceString(colString.substring(3));
			}
			else if (colString.startsWith("HEIGHT:"))
			{
				template.setHeightString(colString.substring(7));
			}
			else if (colString.startsWith("HITDIE:")) // HitDieLock
			{
				template.setHitDieLock(colString.substring(7));
			}
			else if (colString.startsWith("LANGBONUS"))
			{
				template.setLanguageBonus(colString.substring(10));
			}
			else if (colString.startsWith("LEVEL:"))
			{
				template.addLevelString(colString.substring(6));
			}
			else if (colString.startsWith("LEVELADJUSTMENT:"))
			{
				template.setLevelAdjustment(colString.substring(16));
			}
			else if (colString.startsWith("LEVELSPERFEAT:"))
			{
				final int newLevels = Integer.parseInt(colString.substring(14));

				if (newLevels >= 0)
				{
					template.setLevelsPerFeat(newLevels);
				}
			}
			 // how many levels per feat.
			else if (colString.startsWith("NONPP:"))
			{
				template.setNonProficiencyPenalty(Integer.parseInt(colString.substring(6)));
			}
			else if (colString.startsWith("POPUPALERT:"))
			{
				// NOTE: This shouldn't be in here...
				// GuiFacade ties this class to Swing
				// GuiFacade.showMessageDialog(null, colString.substring(11), "PCGen", GuiFacade.ERROR_MESSAGE);
			}
			 // pops the message to the screen.
			else if (colString.startsWith("SIZE:"))
			{
				template.setTemplateSize(colString.substring(5));
			}
			else if (colString.startsWith("WEIGHT:"))
			{
				template.setWeightString(colString.substring(7));
			}
			 // replace racial weight
			else if (colString.startsWith("QUALIFY:"))
			{
				template.setQualifyString(colString.substring(8));
			}
			else if (colString.startsWith("RACETYPE:"))
			{
				template.setRaceType(colString.substring(9));
			}
			else if (colString.startsWith("RACESUBTYPE:"))
			{
				template.addSubTypeString(colString.substring(12));
			}
			else if (colString.startsWith("REMOVABLE:"))
			{
				if (colString.substring(10).startsWith("No"))
				{
					template.setRemovable(false);
				}
			}
			else if (colString.startsWith("REPEATLEVEL:"))
			{
				parseTemplateRepeatLevel(template, colString.substring(12), sourceURL);
			}
			else if (colString.startsWith("SUBRACE:"))
			{
				String subRace = colString.substring(8);

				if ("Yes".equalsIgnoreCase(subRace))
				{
					subRace = templName;
				}

				template.setSubRace(subRace);
			}
			else if (colString.startsWith("SUBREGION:"))
			{
				String subregion = colString.substring(10);

				if ("Yes".equalsIgnoreCase(subregion))
				{
					subregion = templName;
				}

				template.setSubRegion(subregion);
			}
			else if (colString.startsWith("TEMPLATE:"))
			{
				template.addTemplate(colString.substring(9));
			}
			else if (colString.startsWith("VISIBLE:"))
			{
				final String visType = colString.substring(8).toUpperCase();

				if (visType.startsWith("DISPLAY"))
				{
					template.setVisible(PCTemplate.VISIBILITY_DISPLAY_ONLY);
				}
				else if (visType.startsWith("EXPORT"))
				{
					template.setVisible(PCTemplate.VISIBILITY_OUTPUT_ONLY);
				}
				else if (visType.startsWith("NO"))
				{
					template.setVisible(PCTemplate.VISIBILITY_HIDDEN);
				}
				else
				{
					template.setVisible(PCTemplate.VISIBILITY_DEFAULT);
				}
			}
			else if (colString.startsWith("WEAPONBONUS"))
			{
				template.setWeaponProfBonus(colString.substring(12));
			}

			++col;
		}
	}

	/**
	 * Attempt to parse "toParse" as an int.  If it doesn't parse correctly, use
	 * aMessage and sourceURL to explain what is badly formatted and where.
	 *
	 * @param   toParse    Potential int string
	 * @param   aMessage   What the int represents (i.e. what we're trying to
	 *                     parse)
	 * @param   sourceURL  Where this came from (for the error message)
	 *
	 * @return  the parsed int (-1 on failure)
	 */

	private static int parseInt(final String toParse, final String aMessage, final URL sourceURL)
	{
		int iValue = -1;
		try
		{
			iValue = Integer.parseInt(toParse);
		}
		catch (NumberFormatException nfe)
		{
			Logging.errorPrint("Non-numeric " + aMessage + " '" + toParse + "' in " + sourceURL.toString());
		}
		return iValue;
	}


	/**
	 * Parse a repeat level tag in the format x|y|z:level:<level assigned item>.
	 * beginning at level, and ending at z, stepping by x, add <level assigned
	 * item> for y consecutive steps, then miss one.
	 *
	 * @param  template   the template to add to
	 * @param  colString  the repeat string
	 * @param  sourceURL  the file this came from
	 */

	private static void parseTemplateRepeatLevel(PCTemplate template, String colString, URL sourceURL)
	{
		//
		// x|y|z:level:<level assigned item>
		//
		final int endRepeat = colString.indexOf(':');
		if (endRepeat > 0)
		{
			final int endLevel = colString.indexOf(':', endRepeat + 1);
			if (endLevel > 0)
			{
				final StringTokenizer repeatToken = new StringTokenizer(colString.substring(0, endRepeat), "|");
				if (repeatToken.countTokens() == 3)
				{
					final int lvlIncrement = parseInt(repeatToken.nextToken(), "Level Increment", sourceURL);
					final int consecutive  = parseInt(repeatToken.nextToken(), "Consecutive Level", sourceURL);
					final int maxLevel     = parseInt(repeatToken.nextToken(), "Max Level", sourceURL);
					int iLevel = parseInt(colString.substring(endRepeat + 1, endLevel), "Level", sourceURL);

					if ((iLevel > 0) && (lvlIncrement > 0) && (maxLevel > 0) && (consecutive >= 0))
					{
						int count = consecutive;
						for(; iLevel <= maxLevel; iLevel += lvlIncrement)
						{
							if ((consecutive == 0) || (count != 0))
							{
								template.addLevelString(Integer.toString(iLevel) + ":" +
										colString.substring(endLevel + 1));
							}
							if (consecutive != 0)
							{
								if (count == 0)
								{
									count = consecutive;
								}
								else
								{
									--count;
								}
							}
						}
					}

					return;
				}
			}
		}

		Logging.errorPrint("Invalid REPEATLEVEL tag format: " + colString + "' in " + sourceURL.toString());
	}
}
