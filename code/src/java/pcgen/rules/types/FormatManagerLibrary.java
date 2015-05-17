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

/**
 * A FormatManagerLibrary stores the FormatManagers that can be used. These are
 * stored by their identifier String (e.g. "STRING" for StringManager).
 */
public final class FormatManagerLibrary
{

	private FormatManagerLibrary()
	{
		//Utility class must not be constructed
	}

	private static CaseInsensitiveMap<FormatManager<?>> managerMap =
			new CaseInsensitiveMap<FormatManager<?>>();

	/**
	 * Gets the FormatManager for the given String identifying a format of
	 * object.
	 * 
	 * @param idType
	 *            The String identifying the format for which the FormatManager
	 *            should be returned
	 * @return The FormatManager for the given String identifying a format of
	 *         object
	 * @throws IllegalArgumentException
	 *             if the given format does not have an associated FormatManager
	 */
	public static FormatManager<?> getFormatManager(String idType)
	{
		FormatManager<?> manager = managerMap.get(idType);
		if (manager == null)
		{
			throw new IllegalArgumentException(
				"No FormatManager available for " + idType);
		}
		return manager;
	}

	/**
	 * Adds a FormatManager to the FormatManagerLibrary.
	 * 
	 * @param fm The FormatManager to be added to this FormatManagerLibrary
	 * @throws IllegalArgumentException
	 *             if this FormatManagerLibrary already has a FormatManager with
	 *             a matching identifier
	 */
	public static void addFormatManager(FormatManager<?> fm)
	{
		String idType = fm.getIdentifierType();
		FormatManager<?> manager = managerMap.get(idType);
		if (manager != null)
		{
			throw new IllegalArgumentException(
				"Cannot set another Type Manager for " + idType);
		}
		managerMap.put(idType, fm);
	}

	/**
	 * Resets the FormatManagerLibrary, to be used when a test needs a clean
	 * FormatManagerLibrary.
	 */
	public static void reset()
	{
		managerMap.clear();
	}
}
