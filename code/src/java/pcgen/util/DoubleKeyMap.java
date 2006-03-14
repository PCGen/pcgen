/*
 * Created on Jun 16, 2005
 *
 * Copyright (c) Thomas Parker, 2005.
 */
package pcgen.util;

import java.util.*;

/**
 * A map representation for a dual key entity
 */
public class DoubleKeyMap implements Cloneable
{

	private Map map = new HashMap();

	/**
	 * Constructor
	 */
	public DoubleKeyMap()
	{
		super();
	}

	/**
	 * Put an object in a map
	 * @param key1
	 * @param key2
	 * @param value
	 * @return Object
	 */
	public Object put(Object key1, Object key2, Object value)
	{
		Map localMap = (Map) map.get(key1);
		if (localMap == null)
		{
			localMap = new HashMap();
			map.put(key1, localMap);
		}
		return localMap.put(key2, value);
	}

	/**
	 * Get an object from the map
	 * @param key1
	 * @param key2
	 * @return Object
	 */
	public Object get(Object key1, Object key2)
	{
		Map localMap = (Map) map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		return localMap.get(key2);
	}

	/**
	 * Returns true if an object is in the map given two keys
	 * @param key1
	 * @param key2
	 * @return true if an object is in the map given two keys
	 */
	public boolean containsKey(Object key1, Object key2)
	{
		Map localMap = (Map) map.get(key1);
		if (localMap == null)
		{
			return false;
		}
		return localMap.containsKey(key2);
	}

	/**
	 * Remove the object from the map
	 * @param key1
	 * @param key2
	 * @return Object
	 */
	public Object remove(Object key1, Object key2)
	{
		Map localMap = (Map) map.get(key1);
		if (localMap == null)
		{
			return null;
		}
		Object o = localMap.remove(key2);
		//cleanup!
		if (localMap.isEmpty())
		{
			map.remove(key1);
		}
		return o;
	}

	/**
	 * Get the Set of keys
	 * @return set of keys
	 */
	public Set getKeySet()
	{
		return new HashSet(map.keySet());
	}

	/**
	 * Clear
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * Returns true if the map is empty
	 * @return true if the map is empty
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Object clone() throws CloneNotSupportedException
	{
		DoubleKeyMap dkm = (DoubleKeyMap) super.clone();
		dkm.map = new HashMap();
		for (Iterator it = map.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			Map m = (Map) map.get(key);
			dkm.map.put(key, new HashMap(m));
		}
		return dkm;
	}
}
