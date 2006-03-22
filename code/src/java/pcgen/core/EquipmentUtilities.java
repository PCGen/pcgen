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

import java.util.*;

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
	 * Merge the equipment list
	 * @param aList
	 * @param merge
	 * @return merged list
	 */
	public static List mergeEquipmentList(final List aList, final int merge)
	{
		Collections.sort(aList, new Comparator()
		{
			public int compare(final Object obj1, final Object obj2)
			{
				int e1 = ((Equipment) obj1).getOutputIndex();
				int obj2Index = ((Equipment) obj2).getOutputIndex();

				// Force unset items (index of 0) to appear at the end
				if (e1 == 0)
				{
					e1 = 999;
				}

				if (obj2Index == 0)
				{
					obj2Index = 999;
				}

				if (e1 > obj2Index)
				{
					return 1;
				}
				else if (e1 < obj2Index)
				{
					return -1;
				}
				else
				{
					int sub1 = ((Equipment) obj1).getOutputSubindex();
					int obj2Subindex = ((Equipment) obj2).getOutputSubindex();

					if (sub1 > obj2Subindex)
					{
						return 1;
					}
					else if (sub1 < obj2Subindex)
					{
						return -1;
					}
					else
					{
						if (((Equipment) obj1).getName().compareToIgnoreCase(
								((Equipment) obj2).getName()) == 0)
						{
							return ((Equipment) obj1).getParentName()
									.compareToIgnoreCase(
											((Equipment) obj2).getParentName());
						}
						return ((Equipment) obj1).getName()
								.compareToIgnoreCase(((Equipment) obj2).getName());
					}
				}
			}

			public boolean equals(final Object obj)
			{
				return false;
			}

			public int hashCode()
			{
				return 0;
			}
		});

		// no merging, just sorting
		if (merge == Constants.MERGE_NONE)
		{
			return aList;
		}

		final ArrayList eq1List = new ArrayList();
		final ArrayList eq2List = new ArrayList();
		final ArrayList mergeList = new ArrayList();

		// create a temporary list to merge with
		for (Iterator e = aList.iterator(); e.hasNext();)
		{
			final Equipment tempEquip = (Equipment) e.next();
			final Equipment eq = (Equipment) tempEquip.clone();
			eq1List.add(eq);
			eq2List.add(eq);
		}

		// merge like equipment within same container
		if (merge == Constants.MERGE_LOCATION)
		{
			for (Iterator e = eq1List.iterator(); e.hasNext();)
			{
				final Equipment eq1 = (Equipment) e.next();
				double eQty = eq1.qty();
				boolean found = false;

				for (int i = 0; i < eq2List.size(); i++)
				{
					final Equipment eq2 = (Equipment) eq2List.get(i);

					if (eq1 == eq2)
					{
						eq2List.remove(eq2);
						found = true;
						i--;
					}
					else if (eq1.isContainer())
					{
						// no container merge
						continue;
					}
					else if (eq1.isType("TEMPORARY") || eq2.isType("TEMPORARY"))
					{
						// Temporary Bonus generated equipment must not merge
						continue;
					}
					else if (eq1.getName().equals(eq2.getName())
							&& (eq1.getLocation() == eq2.getLocation())
							&& eq1.getParentName().equals(eq2.getParentName()))
					{
						eq2List.remove(eq2);
						eQty += eq2.qty();
						found = true;
						i--;
					}
				}

				if (found)
				{
					eq1.setQty(eQty);
					mergeList.add(eq1);
				}
			}

			return mergeList;
		}

		// merge all like equipment together
		if (merge == Constants.MERGE_ALL)
		{
			for (Iterator e1 = eq1List.iterator(); e1.hasNext();)
			{
				final Equipment eq1 = (Equipment) e1.next();
				double eQty = 0.0;
				boolean found = false;

				for (int i = 0; i < eq2List.size(); i++)
				{
					final Equipment eq2 = (Equipment) eq2List.get(i);

					if (eq1.getName().equals(eq2.getName()))
					{
						if (eq1.isContainer())
						{
							// no container merge
							found = true;
						}
						else if (eq1.isType("TEMPORARY")
								|| eq2.isType("TEMPORARY"))
						{
							// Temporary Bonus generated equipment must not
							// merge
							found = true;
						}
						else
						{
							eq2List.remove(eq2);
							eQty += eq2.qty();
							found = true;
							i--;
						}
					}
				}

				if (eQty <= 0.0)
				{
					eQty = eq1.qty();
				}

				if (found)
				{
					eq1.setQty(eQty);
					mergeList.add(eq1);
				}
			}

			return mergeList;
		}

		return null;
	}

	/**
	 * filters a list of equipment to remove all equipment of a given type
	 * @param aList the list to remove
	 * @param type the type to remove
	 * @return a new list containing objects which aren't the specified type
	 */
	public static List removeEqType(final List aList, final String type)
	{
		final List aArrayList = new ArrayList();
		Equipment eq;

		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();

			if (type.equalsIgnoreCase("CONTAINED")
					&& (eq.getParent() != null))
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
	 * Filters a list of equipment, returns a new list which only has the item
	 * of equipment that matched type
	 * @param aList the list of equipment to filter
	 * @param aString the type of object to return
	 * @return a new list of objects which are all of type aString
	 */
	public static List removeNotEqType(final List aList, final String aString)
	{
		final List aArrayList = new ArrayList();
		Equipment eq;

		for (Iterator mapIter = aList.iterator(); mapIter.hasNext();)
		{
			eq = (Equipment) mapIter.next();

			if (eq.typeStringContains(aString))
			{
				aArrayList.add(eq);
			}
		}

		return aArrayList;
	}

	/**
	 * Adds a String to a name, for example, adding "Longsword" to "Weapon
	 * Specialisation" gives "Weapon Specialisation (Longsword)"
	 *
	 * @param   aName    The Name to add to
	 * @param   aString  The string to add
	 *
	 * @return  The modified name
	 */
	static String appendToName(final String aName, final String aString)
	{
		final StringBuffer aBuf = new StringBuffer(aName);
		final int          iLen = aBuf.length() - 1;
	
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
	 * @param   aName
	 *
	 * @return  the name with sub-choices stripped from it
	 */
	public static String removeChoicesFromName(String aName)
	{
		final int anInt = aName.indexOf('(');
	
		return (anInt >= 0) ? aName.substring(0, anInt).trim() : aName;
	}


	/**
	 * Takes a string of the form "foo (bar, baz)", populates the array
	 * with ["bar", "baz"] and returns foo.  All strings returned by this
	 * function have had leading.trailing whitespace removed.
	 * 
	 * @param name
	 * @param specifics 
	 * 
	 * @return the name with sub-choices stripped from it
	 */
	public static String getUndecoratedName(final String name, ArrayList specifics) {

		String altName = removeChoicesFromName(name);

		specifics.clear();
		int start = name.indexOf('(') + 1;
		int end   = name.lastIndexOf(')');
		
		if (start >= 0 && end > start) {

			// we want what is inside the outermost parenthesis.
			String subName = name.substring(start, end);
			
			for (Iterator specIt = Arrays.asList(subName.split("\\s*,\\s*")).iterator(); specIt.hasNext(); ) {
				specifics.add(((String) specIt.next()).trim());
			}
		}
		
		return altName;
	}

}
