/*
 * DenominationList.java
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.core.money;

import java.util.ArrayList;

/**
 * DenominationList.java
 *
 * Implements a list of coin denominations by region.
 * The real purpose of this class is to provide a method
 * to parse a file of coins.  All else pretty much stays
 * the same.
 *
 * @author   Brad Stiles (brasstilde@yahoo.com)
 * @version    $Revision: 1.7 $
 */
public final class DenominationList extends ArrayList
{
	private static DenominationList instance = new DenominationList();

	private DenominationList()
	{
		super();
	}

	/**
	 * Get instance
	 * @return instance
	 */
	public static DenominationList getInstance()
	{
		return instance;
	}

	/**
	 * Get Global denominations
	 * @return Global denominations
	 */
	public Denominations getGlobalDenominations()
	{
		/*
		   This spliting of the calls is to hopefully someday
		   accomodate regional money.
		 */
		return getRegionalDenominations("Global"); // "Global"
	}

	/**
	 * Get regional denominations
	 * @param regionName
	 * @return regional denominations
	 */
	public Denominations getRegionalDenominations(final String regionName) /*String regionName*/
	{
		//removed the String regionName from the parameter list since it was being
		//set to Global regardless of what was passed.
		Denominations d;
		String region;

		for (int i = 0; i < size(); i++)
		{
			d = (Denominations) get(i);
			region = d.getRegion();

			if (region.equalsIgnoreCase(regionName))
			{
				return d;
			}
		}

		return null;
	}
}
