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
package pcgen.cdom.reference;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.LSTWriteable;


public final class ReferenceUtilities
{

	private ReferenceUtilities()
	{
		// Cannot construct utility class
	}

	public static <T extends LSTWriteable> String joinLstFormat(
		Collection<T> set, String separator)
	{
		if (set == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(set.size() * 10);

		boolean needjoin = false;

		for (LSTWriteable obj : set)
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

	public static <T extends CDOMObject> String joinDisplayFormat(
			Collection<CDOMReference<T>> set, String separator)
	{
		if (set == null)
		{
			return "";
		}

		Set<String> resultSet = new TreeSet<String>();
		for (CDOMReference<T> ref : set)
		{
			for (T obj : ref.getContainedObjects())
			{
				resultSet.add(obj.getDisplayName());
			}
		}

		return StringUtil.join(resultSet, separator);
	}
}
