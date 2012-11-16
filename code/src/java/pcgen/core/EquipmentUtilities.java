/*
 * EquipmentUtilities.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on Aug 25, 2005
 *  Refactored from PlayerCharacter, created on April 21, 2001, 2:15 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class EquipmentUtilities
{

	private EquipmentUtilities()
	{
		//Don't allow instantiation of utility class
	}

	/**
	 * filters a list of equipment to remove all equipment of a given type
	 *
	 * @param aList the list to remove
	 * @param type  the type to remove
	 *
	 * @return a new list containing objects which aren't the specified type
	 */
	public static List<Equipment> removeEqType(final List<Equipment> aList, final String type)
	{
		final List<Equipment> aArrayList = new ArrayList<Equipment>();

		for (final Equipment eq : aList)
		{
			if ("CONTAINED".equalsIgnoreCase(type) && (eq.getParent() != null))
			{
				continue;
			}

			if (!eq.typeStringContains(type))
			{
				aArrayList.add(eq);
			}
		}

		return aArrayList;
	}

	/**
	 * Filters a list of equipment, returns a new list which only has the item of equipment
	 * that matched type
	 *
	 * @param aList   the list of equipment to filter
	 * @param aString the type of object to return
	 *
	 * @return a new list of objects which are all of type aString
	 */
	public static List<Equipment> removeNotEqType(final List<Equipment> aList, final String aString)
	{
		final List<Equipment> aArrayList = new ArrayList<Equipment>();

		for (Equipment eq : aList)
		{
			if (eq.typeStringContains(aString))
			{
				aArrayList.add(eq);
			}
		}

		return aArrayList;
	}

	/**
	 * Adds a String to a name, for example, adding "Longsword" to "Weapon Specialisation"
	 * gives "Weapon Specialisation (Longsword)"
	 *
	 * @param aName   The Name to add to
	 * @param aString The string to add
	 *
	 * @return The modified name
	 */
	static String appendToName(final String aName, final String aString)
	{
		final StringBuilder aBuf = new StringBuilder(aName);
		final int iLen = aBuf.length() - 1;

		if (aBuf.charAt(iLen) == ')')
		{
			aBuf.setCharAt(iLen, '/');
		}
		else
		{
			aBuf.append(" (");
		}

		aBuf.append(aString);
		aBuf.append(')');

		return aBuf.toString();
	}
}
