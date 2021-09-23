/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.HashMap;

/**
 * A DefaultMap is a HashMap that has a modified get operation. This get
 * operation will return a given default value (instead of null) if the given
 * key is not otherwise contained in the DefaultMap. This default return
 * behavior does NOT modify the behavior of the containsKey() method.
 * 
 * @param <K>
 *            The type of the key objects for this DefaultMap
 * @param <V>
 *            The type of the value objects for this DefaultMap
 */
public class DefaultMap<K, V> extends HashMap<K, V>
{

	/**
	 * The default value to be returned if a key is not contained in the Map.
	 */
	private V defaultValue;

	/**
	 * Gets the value mapped to a given key if there is a value for the key in
	 * the Map. Otherwise, returns the default value.
	 */
	@Override
	public V get(Object key)
	{
		/*
		 * Please note this cannot be done as a get and a test for null, because
		 * null is a valid map if the key is actually contained within the Map.
		 */
		return containsKey(key) ? super.get(key) : defaultValue;
	}

	/**
	 * Returns the default value (what is returned by the get method if a key is
	 * not contained in the Map).
	 * 
	 * @return the default value for this Map
	 */
	public V getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Sets the default value (what is returned by the get method if a key is
	 * not contained in the Map).
	 * 
	 * @param defaultValue
	 *            the new default value for this Map
	 */
	public void setDefaultValue(V defaultValue)
	{
		this.defaultValue = defaultValue;
	}
}
