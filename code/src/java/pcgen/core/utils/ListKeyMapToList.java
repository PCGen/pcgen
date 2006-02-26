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
 * Current Ver: $Revision: 1.4 $
 * Last Editor: $Author: binkley $
 * Last Edited: $Date: 2005/10/18 20:23:41 $
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

	public ListKeyMapToList()
	{
		// Do Nothing
	}

	public void addAllLists(ListKeyMapToList lcs)
	{
		map.addAllLists(lcs.map);
	}

	public void addAllToListFor(ListKey key, List list)
	{
		map.addAllToListFor(key, list);
	}

	public void addToListFor(ListKey key, Object value)
	{
		map.addToListFor(key, value);
	}

	public boolean containsListFor(ListKey key)
	{
		return map.containsListFor(key);
	}

	public List getListFor(ListKey key)
	{
		return map.getListFor(key);
	}

	public Object getElementInList(ListKey key, int i)
	{
		return map.getElementInList(key, i);
	}

	public void initializeListFor(ListKey key)
	{
		map.initializeListFor(key);
	}

	public boolean removeFromListFor(ListKey key, Object value)
	{
		return map.removeFromListFor(key, value);
	}

	public List removeListFor(ListKey key)
	{
		return map.removeListFor(key);
	}

	public int sizeOfListFor(ListKey key)
	{
		return map.sizeOfListFor(key);
	}

	public boolean containsInList(ListKey key, String value)
	{
		return map.containsInList(key, value);
	}
}
