/*
 * Copyright 2018 (C) Tom Parker <thpr@sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.util;

import java.util.Comparator;

import pcgen.cdom.base.SortKeyRequired;

/**
 * This is a trivial sorter for objects that implement the SortKeyRequired interface
 */
public final class SortKeyComparator implements Comparator<SortKeyRequired>
{

	/**
	 * The singleton instance of SortKeyComparator.
	 */
	private static final Comparator<SortKeyRequired> INSTANCE = new SortKeyComparator();

	private SortKeyComparator()
	{
		//Private for Singleton
	}

	@Override
	public int compare(SortKeyRequired o1, SortKeyRequired o2)
	{
		return o1.getSortKey().compareTo(o2.getSortKey());
	}

	/**
	 * Returns the SortKeyComparator instance.
	 * 
	 * @return the SortKeyComparator instance
	 */
	public static Comparator<SortKeyRequired> getInstance()
	{
		return INSTANCE;
	}

}
