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

@SuppressWarnings("unchecked")
public final class Comparators
{

	private Comparators()
	{
	}

	private static final Comparator<Object> tSC = new ToStringComparator();
	private static final Comparator<Object> tSICC = new ToStringIgnoreCaseComparator();
	private static final Comparator<Object> tSICCol = new ToStringIgnoreCaseCollator();
	private static final Comparator<Integer> iC = Comparator.comparingInt(o -> o);
	private static final Comparator<Number> nC = Comparator.comparing(Number::doubleValue);
	private static final Comparator<Date> DC = Comparator.comparing(Date::getTime);
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

	public static <T> Comparator<? super T> getComparatorFor(Class<T> c)
	{
		if (c == Integer.class)
		{
			return (Comparator<? super T>) iC;
		}
		else if (c.getSuperclass() == Number.class)
		{
			return (Comparator<? super T>) nC;
		}
		else if (c == Date.class)
		{
			return (Comparator<? super T>)DC;
		}
		else if (c == String.class)
		{
			return (Comparator<? super T>)String.CASE_INSENSITIVE_ORDER;
		}
		else if ((c == TreeTableNode.class) || (c == InfoFacade.class) || (c.getSuperclass() == InfoFacade.class))
		{
			return treeNodeComp;
		}
		return toStringComparator();
	}

	/**
	 * A {@code Comparator} to compare objects as
	 * {@code String}s.  This is particularly useful for applications
	 * such as maintaining a sorted {@code JComboBoxEx} and the like.
	 */
	private static final class ToStringComparator implements Comparator<Object>, Serializable
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareTo((o2 == null) ? "" : o2.toString());
		}

	}

	/**
	 * A {@code Comparator} to compare objects as
	 * {@code String}s ignoring case.  This is particularly useful
	 * for applications such as maintaining a sorted
	 * {@code JComboBoxEx} and the like.
	 */
	private static final class ToStringIgnoreCaseComparator implements Comparator<Object>, Serializable
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareToIgnoreCase((o2 == null) ? "" : o2.toString());
		}

	}

	/**
	 * A {@code Comparator} to compare tree table nodes. This respects SORTKEY for the contained object.
	 */
	private static final class TreeTableNodeComparator implements Comparator<Object>, Serializable
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
			else if (obj1 instanceof CDOMObject co)
			{
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

	private static final class ToStringIgnoreCaseCollator implements Comparator<Object>, Serializable
	{

		private static final Collator COLLATOR = Collator.getInstance();

		@Override
		public int compare(Object o1, Object o2)
		{
			String s1 = (o1 == null) ? "" : o1.toString();
			String s2 = (o2 == null) ? "" : o2.toString();
			return COLLATOR.compare(s1, s2);
		}

	}

}
