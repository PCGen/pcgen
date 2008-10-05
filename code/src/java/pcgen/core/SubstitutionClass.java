/*
 * SubClass.java
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
 *
 * Created on November 19, 2002, 10:29 PM
 *
 * $Id: SubClass.java 254 2006-03-15 16:46:10Z karianna $
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.utils.DeferredLine;
import pcgen.util.Logging;

/**
 * <code>SubClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 254 $
 */
public final class SubstitutionClass extends PCClass
{
	/** Constructor */
	public SubstitutionClass()
	{
	}

	/**
	 * Apply the level mods to a class
	 * @param aClass
	 */
	public void applyLevelArrayModsToLevel(final PCClass aClass, final int aLevel, final PlayerCharacter aPC)
	{
		List<DeferredLine> levelArray = getListFor(ListKey.SUB_CLASS_LEVEL);
		if (levelArray == null)
		{
			return;
		}

		List<DeferredLine> newLevels = new ArrayList<DeferredLine>();
		for (DeferredLine line : levelArray)
		{
			String aLine = line.lstLine;
			final int modLevel = Integer.parseInt(aLine.substring(0, aLine
					.indexOf("\t")));

			if (aLevel == modLevel)
			{
				if (levelArrayQualifies(aPC, aLine, line.source))
				{
					newLevels.add(line);
				}
			}
		}

		// find all qualifying level lines for this level
		// and put into newLevels list.
		if (!newLevels.isEmpty())
		{
			// remove all stuff from the original level
			aClass.stealClassLevel(this, aLevel);
			aClass.removeAllBonuses(aLevel);
			aClass.removeAllAutoAbilites(aLevel);
			aClass.removeAllVirtualAbilites(aLevel);
			aClass.removeAllLevelAbilities(aLevel);
			// Now add in each new level line in turn.
			for (DeferredLine line : newLevels)
			{
				aClass.performReallyBadHackForOldTokens(line);
			}
		}
	}

	/**
	 * Get the level mods for a specific level
	 * 
	 * @param aClass
	 */
	public boolean hasLevelArrayModsForLevel(final int aLevel)
	{
		return hasClassLevel(aLevel);
	}
	
	public boolean qualifiesForSubstitutionLevel(PlayerCharacter pc, int level) 
	{ 
		List<DeferredLine> levelArray = getListFor(ListKey.SUB_CLASS_LEVEL);
		if (levelArray == null)
		{
			return false;
		}

		for (DeferredLine line : levelArray)
		{
			String aLine = line.lstLine;
			final int modLevel = Integer.parseInt(aLine.substring(0, aLine
					.indexOf("\t")));

			if (level == modLevel)
			{
				if (!levelArrayQualifies(pc, aLine, line.source))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param pc
	 * @param aLine
	 * @param tempSource
	 * @return
	 */
	private boolean levelArrayQualifies(final PlayerCharacter pc, final String aLine,
		final CampaignSourceEntry tempSource)
	{
		final PCClassLoader classLoader = new PCClassLoader(); 
		 PCClass dummyClass = new PCClass();   
		 
		 try
		{
			classLoader.parseLine(Globals.getContext(), dummyClass, aLine, tempSource);
		}
		catch (PersistenceLayerException e)
		{
			Logging
			.errorPrint("Unable to parse line from levelArray: " + aLine);
		} 
		 return dummyClass.qualifies(pc);
	}
	


}
