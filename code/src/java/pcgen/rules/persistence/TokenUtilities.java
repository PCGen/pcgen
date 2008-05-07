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
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.LSTWriteable;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public final class TokenUtilities
{

	public static final Comparator<LSTWriteable> WRITEABLE_SORTER =
			new Comparator<LSTWriteable>()
			{

				public int compare(LSTWriteable arg0, LSTWriteable arg1)
				{
					return compareWriteable(arg0, arg1);
				}
			};

	public static final Comparator<CDOMReference<?>> REFERENCE_SORTER =
			new Comparator<CDOMReference<?>>()
			{

				public int compare(CDOMReference<?> arg0, CDOMReference<?> arg1)
				{
					return compareRefs(arg0, arg1);
				}
			};

//	public static final Comparator<CategorizedCDOMReference<?>> CAT_REFERENCE_SORTER =
//			new Comparator<CategorizedCDOMReference<?>>()
//			{
//
//				public int compare(CategorizedCDOMReference<?> arg0,
//					CategorizedCDOMReference<?> arg1)
//				{
//					if (arg0 instanceof CDOMSingleRef)
//					{
//						if (!(arg1 instanceof CDOMSingleRef))
//						{
//							return -1;
//						}
//						return arg0.getName().compareTo(arg1.getName());
//					}
//					if (arg1 instanceof CDOMSingleRef)
//					{
//						return 1;
//					}
//					return arg0.getName().compareTo(arg1.getName());
//				}
//			};

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
				.errorPrint("Type may not start or end with . in: " + subStr);
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

//	public static <T extends CDOMObject & CategorizedCDOMObject<T>> CDOMReference<T> getTypeOrPrimitive(
//		LoadContext context, Class<T> cl, Category<T> cat, String s)
//	{
//		if (s.startsWith(Constants.LST_TYPE_OLD)
//			|| s.startsWith(Constants.LST_TYPE))
//		{
//			String subStr = s.substring(5);
//			if (subStr.length() == 0)
//			{
//				Logging.errorPrint("Type may not be empty in: " + s);
//				return null;
//			}
//			if (subStr.charAt(0) == '.'
//				|| subStr.charAt(subStr.length() - 1) == '.')
//			{
//				Logging.errorPrint("Type may not start or end with . in: " + s);
//				return null;
//			}
//			String[] types = subStr.split("\\.");
//			for (String type : types)
//			{
//				if (type.length() == 0)
//				{
//					Logging
//						.errorPrint("Attempt to acquire empty Type in: " + s);
//					return null;
//				}
//			}
//			return context.ref.getCDOMTypeReference(cl, cat, types);
//		}
//		else
//		{
//			return context.ref.getCDOMReference(cl, cat, s);
//		}
//	}
//
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

	public static int compareWriteable(LSTWriteable arg0, LSTWriteable arg1)
	{
		if (arg0 instanceof CDOMSingleRef || arg0 instanceof CDOMObject)
		{
			if (!(arg1 instanceof CDOMSingleRef || arg1 instanceof CDOMObject))
			{
				return -1;
			}
			return compareLSTformats(arg0, arg1);
		}
		if (arg1 instanceof CDOMSingleRef)
		{
			return 1;
		}
		/*
		 * BUG TODO This is NOT Consistent with equals :(
		 */
		return compareLSTformats(arg0, arg1);
	}

	private static int compareLSTformats(LSTWriteable arg0, LSTWriteable arg1)
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

//	public static <T extends CDOMObject> CDOMReference<T> getCompoundReference(
//		LoadContext context, Class<T> cl, String value)
//	{
//		if (value == null || value.length() == 0)
//		{
//			Logging
//				.errorPrint("Compound Reference arguments may not be empty : "
//					+ value);
//			return null;
//		}
//		if (value.indexOf(',') == -1)
//		{
//			return getTypeOrPrimitive(context, cl, value);
//		}
//		if (value.charAt(0) == ',')
//		{
//			Logging
//				.errorPrint("Compound Reference arguments may not start with , : "
//					+ value);
//			return null;
//		}
//		if (value.charAt(value.length() - 1) == ',')
//		{
//			Logging
//				.errorPrint("Compound Reference arguments may not end with , : "
//					+ value);
//			return null;
//		}
//		if (value.indexOf(",,") != -1)
//		{
//			Logging
//				.errorPrint("Compound Reference arguments uses double separator ,, : "
//					+ value);
//			return null;
//		}
//		StringTokenizer st = new StringTokenizer(value, ",");
//		CDOMCompoundAndReference<T> andRef =
//				new CDOMCompoundAndReference<T>(cl, value);
//		while (st.hasMoreTokens())
//		{
//			String tokString = st.nextToken();
//			// TODO Need to implement !TYPE parsing and how that is handled in
//			// the CompoundReference
//			CDOMReference<T> ref = getTypeOrPrimitive(context, cl, tokString);
//			if (ref == null)
//			{
//				Logging
//					.errorPrint("Compound Reference arguments has invalid reference : "
//						+ tokString);
//				return null;
//			}
//			andRef.addReference(ref);
//		}
//		return andRef;
//	}
}
