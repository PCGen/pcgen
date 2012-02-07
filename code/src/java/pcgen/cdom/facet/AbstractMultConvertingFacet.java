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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;

/**
 * @author Thomas Parker (thpr [at] yahoo.com)
 * 
 * A AbstractMultConvertingFacet is a DataFacet that converts information from
 * one type to another when the source of that object should be tracked.
 * 
 * This class is designed to assume that each original object may only be
 * contained one time by the PlayerCharacter, even if received from multiple
 * sources. The original object will only trigger one DATA_ADDED event (when
 * added by the first source) and if removed by some sources, will only trigger
 * one DATA_REMOVED event (when it is removed by the last remaining source).
 * Sources do not need to be removed in the order in which they are added, and
 * the first source to be added does not possess special status with respect to
 * triggering a DATA_REMOVED event (it will only trigger removal if it was the
 * last source when removed)
 * 
 * The sources stored in this AbstractMultConvertingFacet are stored as a List,
 * meaning the list of sources may contain the same source multiple times. If
 * so, each call to remove will only remove that source one time from the list
 * of sources.
 * 
 * null is a valid source.
 */
public abstract class AbstractMultConvertingFacet<S, D> extends
		AbstractDataFacet<D>
{
	/**
	 * Add the given object with the given source to the list of objects stored
	 * in this AbstractMultConvertingFacet for the Player Character represented
	 * by the given CharID
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
	public void add(CharID id, S obj, Object source)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		Map<S, Target> map = getConstructingCachedMap(id);
		Target target = map.get(obj);
		boolean fireNew = (target == null);
		if (fireNew)
		{
			target = new Target();
			map.put(obj, target);
		}
		D newDest = convert(obj);
		target.dest.add(newDest);
		target.set.add(source);
		if (fireNew)
		{
			fireDataFacetChangeEvent(id, newDest,
					DataFacetChangeEvent.DATA_ADDED);
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
	public void addAll(CharID id, Collection<? extends S> c, Object source)
	{
		for (S obj : c)
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
	public void remove(CharID id, S obj, Object source)
	{
		Map<S, Target> componentMap = getCachedMap(id);
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
	public void removeAll(CharID id, Collection<S> c, Object source)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (S obj : c)
			{
				processRemoval(id, componentMap, obj, source);
			}
		}
	}

	/**
	 * Removes all objects (and all sources for those objects) from the list of
	 * objects stored in this AbstractMultConvertingFacet for the Player
	 * Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            items should be removed
	 * @return A non-null Set of objects removed from the list of objects stored
	 *         in this AbstractMultConvertingFacet for the Player Character
	 *         represented by the given CharID
	 */
	public Map<S, Set<Object>> removeAll(CharID id)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptyMap();
		}
		removeCache(id, getClass());
		IdentityHashMap<S, Set<Object>> map = new IdentityHashMap<S, Set<Object>>();
		for (Map.Entry<S, Target> me : componentMap.entrySet())
		{
			Target tgt = me.getValue();
			for (D dest : tgt.dest)
			{
				fireDataFacetChangeEvent(id, dest,
						DataFacetChangeEvent.DATA_REMOVED);
			}
			map.put(me.getKey(), tgt.set);
		}
		return map;
	}

	/**
	 * Returns the Set of objects in this AbstractQualifiedListFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this AbstractQualifiedListFacet should be returned.
	 * @return A non-null Set of objects in this AbstractQualifiedListFacet for
	 *         the Player Character represented by the given CharID
	 */
	public Set<D> getSet(CharID id)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return Collections.emptySet();
		}
		Set<D> dest = new WrappedMapSet<D>(IdentityHashMap.class);;
		for (Target target : componentMap.values())
		{
			dest.addAll(target.dest);
		}
		return Collections.unmodifiableSet(dest);
	}

	/**
	 * Returns the count of items in this AbstractMultConvertingFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of items should be returned
	 * @return The count of items in this AbstractMultConvertingFacet for the
	 *         Player Character represented by the given CharID
	 */
	public int getCount(CharID id)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			return 0;
		}
		return componentMap.size();
	}

	/**
	 * Returns true if this AbstractMultConvertingFacet does not contain any
	 * items for the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this AbstractsSourcedListFacet
	 * @return true if this AbstractMultConvertingFacet does not contain any
	 *         items for the Player Character represented by the given CharID;
	 *         false otherwise (if it does contain items for the Player
	 *         Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		return componentMap == null || componentMap.isEmpty();
	}

	/**
	 * Returns true if this AbstractMultConvertingFacet contains the given value
	 * in the list of items for the Player Character represented by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The object to test if this AbstractMultConvertingFacet
	 *            contains that item for the Player Character represented by the
	 *            given CharID
	 * @return true if this AbstractMultConvertingFacet contains the given value
	 *         for the Player Character represented by the given CharID; false
	 *         otherwise
	 */
	public boolean contains(CharID id, S obj)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		return componentMap != null && componentMap.containsKey(obj);
	}

	/**
	 * Returns a Set of sources for this AbstractMultConvertingFacet, the
	 * PlayerCharacter represented by the given CharID, and the given object.
	 * Will add the given object to the list of items for the PlayerCharacter
	 * represented by the given CharID and will return a new, empty Set if no
	 * information has been set in this AbstractMultConvertingFacet for the
	 * given CharID and given object. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Set object is owned by
	 * AbstractMultConvertingFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractMultConvertingFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @param obj
	 *            The object for which the Set of sources should be returned
	 * @return The Set of sources for the given object and Player Character
	 *         represented by the given CharID.
	 */
	private Target getConstructingCachedSetFor(CharID id, S obj)
	{
		Map<S, Target> map = getConstructingCachedMap(id);
		Target target = map.get(obj);
		if (target == null)
		{
			target = new Target();
			map.put(obj, target);
		}
		return target;
	}

	/**
	 * Returns the type-safe Map for this AbstractMultConvertingFacet and the
	 * given CharID. May return null if no information has been set in this
	 * AbstractMultConvertingFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractMultConvertingFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractMultConvertingFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractMultConvertingFacet for the Player Character.
	 */
	protected Map<S, Target> getCachedMap(CharID id)
	{
		return (Map<S, Target>) getCache(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AbstractMultConvertingFacet and the
	 * given CharID. Will return a new, empty Map if no information has been set
	 * in this AbstractMultConvertingFacet for the given CharID. Will not return
	 * null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractMultConvertingFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractMultConvertingFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<S, Target> getConstructingCachedMap(CharID id)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = getComponentMap();
			setCache(id, getClass(), componentMap);
		}
		return componentMap;
	}

	protected Map<S, Target> getComponentMap()
	{
		return new IdentityHashMap<S, Target>();
	}

	/**
	 * Copies the contents of the AbstractMultConvertingFacet from one Player
	 * Character to another Player Character, based on the given CharIDs
	 * representing those Player Characters.
	 * 
	 * This is a method in AbstractMultConvertingFacet in order to avoid
	 * exposing the mutable Map object to other classes. This should not be
	 * inlined, as the Map is internal information to
	 * AbstractMultConvertingFacet and should not be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AbstractMultConvertingFacet
	 * of one Player Character will only impact the Player Character where the
	 * AbstractMultConvertingFacet was changed).
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
		Map<S, Target> sourceMap = getCachedMap(source);
		if (sourceMap != null)
		{
			for (Map.Entry<S, Target> me : sourceMap.entrySet())
			{
				Target origTarget = me.getValue();
				if (origTarget != null)
				{
					S obj = me.getKey();
					Target target = getConstructingCachedSetFor(destination,
							obj);
					//This could be dangerous!
					target.dest.addAll(origTarget.dest);
					target.set.addAll(origTarget.set);
				}
			}
		}
	}

	/**
	 * This method implements removal of a source for an object contained by
	 * this AbstractMultConvertingFacet. This implements the actual check that
	 * determines if the given source was the only source for the given object.
	 * If so, then that object is removed from the list of objects stored in
	 * this AbstractQualifiedListFacet for the Player Character represented by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character which may have
	 *            the given item removed.
	 * @param componentMap
	 *            The (private) Map for this AbstractMultConvertingFacet that
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
	private void processRemoval(CharID id, Map<S, Target> componentMap, S obj,
			Object source)
	{
		if (obj == null)
		{
			throw new IllegalArgumentException(
					"Object to remove may not be null");
		}
		Target target = componentMap.get(obj);
		if (target != null)
		{
			if (target.set.remove(source))
			{
				Iterator<D> it = target.dest.iterator();
				D dest = it.next();
				it.remove();
				fireDataFacetChangeEvent(id, dest,
						DataFacetChangeEvent.DATA_REMOVED);
			}
			if (target.set.isEmpty())
			{
				componentMap.remove(obj);
			}
		}
	}

	public void removeAll(CharID id, Object source)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Target> it = componentMap.values().iterator(); it
					.hasNext();)
			{
				Target target = it.next();
				if (target != null)
				{
					if (target.set.remove(source))
					{
						Iterator<D> di = target.dest.iterator();
						D dest = di.next();
						di.remove();
						fireDataFacetChangeEvent(id, dest,
								DataFacetChangeEvent.DATA_REMOVED);
					}
					if (target.set.isEmpty())
					{
						it.remove();
					}
				}
			}
		}
	}

	public boolean containsFrom(CharID id, Object owner)
	{
		Map<S, Target> componentMap = getCachedMap(id);
		if (componentMap != null)
		{
			for (Iterator<Map.Entry<S, Target>> it = componentMap.entrySet()
					.iterator(); it.hasNext();)
			{
				Entry<S, Target> me = it.next();
				Target target = me.getValue();
				if (target != null)
				{
					if (target.set.contains(owner))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	private class Target
	{
		public Set<Object> set = new WrappedMapSet<Object>(
				IdentityHashMap.class);
		public Set<D> dest = new WrappedMapSet<D>(IdentityHashMap.class);;

		@Override
		public int hashCode()
		{
			return dest.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == this)
			{
				return true;
			}
			if (o instanceof AbstractMultConvertingFacet.Target)
			{
				Target other = (Target) o;
				return dest.equals(other.dest) && set.equals(other.set);
			}
			return false;
		}
	}

	protected abstract D convert(S obj);
}
