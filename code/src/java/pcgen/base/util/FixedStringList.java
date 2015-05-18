/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.RandomAccess;

import pcgen.base.lang.StringUtil;

/**
 * A FixedStringList is a fixed-length java.util.List<String>. The size of the
 * FixedStringList is set at construction and cannot be modified. Entries which
 * are not set are null.
 * 
 * A FixedStringList will always report the size defined at construction and
 * will always iterate over the null values.
 */
public class FixedStringList extends AbstractList<String> implements
		List<String>, RandomAccess
{

	/**
	 * Provides a Comparator for FixedStringList objects that will sort the
	 * contents in a Case Sensitive order. Note that null sorts first (before
	 * any non-null Strings)
	 */
	@SuppressWarnings("PMD.LongVariable")
	public static final Comparator<FixedStringList> CASE_SENSITIVE_ORDER = new Comparator<FixedStringList>()
	{
		@Override
		public int compare(FixedStringList fsl1, FixedStringList fsl2)
		{
			return FixedStringList.compare(fsl1, fsl2,
					StringUtil.CASE_SENSITIVE_ORDER);
		}
	};

	/**
	 * Provides a Comparator for FixedStringList objects that will sort the
	 * contents in a Case Insensitive order. Note that null sorts first (before
	 * any non-null Strings)
	 */
	@SuppressWarnings("PMD.LongVariable")
	public static final Comparator<FixedStringList> CASE_INSENSITIVE_ORDER = new Comparator<FixedStringList>()
	{
		@Override
		public int compare(FixedStringList fsl1, FixedStringList fsl2)
		{
			return FixedStringList.compare(fsl1, fsl2,
					String.CASE_INSENSITIVE_ORDER);
		}
	};

	/**
	 * The String array underlying the FixedStringList.
	 */
	private final String[] array;

	/**
	 * Creates a new FixedStringList of the given size. All values in the
	 * FixedStringList remain the default (null).
	 * 
	 * @param size
	 *            The size of the FixedStringList to be constructed.
	 */
	public FixedStringList(int size)
	{
		array = new String[size];
	}

	/**
	 * Creates a new FixedStringList from the given String Collection. The
	 * FixedStringList will have a size equal to the size of the given
	 * Collection and the contents of this FixedStringList will match the order
	 * of the given Collection. null values are allowed and will be included in
	 * the FixedStringList.
	 * 
	 * This constructor is value-semantic, in that the given Collection will not
	 * be modified by this constructor, and no reference to the given Collection
	 * will be maintained. (References to the Strings within the Collection will
	 * be maintained, but as Strings are immutable objects, that is a 'safe'
	 * operation)
	 * 
	 * @param collection
	 *            The String Collection to be used to initialize the size and
	 *            contents of this FixedStringList
	 */
	public FixedStringList(Collection<String> collection)
	{
		array = collection.toArray(new String[collection.size()]);
	}

	/**
	 * Creates a new FixedStringList from the given String Array. The
	 * FixedStringList will have a size equal to the size of the given Array and
	 * the contents of this FixedStringList will match the order of the given
	 * Array. null values are allowed and will be included in the
	 * FixedStringList.
	 * 
	 * This constructor is value-semantic, in that the given Array will not be
	 * modified by this constructor, and no reference to the given Array will be
	 * maintained. (References to the Strings within the Array will be
	 * maintained, but as Strings are immutable objects, that is a 'safe'
	 * operation)
	 * 
	 * @param stringArray
	 *            The String Array to be used to initialize the size and
	 *            contents of this FixedStringList
	 * 
	 */
	public FixedStringList(String... stringArray)
	{
		array = new String[stringArray.length];
		System.arraycopy(stringArray, 0, array, 0, stringArray.length);
	}

	/**
	 * Adds a new String to this FixedStringList. This new value will replace
	 * the first null value within the FixedStringList.
	 * 
	 * Per the java.util.List specification, you should test the return value of
	 * this method! This method will return false if there is no null value in
	 * the FixedStringList (indicating that the add failed). No Error or
	 * Exception will be generated.
	 * 
	 * @see java.util.AbstractList#add(java.lang.Object)
	 */
	@Override
	public boolean add(String element)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == null)
			{
				array[i] = element;
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a Collection of Strings to this FixedStringList. These values will
	 * replace the first null values within the FixedStringList (in order
	 * returned by the Iterator provided by the Collection).
	 * 
	 * This method is value-semantic, in that the given Collection will not be
	 * modified by this constructor, and no reference to the given Collection
	 * will be maintained. (References to the Strings within the Collection will
	 * be maintained, but as Strings are immutable objects, that is a 'safe'
	 * operation)
	 * 
	 * Per the java.util.List specification, you should test the return value of
	 * this method! This method will return false if there is are insufficient
	 * null values in the FixedStringList to fit all of the contents of the
	 * given Collection (indicating that the addAll failed). No Error or
	 * Exception will be generated.
	 * 
	 * @see java.util.AbstractList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int index, Collection<? extends String> collection)
	{
		for (String s : collection)
		{
			if (!add(s))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Removes the String at the given index from this FixedStringList. The
	 * value is replaced by null. The contents of the FixedStringList are not
	 * consolidated, meaning the null value will continue to exist in the
	 * FixedStringList at exactly the index where the value was removed.
	 * 
	 * @see java.util.AbstractList#remove(int)
	 */
	@Override
	public String remove(int index)
	{
		String old = array[index];
		array[index] = null;
		return old;
	}

	/**
	 * Sets the String at the given index to the given value.
	 * 
	 * @see java.util.AbstractList#set(int, java.lang.Object)
	 */
	@Override
	public String set(int index, String element)
	{
		String old = array[index];
		array[index] = element;
		return old;
	}

	/**
	 * Returns the String at the given index of this FixedStringList (may be
	 * null if there is no String present at the given index and the index is
	 * greater than or equal to 0 and less than or equal to the size of this
	 * FixedStringList minus 1.
	 * 
	 * @see java.util.AbstractList#get(int)
	 */
	@Override
	public String get(int index)
	{
		return array[index];
	}

	/**
	 * Returns the size of this FixedStringList. Will always be the size defined
	 * during the construction of this FixedStringList, regardless of how many
	 * entries in the FixedStringList are set to zero.
	 * 
	 * @see java.util.AbstractCollection#size()
	 */
	@Override
	public int size()
	{
		return array.length;
	}

	/**
	 * Returns true is this FixedStringList is equal to the given Object. This
	 * method is consistent with the equals behavior defined in java.util.List
	 * (meaning this will return true if this FixedStringList is the same size
	 * as another java.util.List and has identical contents [in identical
	 * order])
	 * 
	 * @see java.util.AbstractList#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof FixedStringList)
		{
			FixedStringList other = (FixedStringList) obj;
			return Arrays.deepEquals(array, other.array);
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode()
	{
		if (array.length == 0)
		{
			return 0;
		}
		return array.length + 29 * (array[0] == null ? 0 : array[0].hashCode());
	}

	/**
	 * Returns true is this FixedStringList is equal to the given Object,
	 * ignoring case in the underlying String objects. This method is consistent
	 * with the equalsIgnoreCase behavior defined in java.lang.String
	 * 
	 * @see java.util.AbstractList#equals(java.lang.Object)
	 */
	public boolean equalsIgnoreCase(FixedStringList fsl)
	{
		int thisArrayLength = array.length;
		String[] otherArray = fsl.array;
		if (otherArray.length != thisArrayLength)
		{
			return false;
		}
		for (int i = 0; i < thisArrayLength; i++)
		{
			String thisItem = array[i];
			if (thisItem == null)
			{
				if (otherArray[i] != null)
				{
					return false;
				}
			}
			else
			{
				if (!thisItem.equalsIgnoreCase(otherArray[i]))
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Compares FixedStringList objects with the given String Comparator. Note
	 * that a FixedStringList is sorted first on the size of the
	 * FixedStringList, rather than on the contents. Note that relative to the
	 * contents of a FixedStringList (tested after size), null sorts first
	 * (before any non-null Strings).
	 */
	public static int compare(FixedStringList fsl1, FixedStringList fsl2,
			Comparator<String> comparator)
	{
		String[] thisArray = fsl1.array;
		int thisArrayLength = thisArray.length;
		String[] otherArray = fsl2.array;
		int otherLength = otherArray.length;
		if (thisArrayLength < otherLength)
		{
			return -1;
		}
		else if (thisArrayLength > otherLength)
		{
			return 1;
		}

		for (int i = 0; i < thisArrayLength; i++)
		{
			String thisItem = thisArray[i];
			String otherItem = otherArray[i];
			if (thisItem == null)
			{
				if (otherItem != null)
				{
					// null sorts first
					return -1;
				}
			}
			else if (otherItem == null)
			{
				// null sorts first
				return 1;
			}
			else
			{
				int compare = comparator.compare(thisItem, otherItem);
				if (compare != 0)
				{
					return compare;
				}
			}
		}
		return 0;
	}
}
