/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.choiceset;

import java.util.Collection;
import java.util.Comparator;

import pcgen.cdom.base.PrimitiveChoiceSet;

public class ChoiceSetUtilities
{
	public static final Comparator<PrimitiveChoiceSet<?>> WRITEABLE_SORTER = new Comparator<PrimitiveChoiceSet<?>>()
	{

		public int compare(PrimitiveChoiceSet<?> arg0,
				PrimitiveChoiceSet<?> arg1)
		{
			return compareChoiceSets(arg0, arg1);
		}
	};

	public static int compareChoiceSets(PrimitiveChoiceSet<?> arg0,
			PrimitiveChoiceSet<?> arg1)
	{
		String base = arg0.getLSTformat();
		if (base == null)
		{
			if (arg1.getLSTformat() == null)
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
			if (arg1.getLSTformat() == null)
			{
				return 1;
			}
			else
			{
				return base.compareTo(arg1.getLSTformat());
			}
		}
	}

	/**
	 * Concatenates the LST format of the given Collection of PrimitiveChoiceSet
	 * objects into a String using the separator as the delimiter.
	 * 
	 * The LST format for each CDOMReference is determined by calling the
	 * getLSTformat() method on the PrimitiveChoiceSet.
	 * 
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection.
	 * 
	 * @param strings
	 *            An Collection of PrimitiveChoiceSet objects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the LST format of the
	 *         given Collection of PrimitiveChoiceSet objects
	 */
	public static <T extends PrimitiveChoiceSet<?>> String joinLstFormat(
			Collection<T> c, String separator)
	{
		if (c == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(c.size() * 10);

		boolean needjoin = false;

		for (PrimitiveChoiceSet<?> obj : c)
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
