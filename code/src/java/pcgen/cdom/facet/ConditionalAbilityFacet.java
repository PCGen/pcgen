/*
 * Copyright (c) Thomas Parker, 2010.
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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.helper.ConditionalAbility;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * An DeniedAbilityFacet is a DataFacet that contains information about Ability
 * objects that are not contained in a PlayerCharacter because the PC did not
 * pass prerequisites
 */
public class ConditionalAbilityFacet
{

	private final Class<?> thisClass = getClass();

	/**
	 * Add the given object to the list of objects stored in this
	 * DeniedAbilityFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given item should be added
	 * @param obj
	 *            The object to be added to the list of objects stored in this
	 *            DeniedAbilityFacet for the Player Character represented by the
	 *            given CharID
	 */
	public void add(CharID id, ConditionalAbility obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		getConstructingCachedSet(id).add(obj);
	}

	/**
	 * Adds all of the objects in the given Collection to the list of objects
	 * stored in this DeniedAbilityFacet for the Player Character represented by
	 * the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given items should be added
	 * @param c
	 *            The Collection of objects to be added to the list of objects
	 *            stored in this DeniedAbilityFacet for the Player Character
	 *            represented by the given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAll(CharID id, Collection<ConditionalAbility> c)
	{
		Set<ConditionalAbility> set = getConstructingCachedSet(id);
		for (ConditionalAbility obj : c)
		{
			if (obj == null)
			{
				throw new IllegalArgumentException(
						"Object to add may not be null");
			}
			set.add(obj);
		}
	}

	/**
	 * Removes the given object from the list of objects stored in this
	 * DeniedAbilityFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given item should be removed
	 * @param obj
	 *            The object to be removed from the list of objects stored in
	 *            this DeniedAbilityFacet for the Player Character represented
	 *            by the given CharID
	 */
	public void remove(CharID id, ConditionalAbility obj)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		if (componentSet != null)
		{
			componentSet.remove(obj);
		}
	}

	/**
	 * Removes all of the objects in the given Collection from the list of
	 * objects stored in this DeniedAbilityFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given items should be removed
	 * @param c
	 *            The Collection of objects to be removed from the list of
	 *            objects stored in this DeniedAbilityFacet for the Player
	 *            Character represented by the given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void removeAll(CharID id, Collection<ConditionalAbility> c)
	{
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		if (componentSet != null)
		{
			for (ConditionalAbility obj : c)
			{
				if (obj == null)
				{
					throw new IllegalArgumentException(
							"Object to add may not be null");
				}
				componentSet.remove(obj);
			}
		}
	}

	/**
	 * Removes all objects from the list of objects stored in this
	 * DeniedAbilityFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            items should be removed
	 * @return A non-null Set of objects removed from the list of objects stored
	 *         in this DeniedAbilityFacet for the Player Character represented
	 *         by the given CharID
	 */
	public Set<ConditionalAbility> removeAll(CharID id)
	{
		Set<ConditionalAbility> componentSet = (Set<ConditionalAbility>) FacetCache
				.remove(id, thisClass);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		return componentSet;
	}

	/**
	 * Returns the Set of objects in this DeniedAbilityFacet for the Player
	 * Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this DeniedAbilityFacet should be returned.
	 * @return A non-null Set of objects in this DeniedAbilityFacet for the
	 *         Player Character represented by the given CharID
	 */
	public Set<ConditionalAbility> getSet(CharID id)
	{
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(componentSet);
	}

	/**
	 * Returns the count of items in this DeniedAbilityFacet for the Player
	 * Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @return The count of items in this DeniedAbilityFacet for the Player
	 *         Character represented by the given CharID
	 */
	public int getCount(CharID id)
	{
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return 0;
		}
		return componentSet.size();
	}

	/**
	 * Returns true if this DeniedAbilityFacet does not contain any items for
	 * the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this DeniedAbilityFacet
	 * @return true if this DeniedAbilityFacet does not contain any items for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise (if it does contain items for the Player Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		return componentSet == null || componentSet.isEmpty();
	}

	/**
	 * Returns true if this DeniedAbilityFacet contains the given value in the
	 * list of items for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The object to test if this DeniedAbilityFacet contains that
	 *            item for the Player Character represented by the given CharID
	 * @return true if this DeniedAbilityFacet contains the given value for the
	 *         Player Character represented by the given CharID; false otherwise
	 */
	public boolean contains(CharID id, ConditionalAbility obj)
	{
		/*
		 * TODO null? - log an error?
		 */
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		return componentSet != null && componentSet.contains(obj);
	}

	/**
	 * Returns the type-safe Set for this DeniedAbilityFacet and the given
	 * CharID. May return null if no information has been set in this
	 * DeniedAbilityFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Set is owned by
	 * DeniedAbilityFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than DeniedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this DeniedAbilityFacet
	 *         for the Player Character.
	 */
	private Set<ConditionalAbility> getCachedSet(CharID id)
	{
		return (Set<ConditionalAbility>) FacetCache.get(id, thisClass);
	}

	/**
	 * Returns a type-safe Set for this DeniedAbilityFacet and the given CharID.
	 * Will return a new, empty Set if no information has been set in this
	 * DeniedAbilityFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Set object is owned by
	 * DeniedAbilityFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than DeniedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID.
	 */
	private Set<ConditionalAbility> getConstructingCachedSet(CharID id)
	{
		Set<ConditionalAbility> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			componentSet = new LinkedHashSet<ConditionalAbility>();
			FacetCache.set(id, thisClass, componentSet);
		}
		return componentSet;
	}

	public void removeAll(CharID id, Object source)
	{
		Set<ConditionalAbility> cachedSet = getCachedSet(id);
		if (cachedSet != null)
		{
			for (Iterator<ConditionalAbility> it = cachedSet.iterator(); it
					.hasNext();)
			{
				if (it.next().getParent().equals(source))
				{
					it.remove();
				}
			}
		}
	}
}