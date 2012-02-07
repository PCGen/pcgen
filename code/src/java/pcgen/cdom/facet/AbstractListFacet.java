/*
 * Copyright (c) Thomas Parker, 2009.
 * 
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractListFacet is a DataFacet that contains information about
 * CDOMObjects that are contained in a PlayerCharacter when a PlayerCharacter
 * may have more than one of that type of CDOMObject (e.g. Language,
 * PCTemplate). This is not used for CDOMObjects where the PlayerCharacter only
 * possesses one of that type of object (e.g. Race, Deity)
 */
public abstract class AbstractListFacet<T> extends AbstractDataFacet<T>
{
	private final Class<?> thisClass = getClass();

	/**
	 * Add the given object to the list of objects stored in this
	 * AbstractListFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given item should be added
	 * @param obj
	 *            The object to be added to the list of objects stored in this
	 *            AbstractListFacet for the Player Character represented by the
	 *            given CharID
	 */
	public void add(CharID id, T obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		if (getConstructingCachedSet(id).add(obj))
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Adds all of the objects in the given Collection to the list of objects
	 * stored in this AbstractListFacet for the Player Character represented by
	 * the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given items should be added
	 * @param c
	 *            The Collection of objects to be added to the list of objects
	 *            stored in this AbstractListFacet for the Player Character
	 *            represented by the given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAll(CharID id, Collection<T> c)
	{
		Collection<T> set = getConstructingCachedSet(id);
		for (T obj : c)
		{
			if (obj == null)
			{
				throw new IllegalArgumentException("Object to add may not be null");
			}
			if (set.add(obj))
			{
				fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
			}
		}
	}

	/**
	 * Removes the given object from the list of objects stored in this
	 * AbstractListFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given item should be removed
	 * @param obj
	 *            The object to be removed from the list of objects stored in
	 *            this AbstractListFacet for the Player Character represented by
	 *            the given CharID
	 */
	public void remove(CharID id, T obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet != null)
		{
			if (componentSet.remove(obj))
			{
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

	/**
	 * Removes all of the objects in the given Collection from the list of
	 * objects stored in this AbstractListFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given items should be removed
	 * @param c
	 *            The Collection of objects to be removed from the list of
	 *            objects stored in this AbstractListFacet for the Player
	 *            Character represented by the given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void removeAll(CharID id, Collection<T> c)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet != null)
		{
			for (T obj : c)
			{
				if (obj == null)
				{
					throw new IllegalArgumentException("Object to add may not be null");
				}
				if (componentSet.remove(obj))
				{
					fireDataFacetChangeEvent(id, obj,
							DataFacetChangeEvent.DATA_REMOVED);
				}
			}
		}
	}

	/**
	 * Removes all objects from the list of objects stored in this
	 * AbstractListFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            items should be removed
	 * @return A non-null Set of objects removed from the list of objects stored
	 *         in this AbstractListFacet for the Player Character represented by
	 *         the given CharID
	 */
	public Collection<T> removeAll(CharID id)
	{
		Collection<T> componentSet = (Collection<T>) removeCache(id, thisClass);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		for (T obj : componentSet)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
		}
		return componentSet;
	}

	/**
	 * Returns the Set of objects in this AbstractListFacet for the Player
	 * Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractListFacet should be returned.
	 * @return A non-null Set of objects in this AbstractListFacet for the
	 *         Player Character represented by the given CharID
	 */
	public Collection<T> getSet(CharID id)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableCollection(componentSet);
	}

	/**
	 * Returns the count of items in this AbstractListFacet for the Player
	 * Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @return The count of items in this AbstractListFacet for the Player
	 *         Character represented by the given CharID
	 */
	public int getCount(CharID id)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return 0;
		}
		return componentSet.size();
	}

	/**
	 * Returns true if this AbstractListFacet does not contain any items for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this AbstractListFacet
	 * @return true if this AbstractListFacet does not contain any items for the
	 *         Player Character represented by the given CharID; false otherwise
	 *         (if it does contain items for the Player Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Collection<T> componentSet = getCachedSet(id);
		return componentSet == null || componentSet.isEmpty();
	}

	/**
	 * Returns true if this AbstractListFacet contains the given value in the
	 * list of items for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The object to test if this AbstractListFacet contains that
	 *            item for the Player Character represented by the given CharID
	 * @return true if this AbstractListFacet contains the given value for the
	 *         Player Character represented by the given CharID; false otherwise
	 */
	public boolean contains(CharID id, T obj)
	{
		/*
		 * TODO null? - log an error?
		 */
		Collection<T> componentSet = getCachedSet(id);
		return componentSet != null && componentSet.contains(obj);
	}

	/**
	 * Returns the type-safe Set for this AbstractListFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Set is owned by
	 * AbstractListFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than AbstractListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this AbstractListFacet for
	 *         the Player Character.
	 */
	protected Collection<T> getCachedSet(CharID id)
	{
		return (Collection<T>) getCache(id, thisClass);
	}

	/**
	 * Returns a type-safe Set for this AbstractListFacet and the given CharID.
	 * Will return a new, empty Set if no information has been set in this
	 * AbstractListFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Set object is owned by
	 * AbstractListFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than AbstractListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID.
	 */
	private Collection<T> getConstructingCachedSet(CharID id)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			componentSet = getComponentSet();
			setCache(id, thisClass, componentSet);
		}
		return componentSet;
	}

	protected Collection<T> getComponentSet()
	{
		return new LinkedHashSet<T>();
	}

	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Collection<T> componentSet = getCachedSet(source);
		if (componentSet != null)
		{
			getConstructingCachedSet(copy).addAll(componentSet);
		}
	}
}
