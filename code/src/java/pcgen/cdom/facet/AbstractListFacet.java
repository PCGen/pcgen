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
import java.util.LinkedHashSet;

import pcgen.cdom.enumeration.CharID;

/**
 * A AbstractListFacet is a DataFacet that contains information about Objects
 * that are contained in a Player Character when a Player Character may have
 * more than one of that type of Object (e.g. Language, PCTemplate). This is not
 * used for Objects where the Player Character only possesses one of that type
 * of object (e.g. Race, Deity)
 * 
 * This class is also used when the source of the Objects in a Player Character
 * do not need to be tracked. If the source needs to be tracked, then
 * AbstractSourcedListFacet should be used.
 * 
 * null is not a valid object to be stored.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
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
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Since this is
	 * a remove all function, modification of the returned Collection will not
	 * modify this AbstractListFacet and modification of this AbstractListFacet
	 * will not modify the returned Collection. Modifications to the returned
	 * Collection will also not modify any future or previous objects returned
	 * by this (or other) methods on AbstractListFacet. If you wish to modify
	 * the information stored in this AbstractListFacet, you must use the add*()
	 * and remove*() methods of AbstractListFacet.
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
	 * Returns a non-null copy of the Set of objects in this AbstractListFacet
	 * for the Player Character represented by the given CharID. This method
	 * returns an empty Set if no objects are in this AbstractListFacet for the
	 * Player Character identified by the given CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this AbstractListFacet and modification of
	 * this AbstractListFacet will not modify the returned List. Modifications
	 * to the returned List will also not modify any future or previous objects
	 * returned by this (or other) methods on AbstractListFacet. If you wish to
	 * modify the information stored in this AbstractListFacet, you must use the
	 * add*() and remove*() methods of AbstractListFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which a copy
	 *            of the items in this AbstractListFacet should be returned.
	 * @return A non-null Collection of objects in this AbstractListFacet for
	 *         the Player Character represented by the given CharID
	 */
	public Collection<T> getSet(CharID id)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableList(new ArrayList<T>(componentSet));
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
		 * TODO obj == null? - log an error?
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

	/**
	 * Returns a new (empty) Collection for this AbstractListFacet. Can be
	 * overridden by classes that extend AbstractListFacet if a Collection other
	 * than an IdentityHashSet is desired for storing the information in the
	 * AbstractListFacet.
	 * 
	 * Note that this method SHOULD NOT be public. The Collection object is
	 * owned by AbstractListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractListFacet.
	 * 
	 * Note that this method should always be the only method used to construct
	 * a Collection for this AbstractListFacet. It is actually preferred to use
	 * getConstructingCacheSet(CharID) in order to implicitly call this method.
	 * 
	 * @return A new (empty) Collection for use in this AbstractListFacet.
	 */
	protected Collection<T> getComponentSet()
	{
		return new LinkedHashSet<T>();
	}

	/**
	 * Copies the contents of the AbstractListFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in AbstractListFacet in order to avoid exposing the
	 * mutable Collection object to other classes. This should not be inlined,
	 * as the Collection is internal information to AbstractListFacet and should
	 * not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AbstractListFacet of one
	 * Player Character will only impact the Player Character where the
	 * AbstractListFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param copy
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID source, CharID copy)
	{
		Collection<T> componentSet = getCachedSet(source);
		if (componentSet != null)
		{
			getConstructingCachedSet(copy).addAll(getCopyForNewOwner(componentSet));
		}
	}

	/**
	 * Create a new copy of this list facet's data. This defaults to a return of the 
	 * same set, but subclasses may need to do deep cloning of their objects as part 
	 * of the copy. Note: The returned collection is directly saved, only its contents.
	 * @param componentSet The collection of data held by the facet for a character.
	 * @return The data ready to be saved to a new collection for a new character.
	 */
	protected Collection<T> getCopyForNewOwner(Collection<T> componentSet)
	{
		return componentSet;
	}
	
	/**
	 * Replaces an item in this AbstractListFacet with another object.
	 * 
	 * NOTE: Use of this method is HIGHLY DISCOURAGED. Please consider another
	 * way of achieving the same results as this method. In other words, this
	 * method was required in order to maintain compatibility with the code in
	 * PCGen that tends to copy & clone things, but in the future, we are
	 * attempting to move away from that structure, so use of this method (which
	 * implies order dependency) is discouraged.
	 * 
	 * This method is equivalent of a replaceAll in a String. In other words, if
	 * the underlying Collection stored in this AbstractListFacet is a List (not
	 * the Set used by default), then this method will replace ALL instances of
	 * an old object in the List, not just the first instance.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which data
	 *            will be replaced
	 * @param old
	 *            The old object to be replaced in the Collection for the Player
	 *            Character represented by the given CharID
	 * @param replacement
	 *            The replacement object to replace the given source object in
	 *            the Collection for the Player Character represented by the
	 *            given CharID
	 * @return true if the given old object was found in the Collection for the
	 *         Player Character represented by the given CharID (and thus true
	 *         if a replacement was successfully made); false otherwise
	 */
	public boolean replace(CharID id, T old, T replacement)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet == null || !componentSet.contains(old))
		{
			return false;
		}
		Collection<T> replaceSet = getComponentSet();
		for (T obj : componentSet)
		{
			if (obj == old)
			{
				replaceSet.add(replacement);
			}
			else
			{
				replaceSet.add(obj);
			}
		}
		setCache(id, thisClass, componentSet);
		fireDataFacetChangeEvent(id, old, DataFacetChangeEvent.DATA_REMOVED);
		fireDataFacetChangeEvent(id, replacement,
			DataFacetChangeEvent.DATA_ADDED);
		return true;
	}

	/**
	 * This method will add the given added object within the underlying
	 * Collection of this AbstractListFacet directly after the given trigger
	 * object.
	 * 
	 * If the underlying Collection for this AbstractListFacet is not an ordered
	 * Collection (e.g. HashSet), then this method is a MUCH slower way of
	 * calling add(CharID, T).
	 * 
	 * NOTE: Use of this method is HIGHLY DISCOURAGED. Please consider another
	 * way of achieving the same results as this method. In other words, this
	 * method was required in order to maintain compatibility with the code in
	 * PCGen that tends to copy & clone things, but in the future, we are
	 * attempting to move away from that structure, so use of this method (which
	 * implies order dependency) is discouraged.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which data
	 *            will be replaced
	 * @param trigger
	 *            The trigger object to be used as the identifier to indicate
	 *            which object precedes the location where the given added
	 *            object is to be placed in the Collection for the Player
	 *            Character represented by the given CharID
	 * @param added
	 *            The object to be added to the the Collection for the Player
	 *            Character represented by the given CharID
	 */
	public void addAfter(CharID id, T trigger, T added)
	{
		Collection<T> componentSet = getCachedSet(id);
		if (componentSet != null && componentSet.contains(trigger))
		{
			Collection<T> replaceSet = getComponentSet();
			for (T obj : componentSet)
			{
				replaceSet.add(obj);
				if (obj == trigger)
				{
					replaceSet.add(added);
				}
			}
			setCache(id, thisClass, componentSet);
			fireDataFacetChangeEvent(id, added, DataFacetChangeEvent.DATA_ADDED);
		}
	}
}
