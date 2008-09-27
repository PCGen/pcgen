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
import java.util.List;
import java.util.RandomAccess;

public class FixedStringList extends AbstractList<String> implements
		List<String>, RandomAccess
{

	String[] array;

	public FixedStringList(int size)
	{
		array = new String[size];
	}

	public FixedStringList(Collection<String> c)
	{
		array = c.toArray(new String[c.size()]);
	}

	public FixedStringList(String... a)
	{
		array = new String[a.length];
		System.arraycopy(a, 0, array, 0, a.length);
	}

	@Override
	public boolean add(String o)
	{
		for (int i = 0; i < array.length; i++)
		{
			if (array[i] == null)
			{
				array[i] = o;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends String> c)
	{
		for (String s : c)
		{
			if (!add(s))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String remove(int index)
	{
		String old = array[index];
		array[index] = null;
		return old;
	}

	@Override
	public String set(int index, String element)
	{
		String old = array[index];
		array[index] = element;
		return old;
	}

	@Override
	public String get(int arg0)
	{
		return array[arg0];
	}

	@Override
	public int size()
	{
		return array.length;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof FixedStringList)
		{
			FixedStringList other = (FixedStringList) o;
			return Arrays.deepEquals(array, other.array);
		}
		return super.equals(o);
	}
}
