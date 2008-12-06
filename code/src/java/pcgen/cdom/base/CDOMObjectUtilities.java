/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.base;

import java.util.Collection;
import java.util.Comparator;

/**
 * CDOMObjectUtilities is a utility class designed to provide utility methods
 * when working with pcgen.cdom.base.CDOMObject Objects
 */
public final class CDOMObjectUtilities
{

	/**
	 * Provides a Comparator that is capable of sorting CDOMObjects. This
	 * sorting is performed based on the Key name of the CDOMObjects.
	 */
	public static final Comparator<CDOMObject> CDOM_SORTER = new Comparator<CDOMObject>()
	{

		public int compare(CDOMObject arg0, CDOMObject arg1)
		{
			return compareKeys(arg0, arg1);
		}
	};

	private CDOMObjectUtilities()
	{
		// Utility class should not be constructed
	}

	/**
	 * Concatenates the Key Name given Collection of CDOMObjects into a String
	 * using the separator as the delimiter.
	 * 
	 * The LST format for each CDOMObject is determined by calling the
	 * getLSTformat() method on the CDOMObject.
	 * 
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection.
	 * 
	 * @param strings
	 *            An Collection of CDOMObjects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the Key Name of the
	 *         given Collection of CDOMObject objects
	 */
	public static <T extends CDOMObject> String joinKeyName(Collection<T> c,
			String separator)
	{
		if (c == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(c.size() * 10);

		boolean needjoin = false;

		for (CDOMObject obj : c)
		{
			if (needjoin)
			{
				result.append(separator);
			}
			needjoin = true;
			result.append(obj.getLSTformat());
		}

		return result.toString();
	}

	/**
	 * Compares the Keys of two CDOMObjects. Returns a negative integer if the
	 * key for the first object sorts before the key for the second object. Note
	 * that null sorts last (though a CDOMObject should never return null from a
	 * call to getKeyName(), this is error protected). This comparison is case
	 * sensitive.
	 * 
	 * @param arg0
	 *            The first CDOMObject, for which the key will be compared
	 * @param arg1
	 *            The second CDOMObject, for which the key will be compared
	 * @return a negative integer if the key for the first object sorts before
	 *         the key for the second object; a positive integer if the key for
	 *         the first object sorts after the key for the second object, or 0
	 *         if the keys are equal
	 */
	public static int compareKeys(CDOMObject arg0, CDOMObject arg1)
	{
		String base = arg0.getKeyName();
		if (base == null)
		{
			if (arg1.getKeyName() == null)
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			if (arg1.getKeyName() == null)
			{
				return 1;
			}
			else
			{
				return base.compareTo(arg1.getKeyName());
			}
		}
	}

}
