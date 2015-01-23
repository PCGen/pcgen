/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.types;

import pcgen.base.util.CaseInsensitiveMap;

public class FormatManagerLibrary
{

	private static CaseInsensitiveMap<FormatManager<?>> managerMap =
			new CaseInsensitiveMap<FormatManager<?>>();

	public static FormatManager<?> getFormatManager(String idType)
	{
		FormatManager<?> manager = managerMap.get(idType);
		if (manager == null)
		{
			throw new IllegalArgumentException("No FormatManager available for "
				+ idType);
		}
		return manager;
	}

	public static void addFormatManager(FormatManager<?> tm)
	{
		String idType = tm.getIdentifierType();
		FormatManager<?> manager = managerMap.get(idType);
		if (manager != null)
		{
			throw new IllegalArgumentException(
				"Cannot set another Type Manager for " + idType);
		}
		managerMap.put(idType, tm);
	}

	public static void reset()
	{
		managerMap.clear();
	}
}
