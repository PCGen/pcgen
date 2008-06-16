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
package pcgen.rules.persistence;

import java.util.Comparator;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.CategorizedCDOMObject;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class TokenUtilities
{

	public static final Comparator<CDOMReference<?>> REFERENCE_SORTER = new Comparator<CDOMReference<?>>()
	{

		public int compare(CDOMReference<?> arg0, CDOMReference<?> arg1)
		{
			return compareRefs(arg0, arg1);
		}
	};
	
	public static final Comparator<CDOMObject> CDOM_SORTER =
		new Comparator<CDOMObject>()
		{

			public int compare(CDOMObject arg0, CDOMObject arg1)
			{
				return compareKeys(arg0, arg1);
			}
		};



	// public static final Comparator<CategorizedCDOMReference<?>>
	// CAT_REFERENCE_SORTER =
	// new Comparator<CategorizedCDOMReference<?>>()
	// {
	//
	// public int compare(CategorizedCDOMReference<?> arg0,
	// CategorizedCDOMReference<?> arg1)
	// {
	// if (arg0 instanceof CDOMSingleRef)
	// {
	// if (!(arg1 instanceof CDOMSingleRef))
	// {
	// return -1;
	// }
	// return arg0.getName().compareTo(arg1.getName());
	// }
	// if (arg1 instanceof CDOMSingleRef)
	// {
	// return 1;
	// }
	// return arg0.getName().compareTo(arg1.getName());
	// }
	// };

	private TokenUtilities()
	{
		// Can't instantiate utility classes
	}

	public static <T extends CDOMObject> CDOMReference<T> getTypeOrPrimitive(
			LoadContext context, Class<T> cl, String s)
	{
		if (s.startsWith(Constants.LST_TYPE_OLD)
				|| s.startsWith(Constants.LST_TYPE))
		{
			return getTypeReference(context, cl, s.substring(5));
		}
		else
		{
			return context.ref.getCDOMReference(cl, s);
		}
	}

	public static <T extends CDOMObject> CDOMReference<T> getTypeReference(
			LoadContext context, Class<T> cl, String subStr)
	{
		if (subStr.length() == 0)
		{
			Logging.errorPrint("Type may not be empty in: " + subStr);
			return null;
		}
		if (subStr.charAt(0) == '.'
				|| subStr.charAt(subStr.length() - 1) == '.')
		{
			Logging
					.errorPrint("Type may not start or end with . in: "
							+ subStr);
			return null;
		}
		String[] types = subStr.split("\\.");
		for (String type : types)
		{
			if (type.length() == 0)
			{
				Logging.errorPrint("Attempt to acquire empty Type "
						+ "(the type String contains '..') in: " + subStr);
				return null;
			}
		}
		return context.ref.getCDOMTypeReference(cl, types);
	}

	public static <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMReference<T> getTypeOrPrimitive(
			LoadContext context, Class<T> cl, Category<T> cat, String s)
	{
		if (s.startsWith(Constants.LST_TYPE_OLD)
				|| s.startsWith(Constants.LST_TYPE))
		{
			String subStr = s.substring(5);
			if (subStr.length() == 0)
			{
				Logging.errorPrint("Type may not be empty in: " + s);
				return null;
			}
			if (subStr.charAt(0) == '.'
					|| subStr.charAt(subStr.length() - 1) == '.')
			{
				Logging.errorPrint("Type may not start or end with . in: " + s);
				return null;
			}
			String[] types = subStr.split("\\.");
			for (String type : types)
			{
				if (type.length() == 0)
				{
					Logging
							.errorPrint("Attempt to acquire empty Type in: "
									+ s);
					return null;
				}
			}
			return context.ref.getCDOMTypeReference(cl, cat, types);
		}
		else
		{
			return context.ref.getCDOMReference(cl, cat, s);
		}
	}

	public static int compareRefs(CDOMReference<?> arg0, CDOMReference<?> arg1)
	{
		if (arg0 instanceof CDOMSingleRef)
		{
			if (!(arg1 instanceof CDOMSingleRef))
			{
				return -1;
			}
			return arg0.getName().compareTo(arg1.getName());
		}
		if (arg1 instanceof CDOMSingleRef)
		{
			return 1;
		}
		return arg0.getName().compareTo(arg1.getName());
	}

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
