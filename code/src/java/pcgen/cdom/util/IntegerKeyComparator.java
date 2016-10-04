/*
 * Copyright 2015 (C) Tom Parker <thpr@sourceforge.net>
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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.IntegerKey;

/**
 * Provides a Comparator that is capable of sorting CDOMObjects based on the
 * value in a specific IntegerKey.
 * 
 * Limitations: (1) Neither IntegerKey can be null. (2) No value stored for the
 * IntegerKey can be identical (or the comparison will return a false positive -
 * this will NOT fall back on [for example] the key of the object to keep
 * objects distinguished)
 */
public class IntegerKeyComparator implements Comparator<CDOMObject>
{
	/**
	 * The IntegerKey for sorting. Specifically, the value stored in the
	 * CDOMObject with this IntegerKey will be used to determine the order of
	 * the objects.
	 */
	private IntegerKey ik;

	/**
	 * Constructs a new IntegerKeyComparator that will use the given IntegerKey
	 * 
	 * @param key
	 *            The IntegerKey for sorting
	 * @throws IllegalArgumentException
	 *             if the given IntegerKey is null
	 */
	public IntegerKeyComparator(IntegerKey key)
	{
		if (key == null)
		{
			throw new IllegalArgumentException("IntegerKey cannot be null");
		}
		ik = key;
	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(CDOMObject cdo1, CDOMObject cdo2)
	{
		return Integer.compare(cdo1.getSafe(ik), cdo2.getSafe(ik));
	}
}
