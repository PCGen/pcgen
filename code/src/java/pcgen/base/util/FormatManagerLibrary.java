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
package pcgen.base.util;

import java.util.HashMap;
import java.util.Map;

import pcgen.base.format.ArrayFormatManager;
import pcgen.base.format.BooleanManager;
import pcgen.base.format.NumberManager;
import pcgen.base.format.OrderedPairManager;
import pcgen.base.format.StringManager;

/**
 * A FormatManagerLibrary stores the FormatManagers that can be used. These are
 * stored by their identifier String (e.g. "STRING" for StringManager).
 */
public final class FormatManagerLibrary
{

	public FormatManagerLibrary()
	{
		reset();
	}

	/**
	 * A Map storing the FormatManagers by (case-insensitive) name
	 */
	private CaseInsensitiveMap<FormatManager<?>> managerNameMap =
			new CaseInsensitiveMap<FormatManager<?>>();

	/**
	 * A Map storing the FormatManagers by (case-insensitive) name
	 */
	private Map<Class<?>, FormatManager<?>> managerClassMap =
			new HashMap<Class<?>, FormatManager<?>>();

	/**
	 * Gets the FormatManager for the given String identifying a format of
	 * object.
	 * 
	 * @param formatName
	 *            The String identifying the format for which the FormatManager
	 *            should be returned
	 * @return The FormatManager for the given String identifying a format of
	 *         object
	 * @throws IllegalArgumentException
	 *             if the given format does not have an associated FormatManager
	 */
	public FormatManager<?> getFormatManager(String formatName)
	{
		FormatManager<?> fmtManager = managerNameMap.get(formatName);
		if ((fmtManager == null)
			&& formatName.regionMatches(true, 0, "ARRAY[", 0, 6)
			&& formatName.endsWith("]"))
		{
			String subName = formatName.substring(6, formatName.length() - 1);
			if (subName.regionMatches(true, 0, "ARRAY[", 0, 6))
			{
				throw new IllegalArgumentException(
					"Cannot built mulit-dimensional arrays, request was: "
						+ formatName);
			}
			FormatManager<?> subFmtManager = managerNameMap.get(subName);
			if (subFmtManager == null)
			{
				throw new IllegalArgumentException(
					"No FormatManager available for " + subName
						+ " when requesting " + formatName);
			}
			/*
			 * Is this comma (separator for the Array Format instruction parser)
			 * the only thing preventing multi-dimensional arrays?
			 */
			fmtManager = new ArrayFormatManager<>(subFmtManager, ',');
			addFormatManager(fmtManager);
		}
		else
		{
			throw new IllegalArgumentException(
				"No FormatManager available for " + formatName);
		}
		return fmtManager;
	}

	/**
	 * Gets the FormatManager for the given Class indicating a format of object.
	 * 
	 * @param format
	 *            The Class identifying the type of object for which the
	 *            FormatManager should be returned
	 * @return The FormatManager for the given Class indicating a format of
	 *         object
	 * @throws IllegalArgumentException
	 *             if the given format does not have an associated FormatManager
	 */
	public <T> FormatManager<T> getFormatManager(Class<T> format)
	{
		@SuppressWarnings("unchecked")
		FormatManager<T> fmtManager = (FormatManager<T>) managerClassMap.get(format);
		if (fmtManager == null)
		{
			if (!format.isArray())
			{
				throw new IllegalArgumentException(
					"No FormatManager available for " + format);
			}
			Class<?> componentType = format.getComponentType();
			if (componentType.isArray())
			{
				throw new IllegalArgumentException(
					"Cannot built mulit-dimensional arrays, request was: "
						+ format);
			}
			FormatManager<?> subFmtManager = managerNameMap.get(componentType);
			if (subFmtManager == null)
			{
				throw new IllegalArgumentException(
					"No FormatManager available for " + componentType
						+ " when requesting " + format);
			}
			/*
			 * Is this comma (separator for the Array Format instruction parser)
			 * the only thing preventing multi-dimensional arrays?
			 */
			@SuppressWarnings("unchecked")
			FormatManager<T> arrayFmtManager =
					(FormatManager<T>) new ArrayFormatManager<>(subFmtManager,
						',');
			addFormatManager(arrayFmtManager);
			fmtManager = arrayFmtManager;
		}
		return fmtManager;
	}

	/**
	 * Adds a FormatManager to the FormatManagerLibrary.
	 * 
	 * @param fmtManager
	 *            The FormatManager to be added to this FormatManagerLibrary
	 * @throws IllegalArgumentException
	 *             if this FormatManagerLibrary already has a FormatManager with
	 *             a matching identifier
	 */
	public void addFormatManager(FormatManager<?> fmtManager)
	{
		String fmIdent = fmtManager.getIdentifierType();
		if (fmIdent == null)
		{
			throw new IllegalArgumentException(
				"Cannot set a FormatManager with no identifier (was nominally for: "
					+ fmtManager.getManagedClass() + ")");
		}
		FormatManager<?> fmtManagerByName = managerNameMap.get(fmIdent);
		if ((fmtManagerByName != null) && !fmtManagerByName.equals(fmtManager))
		{
			throw new IllegalArgumentException(
				"Cannot set another Format Manager for " + fmIdent);
		}
		Class<?> fmFormat = fmtManager.getManagedClass();
		FormatManager<?> fmtManagerByType = managerClassMap.get(fmFormat);
		if ((fmtManagerByType != null) && !fmtManagerByType.equals(fmtManager))
		{
			throw new IllegalArgumentException(
				"Cannot set another Format Manager for "
					+ fmtManager.getManagedClass().getCanonicalName());
		}
		managerNameMap.put(fmIdent, fmtManager);
		managerClassMap.put(fmtManager.getManagedClass(), fmtManager);
	}

	/**
	 * Resets the FormatManagerLibrary, to be used when a test needs a clean
	 * FormatManagerLibrary.
	 */
	public void reset()
	{
		managerNameMap.clear();
		managerClassMap.clear();
		addFormatManager(new NumberManager());
		addFormatManager(new StringManager());
		addFormatManager(new BooleanManager());
		addFormatManager(new OrderedPairManager());
	}
}
