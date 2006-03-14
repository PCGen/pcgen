/*
 * EquipSlot.java
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
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 * Created on February 24th, 2002, 11:26 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.character;

import pcgen.core.Globals;

import java.util.StringTokenizer;

/**
 * EquipSlot
 * Contains the possible slots that equipment can go into
 * and what type of equipment can go into each slot
 **/
public final class EquipSlot
{
	private String containEq = "";

	/**
	 *
	 * the Structure of each EquipSlot is as follows:
	 *
	 * EQSLOT:Neck     CONTAINS:AMULET=1    NUMBER:HEAD
	 * EQSLOT:Fingers  CONTAINS:RING=2      NUMBER:HANDS
	 *
	 * slotName:    Name of this equipment slot (Neck, Body, etc)
	 * containEq:     What type of equipment it can contain
	 * containNum:     The number of items each slot can hold
	 * slotNumType:    The type of slot (used to get total number of slots)
	 *
	 **/
	private String slotName = "";
	private String slotNumType = "";
	private int containNum = 1;

	public String toString()
	{
		return slotName;
	}

	/**
	 * Set container number
	 * @param i
	 */
	public void setContainNum(final int i)
	{
		containNum = i;
	}

	/**
	 * Set container type
	 * @param x
	 */
	public void setContainType(final String x)
	{
		containEq = x;
	}

	/**
	 * Get container type
	 * @return container type
	 */
	public String getContainType()
	{
		return containEq;
	}

	/**
	 * Get number of slots
	 * @return number of slots
	 */
	public int getSlotCount()
	{
		final int multi = Globals.getEquipSlotTypeCount(slotNumType);

		return multi * containNum;
	}

	/**
	 * Set slot name
	 * @param x
	 */
	public void setSlotName(final String x)
	{
		slotName = x;
	}

	/**
	 * Get slot name
	 * @return slot name
	 */
	public String getSlotName()
	{
		return slotName;
	}

	/**
	 * Set slot number type 
	 * @param x
	 */
	public void setSlotNumType(final String x)
	{
		slotNumType = x;
	}

	/**
	 * TRUE if it can contain a type
	 * @param aTypeList
	 * @return TRUE if it can contain a type
	 */
	public boolean canContainType(final String aTypeList)
	{
		final StringTokenizer aTok = new StringTokenizer(aTypeList, ".", false);

		while (aTok.hasMoreTokens())
		{
			final String aType = aTok.nextToken();

			if (aType.equalsIgnoreCase(containEq))
			{
				return true;
			}
		}

		return false;
	}
}
