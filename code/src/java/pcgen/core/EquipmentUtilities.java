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

import pcgen.core.utils.CoreUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	private static Comparator<Equipment> equipmentComparator = new Comparator<Equipment>()
	{
		private int compareInts(final int obj1Index, final int obj2Index)
		{
			if (obj1Index > obj2Index)
			{
				return 1;
			}
			else if (obj1Index < obj2Index)
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}

		public int compare(final Equipment obj1, final Equipment obj2)
		{
			int o1i = obj1.getOutputIndex();
			int o2i = obj2.getOutputIndex();

			// Force unset items (index of 0) to appear at the end
			o1i = (o1i == 0) ? 999 : o1i;
			o2i = (o2i == 0) ? 999 : o2i;

			final int result1 = compareInts(o1i, o2i);

			if (result1 != 0)
			{
				return result1;
			}

			final int result2 = compareInts(obj1.getOutputSubindex(), obj2.getOutputSubindex());

			if (result2 != 0)
			{
				return result2;
			}

			final int result3 = obj1.getName().compareToIgnoreCase(obj2.getName());

			if (result3 != 0)
			{
				return result3;
			}

			return obj1.getParentName().compareToIgnoreCase(obj2.getParentName());
		}

		public boolean equals(final Equipment obj)
		{
			return false;
		}

		public int hashCode()
		{
			return 0;
		}
	};

	/**
	 * Merge the equipment list
	 *
	 * @param aList the list of Equipment
	 * @param merge The type of merge to perform
	 *
	 * @return merged list
	 */
	public static List<Equipment> mergeEquipmentList(final List<Equipment> aList, final int merge)
	{
		Collections.sort(aList, equipmentComparator);

		// no merging, just sorting
		if (merge == Constants.MERGE_NONE)
		{
			return aList;
		}

		final List<Equipment> workingList = new ArrayList<Equipment>();

		// create a temporary list to merge with
		for (final Equipment tempEq : aList)
		{
			workingList.add(tempEq.clone());
		}

		int endIndex = workingList.size();

		for (int i = 0; i < endIndex; i++)
		{
			final Equipment eq1 = workingList.get(i);
			double eQty = eq1.qty();

			for (int j = i + 1; j < endIndex; j++)
			{
				final Equipment eq2 = workingList.get(j);

				// no container merge or Temporary Bonus generated equipment must not merge
				if (eq1.isContainer() || eq1.isType("TEMPORARY") || eq2.isType("TEMPORARY"))
				{
					continue;
				}

				if (eq1.getName().equals(eq2.getName()))
				{
					// merge all like equipment together
					if (merge == Constants.MERGE_ALL ||

					    // merge like equipment within same container
					    (merge == Constants.MERGE_LOCATION
					     && (eq1.getLocation() == eq2.getLocation())
					     && eq1.getParentName().equals(eq2.getParentName())))
					{
						workingList.remove(eq2);
						eQty += eq2.qty();
						endIndex--;
					}
				}
			}

			eq1.setQty(eQty);
		}

		return workingList;
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
		final StringBuffer aBuf = new StringBuffer(aName);
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

	/**
	 * Extracts the choiceless form of a name, for example, with all choices removed
	 *
	 * @param aName
	 *
	 * @return the name with sub-choices stripped from it
	 */
	public static String removeChoicesFromName(String aName)
	{
		final int anInt = aName.indexOf('(');

		return (anInt >= 0) ? aName.substring(0, anInt).trim() : aName;
	}


	/**
	 * Takes a string of the form "foo (bar, baz)", populates the array with ["bar", "baz"]
	 * and returns foo.  All strings returned by this function have had leading.trailing
	 * whitespace removed.
	 *
	 * @param name      The full name with stuff in parenthesis
	 * @param specifics a list which will contain the specifics after the operation has
	 *                  completed
	 *
	 * @return the name with sub-choices stripped from it
	 */
	public static String getUndecoratedName(final String name, final ArrayList<String> specifics)
	{

		final String altName = removeChoicesFromName(name);

		specifics.clear();
		final int start = name.indexOf('(') + 1;
		final int end = name.lastIndexOf(')');

		if (start >= 0 && end > start)
		{

			// we want what is inside the outermost parenthesis.
			final String subName = name.substring(start, end);

			specifics.addAll(CoreUtility.split(subName, ','));
		}

		return altName;
	}
}
