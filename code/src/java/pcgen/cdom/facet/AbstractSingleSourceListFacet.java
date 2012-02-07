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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractSingleSourceListFacet is a DataFacet that contains information
 * about CDOMObjects that are contained in a PlayerCharacter when a
 * PlayerCharacter may have more than one of that type of CDOMObject (e.g.
 * Language, PCTemplate) and the source of that object should be tracked.
 * 
 * Using this class, an object may have only one source. If the object is
 * re-added with a second source, this will not trigger a DATA_ADDED event.
 * 
 * null is NOT a valid source.
 */
public abstract class AbstractSingleSourceListFacet<T, ST> extends
		AbstractDataFacet<T>
{
	/**
	 * Add the given object with the given source to the list of objects stored
	 * in this AbstractSingleSourceListFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given item should be added
	 * @param obj
	 *            The object to be added to the list of objects stored in this
	 *            AbstractQualifiedListFacet for the Player Character
	 *            represented by the given CharID
	 * @param source
	 *            The source for the given object
	 */
	public void add(CharID id, T obj, ST source)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		if (source == null)
		{
			throw new IllegalArgumentException("Source may not be null");
		}
		Map<T, ST> map = getConstructingCachedMap(id);
		Object oldsource = map.get(obj);
		boolean fireNew = (oldsource == null);
		map.put(obj, source);
		if (fireNew)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Adds all of the objects with the given source in the given Collection to
	 * the list of objects stored in this AbstractQualifiedListFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given items should be added
	 * @param c
	 *            The Collection of objects to be added to the list of objects
	 *            stored in this AbstractQualifiedListFacet for the Player
	 *            Character represented by the given CharID
	 * @param source
	 *            The source for the given object
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAll(CharID id, Collection<? extends T> c, ST source)
	{
		for (T obj : c)
		{
			add(id, obj, source);
		}
	}

	/**
	 * Removes the given source entry from the list of sources for the given
	 * object stored in this AbstractQualifiedListFacet for the Player Character
	 * represented by the given CharID. If the given source was the only source
	 * for the given object, then the object is removed from the list of objects
	 * stored in this AbstractQualifiedListFacet for the Player Character
	 * represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given item source should be removed
	 * @param obj
	 *            The object for which the source should be removed
	 * @param source
	 *            The source for the given object to be removed from the list of
	 *            sources.
	 */
	public void remove(CharID id, T obj, ST source)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			processRemoval(id, componentMap, obj, source);
		}
	}

	/**
	 * Removes the given source entry from the list of sources for all of the
	 * objects in the given Collection for the Player Character represented by
	 * the given CharID. If the given source was the only source for any of the
	 * objects in the collection, then those objects are removed from the list
	 * of objects stored in this AbstractQualifiedListFacet for the Player
	 * Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given items should be removed
	 * @param c
	 *            The Collection of objects to be removed from the list of
	 *            objects stored in this AbstractQualifiedListFacet for the
	 *            Player Character represented by the given CharID
	 * @param source
	 *            The source for the objects in the given Collection to be
	 *            removed from the list of sources.
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void removeAll(CharID id, Collection<T> c, ST source)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (T obj : c)
			{
				processRemoval(id, componentMap, obj, source);
			}
		}
	}

	/**
	 * Removes all objects (and all sources for those objects) from the list of
	 * objects stored in this AbstractSingleSourceListFacet for the Player
	 * Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            items should be removed
	 * @return A non-null Set of objects removed from the list of objects stored
	 *         in this AbstractSingleSourceListFacet for the Player Character
	 *         represented by the given CharID
	 */
	public Map<T, ST> removeAll(CharID id)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptyMap();
		}
		removeCache(id, getClass());
		for (T obj : componentMap.keySet())
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
		}
		return componentMap;
	}

	/**
	 * Returns the Set of objects in this AbstractSingleSourceListFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractSingleSourceListFacet should be
	 *            returned.
	 * @return A non-null Set of objects in this AbstractSingleSourceListFacet
	 *         for the Player Character represented by the given CharID
	 */
	public Set<T> getSet(CharID id)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(componentMap.keySet());
	}

	/**
	 * Returns the count of items in this AbstractSingleSourceListFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @return The count of items in this AbstractSingleSourceListFacet for the
	 *         Player Character represented by the given CharID
	 */
	public int getCount(CharID id)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return 0;
		}
		return componentMap.size();
	}

	/**
	 * Returns true if this AbstractSingleSourceListFacet does not contain any
	 * items for the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this AbstractsSourcedListFacet
	 * @return true if this AbstractSingleSourceListFacet does not contain any
	 *         items for the Player Character represented by the given CharID;
	 *         false otherwise (if it does contain items for the Player
	 *         Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		return componentMap == null || componentMap.isEmpty();
	}

	/**
	 * Returns true if this AbstractSingleSourceListFacet contains the given
	 * value in the list of items for the Player Character represented by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The object to test if this AbstractSingleSourceListFacet
	 *            contains that item for the Player Character represented by the
	 *            given CharID
	 * @return true if this AbstractSingleSourceListFacet contains the given
	 *         value for the Player Character represented by the given CharID;
	 *         false otherwise
	 */
	public boolean contains(CharID id, T obj)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		return componentMap != null && componentMap.containsKey(obj);
	}

	/**
	 * Returns the type-safe Map for this AbstractSingleSourceListFacet and the
	 * given CharID. May return null if no information has been set in this
	 * AbstractSingleSourceListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractSingleSourceListFacet, and since it can be modified, a reference
	 * to that object should not be exposed to any object other than
	 * AbstractSingleSourceListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractSingleSourceListFacet for the Player Character.
	 */
	protected Map<T, ST> getCachedMap(CharID id)
	{
		return (Map<T, ST>) getCache(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AbstractSingleSourceListFacet and the
	 * given CharID. Will return a new, empty Map if no information has been set
	 * in this AbstractSingleSourceListFacet for the given CharID. Will not
	 * return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractSingleSourceListFacet, and since it can be modified, a reference
	 * to that object should not be exposed to any object other than
	 * AbstractSingleSourceListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<T, ST> getConstructingCachedMap(CharID id)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = getComponentMap();
			setCache(id, getClass(), componentMap);
		}
		return componentMap;
	}

	protected Map<T, ST> getComponentMap()
	{
		return new IdentityHashMap<T, ST>();
	}

	/**
	 * Copies the contents of the AbstractSingleSourceListFacet from one Player
	 * Character to another Player Character, based on the given CharIDs
	 * representing those Player Characters.
	 * 
	 * This is a method in AbstractSingleSourceListFacet in order to avoid
	 * exposing the mutable Map object to other classes. This should not be
	 * inlined, as the Map is internal information to
	 * AbstractSingleSourceListFacet and should not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the
	 * AbstractSingleSourceListFacet of one Player Character will only impact
	 * the Player Character where the AbstractSingleSourceListFacet was
	 * changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID destination)
	{
		Map<T, ST> sourceMap = getCachedMap(source);
		if (sourceMap != null)
		{
			getConstructingCachedMap(destination).putAll(sourceMap);
		}
	}

	/**
	 * This method implements removal of a source for an object contained by
	 * this AbstractSingleSourceListFacet. This implements the actual check that
	 * determines if the given source was the only source for the given object.
	 * If so, then that object is removed from the list of objects stored in
	 * this AbstractQualifiedListFacet for the Player Character represented by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character which may have
	 *            the given item removed.
	 * @param componentMap
	 *            The (private) Map for this AbstractSingleSourceListFacet that
	 *            will as least have the given source removed from the list for
	 *            the given object.
	 * @param obj
	 *            The object which may be removed if the given source is the
	 *            only source for this object in the Player Character
	 *            represented by the given CharID
	 * @param source
	 *            The source for the given object to be removed from the list of
	 *            sources for that object
	 */
	private void processRemoval(CharID id, Map<T, ST> componentMap, T obj,
			ST source)
	{
		/*
		 * TODO obj Null?
		 */
		Object oldSource = componentMap.get(obj);
		if (oldSource != null)
		{
			if (oldSource.equals(source))
			{
				componentMap.remove(obj);
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

	public void removeAll(CharID id, ST source)
	{
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Map.Entry<T, ST>> it = componentMap.entrySet()
					.iterator(); it.hasNext();)
			{
				Entry<T, ST> me = it.next();
				Object currentsource = me.getValue();
				if (currentsource.equals(source))
				{
					T obj = me.getKey();
					it.remove();
					fireDataFacetChangeEvent(id, obj,
							DataFacetChangeEvent.DATA_REMOVED);
				}
			}
		}
	}

	public List<? extends T> getSet(CharID id, ST owner)
	{
		List<T> list = new ArrayList<T>();
		Map<T, ST> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Map.Entry<T, ST>> it = componentMap.entrySet()
					.iterator(); it.hasNext();)
			{
				Entry<T, ST> me = it.next();
				Object source = me.getValue();
				if (source.equals(owner))
				{
					list.add(me.getKey());
				}
			}
		}
		return list;
	}

	public ST getSource(CharID id, T obj)
	{
		Map<T, ST> map = getCachedMap(id);
		if (map == null)
		{
			return null;
		}
		return map.get(obj);
	}

	public void remove(CharID id, T obj)
	{
		Map<T, ST> map = getCachedMap(id);
		if (map != null)
		{
			if (map.remove(obj) != null)
			{
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}
}
