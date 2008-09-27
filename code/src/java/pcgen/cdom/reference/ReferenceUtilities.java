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

import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

/**
 * ReferenceUtilities is a utility class designed to provide utility methods
 * when working with pcgen.cdom.base.CDOMReference Objects
 */
public final class ReferenceUtilities
{

	private final static Collator COLLATOR = Collator.getInstance();

	public static final Comparator<CDOMReference<?>> REFERENCE_SORTER = new Comparator<CDOMReference<?>>()
	{
	
		public int compare(CDOMReference<?> arg0, CDOMReference<?> arg1)
		{
			return compareRefs(arg0, arg1);
		}
	};

	private ReferenceUtilities()
	{
		// Cannot construct utility class
	}

	/**
	 * Concatenates the LST format of the given Collection of CDOMReference
	 * objects into a String using the separator as the delimiter.
	 * 
	 * The LST format for each CDOMReference is determined by calling the
	 * getLSTformat() method on the CDOMReference.
	 * 
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection.
	 * 
	 * @param strings
	 *            An Collection of CDOMReference objects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the LST format of the
	 *         given Collection of CDOMReference objects
	 */
	public static <T extends CDOMReference<?>> String joinLstFormat(
			Collection<T> c, String separator)
	{
		if (c == null)
		{
			return "";
		}

		final StringBuilder result = new StringBuilder(c.size() * 10);

		boolean needjoin = false;

		for (CDOMReference<?> obj : c)
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
	 * Concatenates the Display Name of the contents of the given Collection of
	 * CDOMReference objects into a String using the separator as the delimiter.
	 * 
	 * Each CDOMReference in the given Collection is expanded to the contained
	 * objects, and each of those contained CDOMObjects has getDisplayName()
	 * called to establish the Display Name of the CDOMObject.
	 * 
	 * The LST format for each CDOMReference is determined by calling the
	 * getLSTformat() method on the CDOMReference.
	 * 
	 * The items will be joined in the order determined by the ordering of the
	 * given Collection and the getContainedObjects() method of the
	 * CDOMReferences contained in the given Collection.
	 * 
	 * @param strings
	 *            An Collection of CDOMReference objects
	 * @param separator
	 *            The separating string
	 * @return A 'separator' separated String containing the Display Name of the
	 *         given CDOMObjects contained within the given Collection of
	 *         CDOMReference objects
	 */
	public static <T extends CDOMObject> String joinDisplayFormat(
			Collection<CDOMReference<T>> c, String separator)
	{
		if (c == null)
		{
			return "";
		}

		Set<String> resultSet = new TreeSet<String>();
		for (CDOMReference<T> ref : c)
		{
			for (T obj : ref.getContainedObjects())
			{
				resultSet.add(obj.getDisplayName());
			}
		}

		return StringUtil.join(resultSet, separator);
	}

	public static int compareRefs(CDOMReference<?> arg0, CDOMReference<?> arg1)
	{
		if (arg0 instanceof CDOMSingleRef)
		{
			if (!(arg1 instanceof CDOMSingleRef))
			{
				return -1;
			}
			return COLLATOR.compare(arg0.getName(), arg1.getName());
		}
		if (arg1 instanceof CDOMSingleRef)
		{
			return 1;
		}
		return COLLATOR.compare(arg0.getName(), arg1.getName());
	}
}
