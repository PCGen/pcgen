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
 * Created on Feb 14, 2008, 11:23:02 PM
 */
package pcgen.util;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Date;

/**
 *
 * @author Connor Petty <mistercpp2000@gmail.com>
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

	public static <T> Comparator<T> toStringComparator()
	{
		return tSC;
	}

	public static <T> Comparator<T> toStringIgnoreCaseComparator()
	{
		return tSICC;
	}

	public static <T> Comparator<T> toStringIgnoreCaseCollator()
	{
		return tSICCol;
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
			return ((InverseComparator) comparator).getComparator();
		}
		return new InverseComparator<T>(comparator);

	}

	public static <T> Comparator<T> hashCodeComparator()
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

	@SuppressWarnings("unchecked")
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
		return toStringComparator();
	}

	/**
	 * A <code>Comparator</code> to compare objects as
	 * <code>String</code>s.  This is particularly useful for applications
	 * such as maintaining a sorted <code>JComboBoxEx</code> and the like.
	 *
	 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
	 * @version $Revision: 2112 $
	 */
	private static final class ToStringComparator<E> implements Comparator<E>,
			Serializable
	{

		/** Constructs a <code>StringComparator</code>. */
		public ToStringComparator()
		{
			// TODO: Exception needs to be handled
		}

		/** {@inheritDoc} */
		public int compare(E o1, E o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareTo((o2 == null) ? ""
					: o2.toString());
		}

	}

	/**
	 * A <code>Comparator</code> to compare objects as
	 * <code>String</code>s ignoring case.  This is particularly useful
	 * for applications such as maintaining a sorted
	 * <code>JComboBoxEx</code> and the like.
	 *
	 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
	 * @version $Revision: 2112 $
	 */
	private static final class ToStringIgnoreCaseComparator<E> implements Comparator<E>,
			Serializable
	{

		/** Constructs a <code>StringIgnoreCaseComparator</code>. */
		public ToStringIgnoreCaseComparator()
		{
			// TODO: Exception needs to be handled
		}

		/** {@inheritDoc} */
		public int compare(E o1, E o2)
		{
			// Treat null as the empty string.
			return ((o1 == null) ? "" : o1.toString()).compareToIgnoreCase((o2 ==
					null)
					? ""
					: o2.toString());
		}

	}

	private static final class ToStringIgnoreCaseCollator<E> implements Comparator<E>, Serializable
	{

		private static final Collator collator = Collator.getInstance();

		public int compare(E o1, E o2)
		{
			String s1 = (o1 == null) ? "" : o1.toString();
			String s2 = (o2 == null) ? "" : o2.toString();
			return collator.compare(s1, s2);
		}

	}

	private static final class IntegerComparator implements Comparator<Integer>
	{

		public int compare(Integer o1, Integer o2)
		{
			return o1.compareTo(o2);
		}

	}

	private static final class NumberComparator implements Comparator<Number>
	{

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

	private static final class HashCodeComparator implements Comparator
	{

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

		public int compare(E o1, E o2)
		{
			return -comparator.compare(o1, o2);
		}

	}

}
