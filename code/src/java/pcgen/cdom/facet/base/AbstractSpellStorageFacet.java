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
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.event.EventListenerList;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.spell.Spell;

/**
 * AbstractSpellStorageFacet is a Facet that tracks the Spells (by level and
 * list) that are contained in a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public abstract class AbstractSpellStorageFacet extends AbstractStorageFacet
{

	private SpellChangeSupport support = new SpellChangeSupport();

	public void addAll(CharID id, CDOMList<Spell> list, int level,
		Collection<Spell> spells, Object source)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> map =
				getConstructingCachedMap(id);
		Map<Integer, Map<Spell, Set<Object>>> levelMap = map.get(list);
		if (levelMap == null)
		{
			levelMap = new HashMap<Integer, Map<Spell, Set<Object>>>();
			map.put(list, levelMap);
		}
		Map<Spell, Set<Object>> spellMap = levelMap.get(level);
		if (spellMap == null)
		{
			spellMap = new HashMap<Spell, Set<Object>>();
			levelMap.put(level, spellMap);
		}
		for (Spell spell : spells)
		{
			Set<Object> sources = spellMap.get(spell);
			boolean firenew = (sources == null);
			if (firenew)
			{
				sources = new WrappedMapSet<Object>(IdentityHashMap.class);
				spellMap.put(spell, sources);
			}
			sources.add(source);
			if (firenew)
			{
				support.fireSpellAddedChangeEvent(id,
					SpellChangeEvent.DATA_ADDED, list, spell, level);
			}
		}
	}

	public void add(CharID id, CDOMList<Spell> list, int level, Spell spell,
		Object cdo)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> map =
				getConstructingCachedMap(id);
		Map<Integer, Map<Spell, Set<Object>>> levelMap = map.get(list);
		boolean fireNew = (levelMap == null);
		if (fireNew)
		{
			levelMap = new HashMap<Integer, Map<Spell, Set<Object>>>();
			map.put(list, levelMap);
		}
		Map<Spell, Set<Object>> spellMap = levelMap.get(level);
		if (spellMap == null)
		{
			spellMap = new HashMap<Spell, Set<Object>>();
			levelMap.put(level, spellMap);
		}
		Set<Object> sources = spellMap.get(spell);
		boolean firenew = (sources == null);
		if (firenew)
		{
			sources = new WrappedMapSet<Object>(IdentityHashMap.class);
			spellMap.put(spell, sources);
		}
		sources.add(cdo);
		if (firenew)
		{
			support.fireSpellAddedChangeEvent(id, SpellChangeEvent.DATA_ADDED,
				list, spell, level);
		}
	}

	/**
	 * Removes all information for the given source from this
	 * ConditionallyKnownSpellFacet for the PlayerCharacter represented by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which items
	 *            from the given source will be removed
	 * @param source
	 *            The source for the objects to be removed from the list of
	 *            items stored for the Player Character identified by the given
	 *            CharID
	 */
	public void removeAll(CharID id, Object source)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap != null)
		{
			for (Iterator<Entry<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>>> lmit =
					listMap.entrySet().iterator(); lmit.hasNext();)
			{
				Entry<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> lme =
						lmit.next();
				CDOMList<Spell> list = lme.getKey();
				Map<Integer, Map<Spell, Set<Object>>> levelMap = lme.getValue();
				for (Iterator<Map.Entry<Integer, Map<Spell, Set<Object>>>> lvmit =
						levelMap.entrySet().iterator(); lvmit.hasNext();)
				{
					Entry<Integer, Map<Spell, Set<Object>>> lvme = lvmit.next();
					int level = lvme.getKey();
					Map<Spell, Set<Object>> spellMap = lvme.getValue();
					for (Iterator<Entry<Spell, Set<Object>>> smit =
							spellMap.entrySet().iterator(); smit.hasNext();)
					{
						Entry<Spell, Set<Object>> sme = smit.next();
						Set<Object> set = sme.getValue();
						if (set.remove(source) && set.isEmpty())
						{
							smit.remove();
							support.fireSpellAddedChangeEvent(id,
								SpellChangeEvent.DATA_REMOVED, list,
								sme.getKey(), level);
						}
					}
					if (spellMap.isEmpty())
					{
						lvmit.remove();
					}
				}
				if (levelMap.isEmpty())
				{
					lmit.remove();
				}
			}
		}
	}

	public void removeAll(CharID id, CDOMList<Spell> list, Integer level,
		List<Spell> toRemove, Object source)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap != null)
		{
			Map<Integer, Map<Spell, Set<Object>>> levelMap = listMap.get(list);
			if (levelMap != null)
			{
				Map<Spell, Set<Object>> spellMap = levelMap.get(level);
				if (spellMap != null)
				{
					for (Spell spell : toRemove)
					{
						Set<Object> sources = spellMap.get(spell);
						if ((sources != null) && sources.remove(source)
							&& sources.isEmpty())
						{
							spellMap.remove(spell);
							support.fireSpellAddedChangeEvent(id,
								SpellChangeEvent.DATA_REMOVED, list, spell,
								level);
						}
					}
					if (spellMap.isEmpty())
					{
						levelMap.remove(level);
					}
				}
				if (levelMap.isEmpty())
				{
					listMap.remove(level);
				}
			}
		}
	}

	public void remove(CharID id, CDOMList<Spell> list, Integer level,
		Spell spell, Object source)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap != null)
		{
			Map<Integer, Map<Spell, Set<Object>>> levelMap = listMap.get(list);
			if (levelMap != null)
			{
				Map<Spell, Set<Object>> spellMap = levelMap.get(level);
				if (spellMap != null)
				{
					Set<Object> sources = spellMap.get(spell);
					if ((sources != null) && sources.remove(source)
						&& sources.isEmpty())
					{
						spellMap.remove(spell);
						support.fireSpellAddedChangeEvent(id,
							SpellChangeEvent.DATA_REMOVED, list, spell, level);
					}
					if (spellMap.isEmpty())
					{
						levelMap.remove(level);
					}
				}
				if (levelMap.isEmpty())
				{
					listMap.remove(level);
				}
			}
		}
	}

	public Collection<CDOMList<Spell>> getSpellLists(CharID id)
	{
		List<CDOMList<Spell>> listInfo = new ArrayList<CDOMList<Spell>>();
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap != null)
		{
			listInfo.addAll(listMap.keySet());
		}
		return listInfo;
	}

	/**
	 * Returns the type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. May return null if no information has been set in this
	 * AbstractSourcedListFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this
	 *         AbstractSourcedListFacet for the Player Character.
	 */
	protected Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> getCachedMap(
		CharID id)
	{
		return (Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>>) getCache(
			id);
	}

	/**
	 * Returns a type-safe Map for this KnownSpellFacet and the given CharID.
	 * Will return a new, empty Map if no information has been set in this
	 * KnownSpellFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * KnownSpellFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than KnownSpellFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> getConstructingCachedMap(
		CharID id)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> componentMap =
				getCachedMap(id);
		if (componentMap == null)
		{
			componentMap =
					new HashMap<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>>();
			setCache(id, componentMap);
		}
		return componentMap;
	}

	/**
	 * Copies the contents of the KnwonSpellFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in KnownSpellFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to KnwonSpellFacet and should not be exposed
	 * to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the KnwonSpellFacet of one
	 * Player Character will only impact the Player Character where the
	 * KnwonSpellFacet was changed).
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
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> map =
				getCachedMap(source);
		if (map != null)
		{
			for (Entry<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> me : map
				.entrySet())
			{
				CDOMList<Spell> list = me.getKey();
				for (Entry<Integer, Map<Spell, Set<Object>>> levelE : me
					.getValue().entrySet())
				{
					Integer level = levelE.getKey();
					for (Entry<Spell, Set<Object>> spellE : levelE.getValue()
						.entrySet())
					{
						Spell spell = spellE.getKey();
						for (Object cdo : spellE.getValue())
						{
							add(copy, list, level, spell, cdo);
						}
					}
				}
			}
		}
	}

	public void addSpellChangeListener(SpellChangeListener listener)
	{
		support.addSpellChangeListener(listener);
	}

	public static interface SpellChangeListener extends EventListener
	{
		public void spellAdded(SpellChangeEvent sce);

		public void spellRemoved(SpellChangeEvent sce);
	}

	public static class SpellChangeEvent extends EventObject
	{
		/**
		 * The constant ID used by an DataFacetChangeEvent to indicate that a
		 * DataFacetChangeEvent was the result of a CDOMObject being added to a
		 * PlayerCharacter.
		 */
		public static final int DATA_ADDED = 0;

		/**
		 * The constant ID used by an DataFacetChangeEvent to indicate that a
		 * DataFacetChangeEvent was the result of a CDOMObject being removed
		 * from a PlayerCharacter.
		 */
		public static final int DATA_REMOVED = 1;

		/**
		 * The ID indicating the owning character for this SpellChangeEvent
		 */
		private final CharID charID;
		private final int eventType;
		private final CDOMList<Spell> spelllist;
		private final Spell spell;
		private final int lvl;

		public SpellChangeEvent(CharID source, int type, CDOMList<Spell> list,
			Spell sp, int level)
		{
			super(source);
			if (source == null)
			{
				throw new IllegalArgumentException("CharID cannot be null");
			}
			if (list == null)
			{
				throw new IllegalArgumentException("Spelllist cannot be null");
			}
			if (sp == null)
			{
				throw new IllegalArgumentException("Spell cannot be null");
			}
			charID = source;
			eventType = type;
			spelllist = list;
			spell = sp;
			lvl = level;
		}

		/**
		 * Returns an identifier indicating the PlayerCharacter on which this
		 * event occurred.
		 * 
		 * @return A identifier indicating the PlayerCharacter on which this
		 *         event occurred.
		 */
		public CharID getCharID()
		{
			return charID;
		}

		public Spell getSpell()
		{
			return spell;
		}

		public int getLevel()
		{
			return lvl;
		}

		public CDOMList<Spell> getSpellList()
		{
			return spelllist;
		}

		public int getEventType()
		{
			return eventType;
		}
	}

	public static class SpellChangeSupport
	{
		/**
		 * The listeners to which SpellChangeEvents will be fired when a change
		 * in the source SpellFacet occurs.
		 */
		private final EventListenerList listenerList = new EventListenerList();

		/**
		 * Adds a new SpellChangeListener to receive SpellChangeEvents
		 * (EdgeChangeEvent and NodeChangeEvent) from the source SpellFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to receive SpellChangeEvents
		 */
		public void addSpellChangeListener(SpellChangeListener listener)
		{
			listenerList.add(SpellChangeListener.class, listener);
		}

		/**
		 * Returns an Array of SpellChangeListeners receiving SpellChangeEvents
		 * from the source SpellFacet.
		 * 
		 * Ownership of the returned Array is transferred to the calling Object.
		 * No reference to the Array is maintained by ClassLevelChangeSupport.
		 * However, the SpellChangeListeners contained in the Array are
		 * (obviously!) returned BY REFERENCE, and care should be taken with
		 * modifying those SpellChangeListeners.*
		 * 
		 * @return An Array of SpellChangeListeners receiving SpellChangeEvents
		 *         from the source SpellFacet
		 */
		public synchronized SpellChangeListener[] getLevelChangeListeners()
		{
			return listenerList.getListeners(SpellChangeListener.class);
		}

		/**
		 * Removes a SpellChangeListener so that it will no longer receive
		 * SpellChangeEvents from the source SpellFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to be removed
		 */
		public void removeLevelChangeListener(SpellChangeListener listener)
		{
			listenerList.remove(SpellChangeListener.class, listener);
		}

		/**
		 * Sends a SpellChangeEvent to the SpellChangeListeners that are
		 * receiving SpellChangeEvents from the source SpellFacet.
		 * 
		 * @param id
		 *            The CharID on which the spell change has taken place
		 * @param type
		 *            The type of change (Add or Remove)
		 * @param sp
		 *            The Spell added to or removed from the objects stored in
		 *            this Facet for the Player Character represented by the
		 *            given CharID
		 * @param level
		 *            The level for the Spell added or removed
		 */
		protected void fireSpellAddedChangeEvent(CharID id, int type,
			CDOMList<Spell> list, Spell sp, int level)
		{
			SpellChangeListener[] listeners =
					listenerList.getListeners(SpellChangeListener.class);
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 */
			SpellChangeEvent ccEvent = null;
			for (int i = listeners.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					ccEvent = new SpellChangeEvent(id, type, list, sp, level);
				}
				switch (type)
				{
					case SpellChangeEvent.DATA_ADDED:
						listeners[i].spellAdded(ccEvent);
						break;
					case SpellChangeEvent.DATA_REMOVED:
						listeners[i].spellRemoved(ccEvent);
						break;
					default:
						break;
				}
				listeners[i].spellAdded(ccEvent);
			}
		}
	}
}
