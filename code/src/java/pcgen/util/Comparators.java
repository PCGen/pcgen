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

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Function;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.StringKey;
import pcgen.facade.core.InfoFacade;
import pcgen.gui2.util.treetable.TreeTableNode;

public final class Comparators
{

	private Comparators()
	{
	}

	private static final Comparator<Object> toStringComparator = new ToStringComparator();
	private static final Comparator<Object> treeTableNodeComparator = new TreeTableNodeComparator();

	public static <T> Comparator<? super T> getComparatorFor(Class<T> c)
	{
		if (c.getSuperclass() == Number.class)
		{
			return (Comparator<? super T>) Comparator.comparing(Function.identity());
		}
		else if (c == Date.class)
		{
			return (Comparator<? super T>) Comparator.comparingLong(Date::getTime);
		}
		else if (c == String.class)
		{
			return (Comparator<? super T>) String.CASE_INSENSITIVE_ORDER;
		}
		else if (c == TreeTableNode.class || c == InfoFacade.class || c.getSuperclass() == InfoFacade.class)
		{
			return treeTableNodeComparator;
		}
		return toStringComparator;
	}

	/**
	 * A {@code Comparator} to compare objects as
	 * {@code String}s.  This is particularly useful for applications
	 * such as maintaining a sorted {@code JComboBoxEx} and the like.
	 */
	private static final class ToStringComparator implements Comparator<Object>
	{
		@Override
		public int compare(Object o1, Object o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareTo((o2 == null) ? "" : o2.toString());
		}

	}

	/**
	 * A {@code Comparator} to compare tree table nodes. This respects SORTKEY for the contained object.
	 */
	private static final class TreeTableNodeComparator implements Comparator<Object>
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
