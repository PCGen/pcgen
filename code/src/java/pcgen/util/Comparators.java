/*
 * Comparators.java
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
 * 
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

/**
 *
 */
@SuppressWarnings("unchecked")
public final class Comparators
{

	private Comparators()
	{
	}

	private static final ToStringComparator tSC = new ToStringComparator();
	private static final ToStringIgnoreCaseComparator tSICC = new ToStringIgnoreCaseComparator();
	private static final ToStringIgnoreCaseCollator tSICCol = new ToStringIgnoreCaseCollator();
	private static final IntegerComparator iC = new IntegerComparator();
	private static final NumberComparator nC = new NumberComparator();
	private static final DateComparator dC = new DateComparator();
	private static final HashCodeComparator hCC = new HashCodeComparator();
	private static final TreeTableNodeComparator treeNodeComp = new TreeTableNodeComparator();

	public static Comparator<Object> toStringComparator()
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
	public static Comparator<Object> treeTableNodeComparator()
	{
		return treeNodeComp;
	}

	/**
	 * TODO: perhaps keep instance references to commonly used InverseComparators?
	 * @param comparator
	 * @return new InverseComparator instance
	 */
	public static <T> Comparator<T> inverseComparator(Comparator<T> comparator)
	{
		if (comparator instanceof InverseComparator)
		{
			return ((InverseComparator<T>) comparator).getComparator();
		}
		return new InverseComparator<>(comparator);

	}

	public static Comparator<Object> hashCodeComparator()
	{
		return hCC;
	}

	public static Comparator<Integer> integerComparator()
	{
		return iC;
	}

	public static Comparator<Number> numberComparator()
	{
		return nC;
	}

	public static Comparator<Date> dateComparator()
	{
		return dC;
	}

	public static Comparator<String> ignoreCaseStringComparator()
	{
		return String.CASE_INSENSITIVE_ORDER;
	}

	public static <T> Comparator<? super T> getComparatorFor(Class<T> c)
	{
		if (c == Integer.class)
		{
			return (Comparator<? super T>) integerComparator();
		}
		else if (c.getSuperclass() == Number.class)
		{
			return (Comparator<? super T>) numberComparator();
		}
		else if (c == Date.class)
		{
			return (Comparator<? super T>) dateComparator();
		}
		else if (c == String.class)
		{
			return (Comparator<? super T>) ignoreCaseStringComparator();
		}
		else if (c == TreeTableNode.class || c == InfoFacade.class
			|| c.getSuperclass() == InfoFacade.class)
		{
			return treeTableNodeComparator();
		}
		return toStringComparator();
	}

	/**
	 * A {@code Comparator} to compare objects as
	 * {@code String}s.  This is particularly useful for applications
	 * such as maintaining a sorted {@code JComboBoxEx} and the like.
	 *
	 */
	private static final class ToStringComparator implements Comparator<Object>,
			Serializable
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareTo((o2 == null) ? ""
					: o2.toString());
		}

	}

	/**
	 * A {@code Comparator} to compare objects as
	 * {@code String}s ignoring case.  This is particularly useful
	 * for applications such as maintaining a sorted
	 * {@code JComboBoxEx} and the like.
	 *
	 */
	private static final class ToStringIgnoreCaseComparator implements
			Comparator<Object>, Serializable
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareToIgnoreCase((o2 ==
					null)
					? ""
					: o2.toString());
		}

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

		private String getSortKey(Object obj1)
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

	private static final class ToStringIgnoreCaseCollator implements
			Comparator<Object>, Serializable
	{

		private static final Collator collator = Collator.getInstance();

		@Override
		public int compare(Object o1, Object o2)
		{
			String s1 = (o1 == null) ? "" : o1.toString();
			String s2 = (o2 == null) ? "" : o2.toString();
			return collator.compare(s1, s2);
		}

	}

	private static final class IntegerComparator implements Comparator<Integer>
	{

		@Override
		public int compare(Integer o1, Integer o2)
		{
			return o1.compareTo(o2);
		}

	}

	private static final class NumberComparator implements Comparator<Number>
	{

		@Override
		public int compare(Number o1, Number o2)
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

	}

	private static final class DateComparator implements Comparator<Date>
	{

		@Override
		public int compare(Date o1, Date o2)
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

	}

	private static final class HashCodeComparator implements Comparator<Object>
	{

		@Override
		public int compare(Object o1, Object o2)
		{
			return iC.compare(o1.hashCode(), o2.hashCode());
		}

	}

	/**
	 *
	 * @param E
	 */
	private static final class InverseComparator<E> implements Comparator<E>,
			Serializable
	{

		private final Comparator<E> comparator;

		public InverseComparator(Comparator<E> comparator)
		{
			this.comparator = comparator;
		}

		public Comparator<E> getComparator()
		{
			return comparator;
		}

		@Override
		public int compare(E o1, E o2)
		{
			return -comparator.compare(o1, o2);
		}

	}

}
