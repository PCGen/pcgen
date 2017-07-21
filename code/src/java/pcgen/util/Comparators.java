/*
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.facade.core.InfoFacade;
import pcgen.gui2.util.treetable.TreeTableNode;

public final class Comparators
{

	private Comparators()
	{
	}

	private static final Comparator<Object> tSC =
			Comparator.comparing(o -> ((o == null) ? "" : o.toString()));
	private static final Comparator<Object> tSICC = (o1, o2) -> {
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareToIgnoreCase((o2 == null)
					? "" : o2.toString());

	};
	private static final Comparator<Object> tSICCol = (o1, o2) ->
	{
		String s1 = (o1 == null) ? "" : o1.toString();
		String s2 = (o2 == null) ? "" : o2.toString();
		return Collator.getInstance().compare(s1, s2);
	};
	private static final Comparator<Number> nC = (o1, o2) ->
	{
		final double d1 = o1.doubleValue();
		final double d2 = o2.doubleValue();

		if (d1 < d2)
		{
			return -1;
		}

		if (d1 > d2)
		{
			return 1;
		}

		return 0;
	}
	private static final Comparator<Date> dC = (o1, o2) ->
	{
		final long n1 = o1.getTime();
		final long n2 = o2.getTime();

		if (n1 < n2)
		{
			return -1;
		}

		if (n1 > n2)
		{
			return 1;
		}

		return 0;

	}
	private static final Comparator<Object> treeNodeComp = new TreeTableNodeComparator();

	private static Comparator<Object> toStringComparator()
	{
		return tSC;
	}

	public static Comparator<Object> toStringIgnoreCaseComparator()
	{
		return tSICC;
	}

	public static Comparator<Object> toStringIgnoreCaseCollator()
	{
		return tSICCol;
	}

	/**
	 * @return A comparator for use with the contents of tree table nodes. 
	 */
	private static Comparator<Object> treeTableNodeComparator()
	{
		return treeNodeComp;
	}

	private static Comparator<Integer> integerComparator()
	{
		return Integer::compareTo;
	}

	private static Comparator<Number> numberComparator()
	{
		return nC;
	}

	private static Comparator<Date> dateComparator()
	{
		return dC;
	}

	private static Comparator<String> ignoreCaseStringComparator()
	{
		return String.CASE_INSENSITIVE_ORDER;
	}

	public static <T> Comparator<? super T> getComparatorFor(Class<T> cls)
	{
		if (cls == Integer.class)
		{
			return (Comparator<? super T>) integerComparator();
		}
		if (cls.getSuperclass() == Number.class)
		{
			return (Comparator<? super T>) numberComparator();
		}
		if (cls == Date.class)
		{
			return (Comparator<? super T>) dateComparator();
		}
		if (cls == String.class)
		{
			return (Comparator<? super T>) ignoreCaseStringComparator();
		}
		if ((cls == TreeTableNode.class) || (cls == InfoFacade.class)
				|| (cls.getSuperclass() == InfoFacade.class))
		{
			return treeTableNodeComparator();
		}
		return toStringComparator();
	}

	/**
	 * A {@code Comparator} to compare tree table nodes. This respects SORTKEY for the contained object.
	 */
	private static final class TreeTableNodeComparator implements Comparator<Object>,
			Serializable
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			String key1 = getSortKey(o1); 
			String key2 = getSortKey(o2);
			final Collator collator = Collator.getInstance();

			if (!key1.equals(key2))
			{
				return collator.compare(key1, key2);
			}
			return collator.compare(String.valueOf(o1), String.valueOf(o2));
		}

		private static String getSortKey(Object obj1)
		{
			String key;
			if (obj1 == null)
			{
				key = "";
			}
			else if (obj1 instanceof CDOMObject)
			{
				CDOMObject co = (CDOMObject) obj1;
				key = co.get(StringKey.SORT_KEY);
				if (key == null)
				{
					key = co.getDisplayName();
				}
			}
			else if (obj1 instanceof SortKeyAware)
			{
				key = ((SortKeyAware) obj1).getSortKey();
			}
			else
			{
				key = obj1.toString();
			}
			return key;
		}
	}

}
