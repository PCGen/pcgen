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

/**
 * CDOMObjectUtilities is a utility class designed to provide utility methods
 * when working with pcgen.cdom.base.CDOMObject Objects
 */
public final class CDOMObjectUtilities
{

	private CDOMObjectUtilities()
	{
		//Utility class should not be constructed
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

}
