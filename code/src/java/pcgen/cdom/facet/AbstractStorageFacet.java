/*
 * Copyright (c) 2012 Tom Parker <thpr@users.sourceforge.net>
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet;

import java.util.Map;
import java.util.Set;

import pcgen.base.test.InequalityTester;
import pcgen.base.util.DoubleKeyMap;
import pcgen.cdom.enumeration.CharID;
import pcgen.util.Logging;

/**
 * An AbstractStorageFacet is a facet which stores contents in the overall CDOM
 * cache. All classes (facets) that want to store information in the cache must
 * extend this class.
 * 
 * @author Tom Parker (thpr [at] yahoo.com)
 */
public abstract class AbstractStorageFacet
{
	/** 
	 * This is a non static copy of the last used character's cache entry. It is 
	 * purely for use with runtime debuggers that do not allow access to static 
	 * fields. 
	 */
	private Map<Class<?>, Object> lastCharacterDebugCache;
	
	/**
	 * Copies the contents of the AbstractStorageFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method each AbstractStorageFacet must implement in order to do
	 * 2 things: First, it must avoid exposing the mutable storage information
	 * stored in the cache to other classes. Second, this ensures that every
	 * AbstractStorageFacet has implemented a copy function so that any deep
	 * copies (if Lists need to be cloned, etc.) is done appropriately.
	 * 
	 * Note also the copy is a one-time event and no references should be
	 * maintained between the Player Characters represented by the given CharIDs
	 * (meaning once this copy takes place, any change to the
	 * AbstractStorageFacet of one Player Character will only impact the Player
	 * Character where the AbstractStorageFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	public abstract void copyContents(CharID source, CharID copy);

	/**
	 * The actual cache that stores the CDOM information, as stored by the
	 * identifying CharID of a PlayerCharacter and the class of the facet
	 * storing the information
	 */
	private static final DoubleKeyMap<CharID, Class<?>, Object> CACHE =
			new DoubleKeyMap<CharID, Class<?>, Object>();

	/**
	 * Removes the information from the cache for a given Player Character and
	 * facet (as identified by the Class)
	 * 
	 * @param id
	 *            The CharID for which information from the cache should be
	 *            removed
	 * @param cl
	 *            The Class used to identify the facet for which information
	 *            should be removed.
	 * @return The information which was removed from the Cache for the Player
	 *         Character identified by the given CharID and the facet identified
	 *         by the given Class.
	 */
	public Object removeCache(CharID id, Class<?> cl)
	{
		lastCharacterDebugCache = CACHE.getMapFor(id);
		return CACHE.remove(id, cl);
	}

	/**
	 * Sets the information from the cache for a given Player Character and
	 * facet (as identified by the Class)
	 * 
	 * @param id
	 *            The CharID for which information from the cache should be
	 *            removed
	 * @param cl
	 *            The Class used to identify the facet for which information
	 *            should be removed.
	 * @param o
	 *            The object to be stored in the cache.
	 * @return The previous information which was removed from the Cache for the
	 *         Player Character identified by the given CharID and the facet
	 *         identified by the given Class.
	 */
	public Object setCache(CharID id, Class<?> cl, Object o)
	{
		lastCharacterDebugCache = CACHE.getMapFor(id);
		return CACHE.put(id, cl, o);
	}

	/**
	 * Retrieves the information from the cache for a given Player Character and
	 * facet (as identified by the Class)
	 * 
	 * @param id
	 *            The CharID for which information from the cache should be
	 *            removed
	 * @param cl
	 *            The Class used to identify the facet for which information
	 *            should be removed.
	 * @return The information in the Cache for the Player Character identified
	 *         by the given CharID and the facet identified by the given Class.
	 */
	public Object getCache(CharID id, Class<?> cl)
	{
		lastCharacterDebugCache = CACHE.getMapFor(id);
		return CACHE.get(id, cl);
	}

	/**
	 * Tests whether the contents of the cache are equal for two Player
	 * Characters, as identified by the CharID objects. The given
	 * InequalityTester is used to compare the cache contents.
	 * 
	 * @param id1
	 *            The CharID of the first PlayerCharacter that is to be compared
	 * @param id2
	 *            The CharID of the second PlayerCharacter that is to be
	 *            compared
	 * @param t
	 *            The InequalityTester used to establish equality between
	 *            contents of the cache.
	 * @return true if the contents of the cache are equal (as identified by the
	 *         given InequalityTester) for the Player Characters identified by
	 *         the given CharIDs; false otherwise
	 */
	public static boolean areEqualCache(CharID id1, CharID id2,
		InequalityTester t)
	{
		Set<Class<?>> set1 = CACHE.getSecondaryKeySet(id1);
		Set<Class<?>> set2 = CACHE.getSecondaryKeySet(id2);
		if (!set1.equals(set2))
		{
			return false;
		}
		for (Class<?> cl : set1)
		{
			Object obj1 = CACHE.get(id1, cl);
			Object obj2 = CACHE.get(id2, cl);
			String equal = t.testEquality(obj1, obj2);
			if (equal != null)
			{
				Logging.errorPrint(equal);
				return false;
			}
		}
		return true;
	}

}
