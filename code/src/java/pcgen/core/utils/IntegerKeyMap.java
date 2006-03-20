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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This encapsulates a Map in a typesafe way (prior to java 1.5 having the
 * ability to do that with typed collections)
 */
public class IntegerKeyMap
{

	private final Map map = new HashMap();

	/** Constructor */
	public IntegerKeyMap()
	{
		// Do Nothing
	}

	/**
	 * Get a characteristic
	 * @param key
	 * @return a characteristic
	 */
	public Integer getCharacteristic(IntegerKey key)
	{
		return (Integer) map.get(key);
	}

	/**
	 * Set a characteristic
	 * @param key
	 * @param value
	 */
	public void setCharacteristic(IntegerKey key, Integer value)
	{
		map.put(key, value);
	}

	/**
	 * Set a characteristic
	 * @param key
	 * @param value
	 */
	public void setCharacteristic(IntegerKey key, int value)
	{
		map.put(key, new Integer(value));
	}

	/**
	 * return true if it has the characteristic
	 * @param key
	 * @return true if map has the characteristic
	 */
	public boolean hasCharacteristic(IntegerKey key)
	{
		return map.containsKey(key);
	}

	/**
	 * Add all of the characteristics
	 * @param scs
	 */
	public void addAllCharacteristics(IntegerKeyMap scs)
	{
		map.putAll(scs.map);
	}
	
	/**
	 * Remove a characteristic from the map
	 * @param key
	 * @return the previous charactersitc assocaited with that key or NULL
	 */
	public Integer removeCharacteristic(IntegerKey key) 
	{
		return (Integer) map.remove(key);
	}
}
