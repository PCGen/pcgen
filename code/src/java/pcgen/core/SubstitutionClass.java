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

import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.util.Logging;

import java.util.ArrayList;
import java.util.List;

/**
 * <code>SubClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision: 254 $
 */
public final class SubstitutionClass extends PCClass
{
	private List<String> levelArray = null;
	private List<Integer> modLevels = null;

	/** Constructor */
	public SubstitutionClass()
	{
		setKnownSpellsFromSpecialty(0);
		setSpellBaseStat(null);
	}

	/**
	 * Add substitution class to the level array
	 * @param arg
	 */
	public void addToLevelArray(final String arg)
	{
		if (levelArray == null)
		{
			levelArray = new ArrayList<String>();
			modLevels = new ArrayList<Integer>();
		}

		levelArray.add(arg);

		final Integer level = Integer.valueOf(arg.substring(0, arg.indexOf("\t")));
		modLevels.add(level);
	}

	/**
	 * Apply the level mods to a class
	 * @param aClass
	 */
	public void applyLevelArrayModsToLevel(final PCClass aClass, final int aLevel)
	{
		if (levelArray == null)
		{
			return;
		}

		try
		{
			final Campaign customCampaign = new Campaign();
			customCampaign.setName("Custom");
			customCampaign.addDescription(new Description("Custom data"));

			final CampaignSourceEntry tempSource = new CampaignSourceEntry(customCampaign, aClass.getSourceURI());

			// remove all stuff from the original level
			aClass.removeAllBonuses(aLevel);
			aClass.removeAllAutoFeats(aLevel);
			aClass.removeAllLevelAbilities(aLevel);

			for (String aLine : levelArray)
			{
				final int modLevel = Integer.parseInt(aLine.substring(0, aLine.indexOf("\t")));
				
				if (aLevel == modLevel)
				{
					final PCClassLoader classLoader = new PCClassLoader();
					classLoader.parseLine(aClass, aLine, tempSource);
				}
			}
		}
		catch (PersistenceLayerException exc)
		{
			Logging.errorPrint(exc.getMessage());
		}
	}

	/**
	 * Get the level mods for a specific level
	 * @param aClass
	 */
	public boolean hasLevelArrayModsForLevel(final int aLevel)
	{
		return modLevels.contains(Integer.valueOf(aLevel));
	}


}
