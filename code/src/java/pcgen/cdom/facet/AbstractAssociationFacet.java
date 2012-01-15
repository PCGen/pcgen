/*
 * Copyright (c) Thomas Parker, 2012.
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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 *         An AbstractAssociationFacet is a DataFacet that contains information
 *         about associations Objects that are contained in a PlayerCharacter.
 * 
 *         This is used when each source may only have one association (such as
 *         associating hit points to a class level)
 * 
 *         If the source object (e.g. the class level) is re-added with a second
 *         association, this will overwrite the original association.
 * 
 *         null is NOT a valid source.
 */
public abstract class AbstractAssociationFacet<S, A>
{

	public A get(CharID id, S obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException(
				"Object for getting association may not be null");
		}
		Map<S, A> map = getCachedMap(id);
		if (map != null)
		{
			return map.get(obj);
		}
		return null;
	}

	/**
	 * Set the given association for the given object to the associations stored
	 * in this AbstractAssociationFacet for the Player Character represented by
	 * the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given association should be made
	 * @param obj
	 *            The object for which the association will be added
	 * @param association
	 *            The association for the given object
	 */
	public void set(CharID id, S obj, A association)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		if (association == null)
		{
			throw new IllegalArgumentException("Association may not be null");
		}
		Map<S, A> map = getConstructingCachedMap(id);
		Object oldsource = map.get(obj);
		boolean fireNew = (oldsource == null);
		map.put(obj, association);
		if (fireNew)
		{
			//fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Removes the association for the given source entry from the list of
	 * associations in this AbstractQualifiedListFacet for the Player Character
	 * represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given item source should be removed
	 * @param obj
	 *            The object for which the source should be removed
	 */
	public void remove(CharID id, S obj)
	{
		Map<S, A> map = getCachedMap(id);
		if (map != null)
		{
			if (map.remove(obj) != null)
			{
				//				fireDataFacetChangeEvent(id, obj,
				//					DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

	/**
	 * Removes all objects (and all sources for those objects) from the list of
	 * objects stored in this AbstractAssociationFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            items should be removed
	 * @return A non-null Set of objects removed from the list of objects stored
	 *         in this AbstractAssociationFacet for the Player Character
	 *         represented by the given CharID
	 */
	public Map<S, A> removeAll(CharID id)
	{
		Map<S, A> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptyMap();
		}
		FacetCache.remove(id, getClass());
		for (S obj : componentMap.keySet())
		{
			//fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
		}
		return componentMap;
	}

	/**
	 * Returns the Set of source objects in this AbstractAssociationFacet for
	 * the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractAssociationFacet should be returned.
	 * @return A non-null Set of objects in this AbstractAssociationFacet for
	 *         the Player Character represented by the given CharID
	 */
	public Set<S> getSet(CharID id)
	{
		Map<S, A> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(componentMap.keySet());
	}

	/**
	 * Returns the count of items in this AbstractAssociationFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @return The count of items in this AbstractAssociationFacet for the
	 *         Player Character represented by the given CharID
	 */
	public int getCount(CharID id)
	{
		Map<S, A> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return 0;
		}
		return componentMap.size();
	}

	/**
	 * Returns true if this AbstractAssociationFacet does not contain any items
	 * for the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this AbstractsSourcedListFacet
	 * @return true if this AbstractAssociationFacet does not contain any items
	 *         for the Player Character represented by the given CharID; false
	 *         otherwise (if it does contain items for the Player Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Map<S, A> componentMap = getCachedMap(id);
		return componentMap == null || componentMap.isEmpty();
	}

	/**
	 * Returns true if this AbstractAssociationFacet contains the given source
	 * in the list of items for the Player Character represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The source object to test if this AbstractAssociationFacet
	 *            contains an association for that item for the Player Character
	 *            represented by the given CharID
	 * @return true if this AbstractAssociationFacet contains an association for
	 *         the given source for the Player Character represented by the
	 *         given CharID; false otherwise
	 */
	public boolean contains(CharID id, S obj)
	{
		Map<S, A> componentMap = getCachedMap(id);
		return componentMap != null && componentMap.containsKey(obj);
	}

	/**
	 * Returns the type-safe Map for this AbstractAssociationFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractAssociationFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractAssociationFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractAssociationFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractAssociationFacet for the Player Character.
	 */
	protected Map<S, A> getCachedMap(CharID id)
	{
		return (Map<S, A>) FacetCache.get(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AbstractAssociationFacet and the given
	 * CharID. Will return a new, empty Map if no information has been set in
	 * this AbstractAssociationFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractAssociationFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractAssociationFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<S, A> getConstructingCachedMap(CharID id)
	{
		Map<S, A> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = getComponentMap();
			FacetCache.set(id, getClass(), componentMap);
		}
		return componentMap;
	}

	protected Map<S, A> getComponentMap()
	{
		return new IdentityHashMap<S, A>();
	}

	/**
	 * Copies the contents of the AbstractAssociationFacet from one Player
	 * Character to another Player Character, based on the given CharIDs
	 * representing those Player Characters.
	 * 
	 * This is a method in AbstractAssociationFacet in order to avoid exposing
	 * the mutable Map object to other classes. This should not be inlined, as
	 * the Map is internal information to AbstractAssociationFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AbstractAssociationFacet of
	 * one Player Character will only impact the Player Character where the
	 * AbstractAssociationFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	public void copyContents(CharID source, CharID destination)
	{
		Map<S, A> sourceMap = getCachedMap(source);
		if (sourceMap != null)
		{
			getConstructingCachedMap(destination).putAll(sourceMap);
		}
	}
}
