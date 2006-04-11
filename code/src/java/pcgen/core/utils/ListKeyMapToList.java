/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package pcgen.core.utils;

import pcgen.util.HashMapToList;

import java.util.List;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 *
 * This encapsulates a MapToList in a typesafe way (prior to java 1.5 having the
 * ability to do that with typed Collections)
 */
public class ListKeyMapToList
{

	private final HashMapToList map = new HashMapToList();

	/** Constructor */
	public ListKeyMapToList()
	{
		// Do Nothing
	}

	/**
	 * Add all lists to the map
	 * @param lcs
	 */
	public void addAllLists(ListKeyMapToList lcs)
	{
		map.addAllLists(lcs.map);
	}

	/**
	 * @param key
	 * @param list
	 */
	public void addAllToListFor(ListKey key, List list)
	{
		map.addAllToListFor(key, list);
	}

	/**
	 * Add value to a list
	 * @param key
	 * @param value
	 */
	public void addToListFor(ListKey key, Object value)
	{
		map.addToListFor(key, value);
	}

	/**
	 * Returns true if list contains a value for a key
	 * @param key
	 * @return true if list contains a value for a key
	 */
	public boolean containsListFor(ListKey key)
	{
		return map.containsListFor(key);
	}

	/**
	 * Get a list for a key
	 * @param key
	 * @return list
	 */
	public List getListFor(ListKey key)
	{
		return map.getListFor(key);
	}

	/**
	 * Get an element in the list
	 * @param key
	 * @param i
	 * @return element in list
	 */
	public Object getElementInList(ListKey key, int i)
	{
		return map.getElementInList(key, i);
	}

	/**
	 * Initialise the list for a given key
	 * @param key
	 */
	public void initializeListFor(ListKey key)
	{
		map.initializeListFor(key);
	}

	/**
	 * Remove an item from a list
	 * @param key
	 * @param value
	 * @return true, removal ok
	 */
	public boolean removeFromListFor(ListKey key, Object value)
	{
		return map.removeFromListFor(key, value);
	}

	/**
	 * Remove a list from a map
	 * @param key
	 * @return removed list
	 */
	public List removeListFor(ListKey key)
	{
		return map.removeListFor(key);
	}

	/**
	 * Get size of a list
	 * @param key
	 * @return size
	 */
	public int sizeOfListFor(ListKey key)
	{
		return map.sizeOfListFor(key);
	}

	/**
	 * True if value is in a list
	 * @param key
	 * @param value
	 * @return True if value is in a list
	 */
	public boolean containsInList(ListKey key, String value)
	{
		return map.containsInList(key, value);
	}
}
