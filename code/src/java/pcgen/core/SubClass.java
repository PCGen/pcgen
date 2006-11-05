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
 * $Id$
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.CampaignSourceEntry;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.util.Logging;

/**
 * <code>SubClass</code>.
 *
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class SubClass extends PCClass
{
	private List<String> levelArray = null;
	private String choice = null;

	/**
	 * Has the prohibitCost value been set yet
	 * If not, it will default to the cost.
	 */
	private boolean prohibitCostSet = false;

	/** The cost to specialise in this sub-class. */
	private int cost = 0;

	/** The cost to have this sub-class as prohibited. */
	private int prohibitCost = 0;

	/** Constructor */
	public SubClass()
	{
		setNumSpellsFromSpecialty(0);
		setSpellBaseStat(null);
	}

	/**
	 * Set the choice
	 * @param arg
	 */
	public void setChoice(final String arg)
	{
		choice = arg;
	}

	/**
	 * Get the choice
	 * @return choice
	 */
	public String getChoice()
	{
		if (choice == null)
		{
			return "";
		}

		return choice;
	}

	/**
	 * Set the cost to specialise in this sub-class to the supplied value.
	 *
	 * @param arg The new cost of the sub-class.
	 */
	public void setCost(final int arg)
	{
		cost = arg;
	}

	/**
	 * Get the cost to specialise in this sub-class.
	 *
	 * @return int The cost of the sub-class.
	 */
	public int getCost()
	{
		return cost;
	}

	/**
	 * Sets the prohibitCost.
	 * @param prohibitCost The prohibitCost to set
	 */
	public void setProhibitCost(final int prohibitCost)
	{
		this.prohibitCost = prohibitCost;
		this.prohibitCostSet = true;
	}

	/**
	 * Returns the prohibitCost. If the prohibited cost has not already
	 * been set, then the sub-classes cost will be returned. This preserves
	 * the previous behaviour where the prohibited cost and cost were the same.
	 *
	 * @return int The prohibit cost for the sub-class.
	 */
	public int getProhibitCost()
	{
		if (prohibitCostSet)
		{
			return prohibitCost;
		}
		return cost;
	}

	/**
	 * Add sub class to the level array
	 * @param arg
	 */
	public void addToLevelArray(final String arg)
	{
		if (levelArray == null)
		{
			levelArray = new ArrayList<String>();
		}

		levelArray.add(arg);
	}

	/**
	 * Apply the level mods to a class
	 * @param aClass
	 */
	public void applyLevelArrayModsTo(final PCClass aClass)
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

			final CampaignSourceEntry tempSource = new CampaignSourceEntry(customCampaign, aClass.getSourceFile());

			for ( String line : levelArray )
			{
				final PCClassLoader classLoader = new PCClassLoader();
				classLoader.parseLine(aClass, line, tempSource);
			}
		}
		catch (PersistenceLayerException exc)
		{
			Logging.errorPrint(exc.getMessage());
		}
	}

	public String getSupplementalDisplayInfo() {
		boolean added = false;
		StringBuffer displayInfo = new StringBuffer();
		if (getNumSpellsFromSpecialty() != 0) {
			displayInfo.append("SPECIALTY SPELLS:").append(
					getNumSpellsFromSpecialty());
			added = true;
		}

		if (getSpellBaseStat() != null) {
			if (added) {
				displayInfo.append(" ");
			}
			displayInfo.append("SPELL BASE STAT:").append(getSpellBaseStat());
			added = true;
		}

		if (!added) {
			displayInfo.append(' ');
		}
		return displayInfo.toString();
	}
}
