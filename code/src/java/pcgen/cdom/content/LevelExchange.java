/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.PCClass;

public class LevelExchange extends ConcretePrereqObject
{

	private final CDOMSingleRef<PCClass> exchangeClass;
	private final int minDonatingLevel;

	private final int maxDonatedLevels;

	private final int donatingLowerLevelBound;

	public LevelExchange(CDOMSingleRef<PCClass> pcc, int minDonatingLvl,
			int maxDonated, int donatingLowerBound)
	{
		if (minDonatingLvl <= 0)
		{
			throw new IllegalArgumentException(
					"Error: Min Donatign Level <= 0: "
							+ "Cannot Allow Donations to produce negative levels");
		}
		if (maxDonated <= 0)
		{
			throw new IllegalArgumentException(
					"Error: Max Donated Levels <= 0: "
							+ "Cannot Allow Donations to produce negative levels");
		}
		if (donatingLowerBound < 0)
		{
			throw new IllegalArgumentException(
					"Error: Max Remaining Levels < 0: "
							+ "Cannot Allow Donations to produce negative levels");
		}
		if (donatingLowerBound < 0)
		{
			throw new IllegalArgumentException(
					"Error: Min Remaining Levels < 0: "
							+ "Cannot Allow Donations to produce negative levels");
		}
		if (minDonatingLvl - maxDonated > donatingLowerBound)
		{
			throw new IllegalArgumentException(
					"Error: Donating Lower Bound cannot be reached");
		}
		exchangeClass = pcc;
		minDonatingLevel = minDonatingLvl;
		maxDonatedLevels = maxDonated;
		donatingLowerLevelBound = donatingLowerBound;
	}

	public int getDonatingLowerLevelBound()
	{
		return donatingLowerLevelBound;
	}

	public CDOMSingleRef<PCClass> getExchangeClass()
	{
		return exchangeClass;
	}

	public int getMaxDonatedLevels()
	{
		return maxDonatedLevels;
	}

	public int getMinDonatingLevel()
	{
		return minDonatingLevel;
	}

	@Override
	public int hashCode()
	{
		return minDonatingLevel * 23 + maxDonatedLevels * 31
				+ donatingLowerLevelBound;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == this)
		{
			return true;
		}
		if (!(o instanceof LevelExchange))
		{
			return false;
		}
		LevelExchange other = (LevelExchange) o;
		return minDonatingLevel == other.minDonatingLevel
				&& maxDonatedLevels == other.maxDonatedLevels
				&& donatingLowerLevelBound == other.donatingLowerLevelBound
				&& exchangeClass.equals(other.exchangeClass);
	}

	public String getLSTformat()
	{
		return getExchangeClass().getLSTformat();
	}
}
