/*
 * Copyright (c) Thomas Parker, 2013.
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
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.event.ScopeFacetChangeEvent;
import pcgen.cdom.facet.event.ScopeFacetChangeListener;

public class AbstractScopeFacet<S, T> extends AbstractStorageFacet
{
	private Map<S, Map<T, Set<Object>>> getConstructingInfo(CharID id)
	{
		Map<S, Map<T, Set<Object>>> map = getInfo(id);
		if (map == null)
		{
			map = new IdentityHashMap<S, Map<T, Set<Object>>>();
			setCache(id, map);
		}
		return map;
	}

	private Map<S, Map<T, Set<Object>>> getInfo(CharID id)
	{
		return (Map<S, Map<T, Set<Object>>>) getCache(id);
	}

	public void add(CharID id, S scope, T obj, Object source)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("Scope cannot be null");
		}
		if(obj == null)
		{
			throw new IllegalArgumentException("Object cannot be null");
		}
		Map<S, Map<T, Set<Object>>> map = getConstructingInfo(id);
		Map<T, Set<Object>> scopeMap = map.get(scope);
		if (scopeMap == null)
		{
			scopeMap = new IdentityHashMap<T, Set<Object>>();
			map.put(scope, scopeMap);
		}
		Set<Object> sources = scopeMap.get(obj);
		boolean isNew = (sources == null);
		if (isNew)
		{
			sources = new WrappedMapSet<Object>(IdentityHashMap.class);
			scopeMap.put(obj, sources);
		}
		sources.add(source);
		if (isNew)
		{
			fireScopeFacetChangeEvent(id, scope, obj,
				ScopeFacetChangeEvent.DATA_ADDED);
		}
	}

	public void remove(CharID id, S scope, T obj, Object source)
	{
		if (scope == null)
		{
			throw new IllegalArgumentException("Scope cannot be null");
		}
		if(obj == null)
		{
			throw new IllegalArgumentException("Object cannot be null");
		}
		Map<S, Map<T, Set<Object>>> map = getInfo(id);
		if (map == null)
		{
			return;
		}
		Map<T, Set<Object>> scopeMap = map.get(scope);
		if (scopeMap == null)
		{
			return;
		}
		Set<Object> sources = scopeMap.get(obj);
		if (sources == null)
		{
			return;
		}
		if (sources.remove(source) && sources.isEmpty())
		{
			fireScopeFacetChangeEvent(id, scope, obj,
				ScopeFacetChangeEvent.DATA_REMOVED);
			scopeMap.remove(obj);
		}
		if (scopeMap.isEmpty())
		{
			map.remove(scope);
		}
		if (map.isEmpty())
		{
			removeCache(id);
		}
	}

	public Collection<T> getSet(CharID id, S scope)
	{
		Map<S, Map<T, Set<Object>>> map = getInfo(id);
		if (map == null)
		{
			return Collections.emptyList();
		}
		Map<T, Set<Object>> scopeMap = map.get(scope);
		if (scopeMap == null)
		{
			return Collections.emptyList();
		}
		return new ArrayList<T>(scopeMap.keySet());
	}

	public boolean contains(CharID id, S scope, T obj)
	{
		Map<S, Map<T, Set<Object>>> map = getInfo(id);
		if (map == null)
		{
			return false;
		}
		Map<T, Set<Object>> scopeMap = map.get(scope);
		return (scopeMap != null) && scopeMap.containsKey(obj);
	}

	public void removeAllFromSource(CharID id, Object source)
	{
		Map<S, Map<T, Set<Object>>> map = getInfo(id);
		if (map != null)
		{
			for (Iterator<Map.Entry<S, Map<T, Set<Object>>>> it =
					map.entrySet().iterator(); it.hasNext();)
			{
				Entry<S, Map<T, Set<Object>>> entry = it.next();
				S scope = entry.getKey();
				Map<T, Set<Object>> scopeMap = entry.getValue();
				for (Iterator<Map.Entry<T, Set<Object>>> lmit =
						scopeMap.entrySet().iterator(); lmit.hasNext();)
				{
					Entry<T, Set<Object>> lme = lmit.next();
					Set<Object> sources = lme.getValue();
					if (sources.remove(source) && sources.isEmpty())
					{
						T obj = lme.getKey();
						lmit.remove();
						fireScopeFacetChangeEvent(id, scope, obj,
							ScopeFacetChangeEvent.DATA_REMOVED);
					}
				}
				if (scopeMap.isEmpty())
				{
					it.remove();
				}
			}
			if (map.isEmpty())
			{
				removeCache(id);
			}
		}
	}

	/**
	 * Copies the contents of the AbstractScopeFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in AbstractScopeFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to AbstractScopeFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the AbstractScopeFacet of one
	 * Player Character will only impact the Player Character where the
	 * AbstractScopeFacet was changed).
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
		Map<S, Map<T, Set<Object>>> map = getInfo(source);
		if (map != null)
		{
			for (Entry<S, Map<T, Set<Object>>> lme : map.entrySet())
			{
				S scope = lme.getKey();
				for (Entry<T, Set<Object>> ome : lme.getValue().entrySet())
				{
					T sp = ome.getKey();
					for (Object spsource : ome.getValue())
					{
						add(copy, scope, sp, spsource);
					}
				}
			}
		}
	}

	private final Map<Integer, ScopeFacetChangeListener<? super S, ? super T>[]> listeners =
			new TreeMap<Integer, ScopeFacetChangeListener<? super S, ? super T>[]>();

	/**
	 * Adds a new ScopeFacetChangeListener to receive ScopeFacetChangeEvents
	 * (EdgeChangeEvent and NodeChangeEvent) from this AbstractScopeFacet. The
	 * given ScopeFacetChangeListener is added at the default priority (zero).
	 * 
	 * Note that the ScopeFacetChangeListeners are a list, meaning a given
	 * ScopeFacetChangeListener can be added more than once at a given priority,
	 * and if that occurs, it must be removed an equivalent number of times in
	 * order to no longer receive events from this AbstractScopeFacet.
	 * 
	 * @param listener
	 *            The ScopeFacetChangeListener to receive ScopeFacetChangeEvents
	 *            from this AbstractScopeFacet
	 */
	public void addScopeFacetChangeListener(
		ScopeFacetChangeListener<? super S, ? super T> listener)
	{
		addScopeFacetChangeListener(0, listener);
	}

	/**
	 * Adds a new ScopeFacetChangeListener to receive ScopeFacetChangeEvents
	 * (EdgeChangeEvent and NodeChangeEvent) from this AbstractScopeFacet.
	 * 
	 * The ScopeFacetChangeListener is added at the given priority.
	 * 
	 * Note that the ScopeFacetChangeListeners are a list, meaning a given
	 * ScopeFacetChangeListener can be added more than once at a given priority,
	 * and if that occurs, it must be removed an equivalent number of times in
	 * order to no longer receive events from this AbstractScopeFacet.
	 * 
	 * @param listener
	 *            The ScopeFacetChangeListener to receive ScopeFacetChangeEvents
	 *            from this AbstractScopeFacet
	 */
	public void addScopeFacetChangeListener(int priority,
		ScopeFacetChangeListener<? super S, ? super T> listener)
	{
		ScopeFacetChangeListener<? super S, ? super T>[] dfcl =
				listeners.get(priority);
		int newSize = (dfcl == null) ? 1 : (dfcl.length + 1);
		ScopeFacetChangeListener<? super S, ? super T>[] newArray =
				new ScopeFacetChangeListener[newSize];
		if (dfcl != null)
		{
			System.arraycopy(dfcl, 0, newArray, 1, dfcl.length);
		}
		newArray[0] = listener;
		listeners.put(priority, newArray);
	}

	/**
	 * Removes a ScopeFacetChangeListener so that it will no longer receive
	 * ScopeFacetChangeEvents from this AbstractScopeFacet. This will remove the
	 * data facet change listener from the default priority (zero).
	 * 
	 * Note that if the given ScopeFacetChangeListener has been registered under
	 * a different priority, it will still receive events at that priority
	 * level.
	 * 
	 * @param listener
	 *            The ScopeFacetChangeListener to be removed
	 */
	public void removeScopeFacetChangeListener(
		ScopeFacetChangeListener<? super S, ? super T> listener)
	{
		removeScopeFacetChangeListener(0, listener);
	}

	/**
	 * Removes a ScopeFacetChangeListener so that it will no longer receive
	 * ScopeFacetChangeEvents from the source DataFacet. This will remove the
	 * data facet change listener from the given priority.
	 * 
	 * Note that if the given ScopeFacetChangeListener has been registered under
	 * a different priority, it will still receive events at that priority
	 * level.
	 * 
	 * @param listener
	 *            The ScopeFacetChangeListener to be removed
	 */
	public void removeScopeFacetChangeListener(int priority,
		ScopeFacetChangeListener<? super S, ? super T> listener)
	{
		ScopeFacetChangeListener<? super S, ? super T>[] dfcl =
				listeners.get(priority);
		if (dfcl == null)
		{
			// No worries
			return;
		}
		int foundLoc = -1;
		int newSize = dfcl.length - 1;
		for (int i = newSize; i >= 0; i--)
		{
			if (dfcl[i] == listener)
			{
				foundLoc = i;
				break;
			}
		}
		if (foundLoc != -1)
		{
			if (dfcl.length == 1)
			{
				listeners.remove(priority);
			}
			else
			{
				ScopeFacetChangeListener<? super S, ? super T>[] newArray =
						new ScopeFacetChangeListener[newSize];
				if (foundLoc != 0)
				{
					System.arraycopy(dfcl, 0, newArray, 0, foundLoc);
				}
				if (foundLoc != newSize)
				{
					System.arraycopy(dfcl, foundLoc + 1, newArray, foundLoc,
						newSize - foundLoc);
				}
				listeners.put(priority, newArray);
			}
		}
	}

	/**
	 * Sends a NodeChangeEvent to the ScopeFacetChangeListeners that are
	 * receiving ScopeFacetChangeEvents from this AbstractScopeFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character to which the
	 *            NodeChangeEvent relates
	 * @param scope
	 *            The Scope through which this facet's contents are viewed
	 * @param node
	 *            The Node that has been added to or removed from this
	 *            AbstractScopeFacet for the given CharID
	 * @param type
	 *            An identifier indicating whether the given CDOMObject was
	 *            added to or removed from this AbstractScopeFacet
	 */
	@SuppressWarnings("rawtypes")
	protected void fireScopeFacetChangeEvent(CharID id, S scope, T node, int type)
	{
		for (ScopeFacetChangeListener<? super S, ? super T>[] dfclArray : listeners
			.values())
		{
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events. This is obviously
			 * subordinate to the priority (loop above).
			 */
			ScopeFacetChangeEvent<S, T> ccEvent = null;
			for (int i = dfclArray.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					ccEvent =
							new ScopeFacetChangeEvent<S, T>(id, scope, node,
								this, type);
				}
				ScopeFacetChangeListener dfcl = dfclArray[i];
				switch (ccEvent.getEventType())
				{
					case ScopeFacetChangeEvent.DATA_ADDED:
						dfcl.dataAdded(ccEvent);
						break;
					case ScopeFacetChangeEvent.DATA_REMOVED:
						dfcl.dataRemoved(ccEvent);
						break;
					default:
						break;
				}
			}
		}
	}

}
