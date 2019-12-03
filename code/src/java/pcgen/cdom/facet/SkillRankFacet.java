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

import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import javax.swing.event.EventListenerList;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.core.PCClass;
import pcgen.core.Skill;

/**
 * SkillRankFacet stores the number of Skill Ranks for a specific Skill for a
 * Player Character.
 * 
 */
public class SkillRankFacet extends AbstractStorageFacet<CharID>
{
	private SkillRankChangeSupport support = new SkillRankChangeSupport();

	/**
	 * Returns the type-safe CacheInfo for this SkillRankFacet and the given
	 * CharID. Will return a new, empty CacheInfo if no Skill information has
	 * been set for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by SkillRankFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than SkillRankFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID.
	 */
	private Map<Skill, Map<PCClass, Double>> getConstructingInfo(CharID id)
	{
		Map<Skill, Map<PCClass, Double>> map = getInfo(id);
		if (map == null)
		{
			map = new HashMap<>();
			setCache(id, map);
		}
		return map;
	}

	/**
	 * Returns the type-safe CacheInfo for this SkillRankFacet and the given
	 * CharID. May return null if no Skill information has been set for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The CacheInfo object is owned
	 * by SkillRankFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than SkillRankFacet.
	 * 
	 * @param id
	 *            The CharID for which the CacheInfo should be returned
	 * @return The CacheInfo for the Player Character represented by the given
	 *         CharID; null if no Skill information has been set for the Player
	 *         Character.
	 */
	private Map<Skill, Map<PCClass, Double>> getInfo(CharID id)
	{
		return (Map<Skill, Map<PCClass, Double>>) getCache(id);
	}

	public void set(CharID id, Skill skill, PCClass pcc, double value)
	{
		Objects.requireNonNull(skill, "Skill cannot be null in add");
		float oldRank = getRank(id, skill);
		Map<Skill, Map<PCClass, Double>> map = getConstructingInfo(id);
		Map<PCClass, Double> clMap = map.get(skill);
		if (clMap == null)
		{
			clMap = new IdentityHashMap<>();
			map.put(skill, clMap);
		}
		clMap.put(pcc, value);

		float newRank = getRank(id, skill);
		support.fireSkillRankChangeEvent(id, skill, oldRank, newRank);
	}

	/**
	 * Copies the contents of the SkillRankFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in SkillRankFacet in order to avoid exposing the mutable
	 * Map object to other classes. This should not be inlined, as the Map is
	 * internal information to SkillRankFacet and should not be exposed to other
	 * classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the SkillRankFacet of one
	 * Player Character will only impact the Player Character where the
	 * SkillRankFacet was changed).
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
		Map<Skill, Map<PCClass, Double>> map = getInfo(source);
		if (map != null)
		{
			for (Entry<Skill, Map<PCClass, Double>> fme : map.entrySet())
			{
				Skill sk = fme.getKey();
				for (Entry<PCClass, Double> clEntry : fme.getValue().entrySet())
				{
					set(copy, sk, clEntry.getKey(), clEntry.getValue());
				}
			}
		}
	}

	public Collection<PCClass> getClasses(CharID id, Skill sk)
	{
		Map<Skill, Map<PCClass, Double>> map = getInfo(id);
		if (map == null)
		{
			return Collections.emptyList();
		}
		Map<PCClass, Double> clMap = map.get(sk);
		if (clMap == null)
		{
			return Collections.emptyList();
		}
		return Collections.unmodifiableSet(clMap.keySet());
	}

	public Double get(CharID id, Skill sk, PCClass pcc)
	{
		Map<Skill, Map<PCClass, Double>> map = getInfo(id);
		if (map == null)
		{
			return null;
		}
		Map<PCClass, Double> clMap = map.get(sk);
		if (clMap == null)
		{
			return null;
		}
		return clMap.get(pcc);
	}

	public void remove(CharID id, Skill sk, PCClass pcc)
	{
		Objects.requireNonNull(sk, "Skill cannot be null in remove");
		Map<Skill, Map<PCClass, Double>> map = getInfo(id);
		if (map != null)
		{
			Map<PCClass, Double> clMap = map.get(sk);
			if (clMap != null)
			{
				float oldRank = getRank(id, sk);
				clMap.remove(pcc);
				float newRank = getRank(id, sk);
				support.fireSkillRankChangeEvent(id, sk, oldRank, newRank);
			}
		}
	}

	public float getRank(CharID id, Skill sk)
	{
		double rank = 0.0;
		Map<Skill, Map<PCClass, Double>> map = getInfo(id);
		if (map != null)
		{
			Map<PCClass, Double> clMap = map.get(sk);
			if (clMap != null)
			{
				for (Double d : clMap.values())
				{
					rank += d;
				}
			}
		}
		return (float) rank;
	}

	public void addSkillRankChangeListener(SkillRankChangeListener listener)
	{
		support.addLevelChangeListener(listener);
	}

	@FunctionalInterface
	public interface SkillRankChangeListener extends EventListener
	{
		void rankChanged(SkillRankChangeEvent lce);
	}

	public static class SkillRankChangeEvent extends EventObject
	{
		/**
		 * The ID indicating the owning character for this SkillRankChangeEvent
		 */
		private final CharID charID;
		private final Skill skill;
		private final float oldRnk;
		private final float newRnk;

		public SkillRankChangeEvent(CharID source, Skill sk, float oldRank, float newRank)
		{
			super(source);
			Objects.requireNonNull(source, "CharID cannot be null");
			Objects.requireNonNull(sk, "PCClass cannot be null");
			charID = source;
			skill = sk;
			oldRnk = oldRank;
			newRnk = newRank;
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

		public Skill getSkill()
		{
			return skill;
		}

		public float getOldRank()
		{
			return oldRnk;
		}

		public float getNewRank()
		{
			return newRnk;
		}
	}

	public static class SkillRankChangeSupport
	{
		/**
		 * The listeners to which SkillRankChangeEvents will be fired when a
		 * change in the source SkillRankFacet occurs.
		 */
		private final EventListenerList listenerList = new EventListenerList();

		/**
		 * Adds a new SkillRankChangeListener to receive SkillRankChangeEvents
		 * (EdgeChangeEvent and NodeChangeEvent) from the source SkillRankFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to receive SkillRankChangeEvents
		 */
		public void addLevelChangeListener(SkillRankChangeListener listener)
		{
			listenerList.add(SkillRankChangeListener.class, listener);
		}

		/**
		 * Returns an Array of SkillRankChangeListeners receiving
		 * SkillRankChangeEvents from the source SkillRankFacet.
		 * 
		 * Ownership of the returned Array is transferred to the calling Object.
		 * No reference to the Array is maintained by ClassLevelChangeSupport.
		 * However, the SkillRankChangeListeners contained in the Array are
		 * (obviously!) returned BY REFERENCE, and care should be taken with
		 * modifying those SkillRankChangeListeners.*
		 * 
		 * @return An Array of SkillRankChangeListeners receiving
		 *         SkillRankChangeEvents from the source SkillRankFacet
		 */
		public synchronized SkillRankChangeListener[] getLevelChangeListeners()
		{
			return listenerList.getListeners(SkillRankChangeListener.class);
		}

		/**
		 * Removes a SkillRankChangeListener so that it will no longer receive
		 * SkillRankChangeEvents from the source SkillRankFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to be removed
		 */
		public void removeLevelChangeListener(SkillRankChangeListener listener)
		{
			listenerList.remove(SkillRankChangeListener.class, listener);
		}

		/**
		 * Sends a SkillRankChangeEvent to the SkillRankChangeListeners that are
		 * receiving SkillRankChangeEvents from the source SkillRankFacet.
		 * 
		 * @param id
		 *            The CharID on which the skill rank change has taken place
		 * @param sk
		 *            The Skill to be added to the list of PCClass objects
		 *            stored in this Facet for the Player Character represented
		 *            by the given CharID
		 * @param oldRank
		 *            The character's previous rank for the given skill
		 * 
		 * @param newRank
		 *            The character's new rank for the given skill.
		 */
		protected void fireSkillRankChangeEvent(CharID id, Skill sk, float oldRank, float newRank)
		{
			if (oldRank == newRank)
			{
				// Nothing to do
				return;
			}
			SkillRankChangeListener[] listeners = listenerList.getListeners(SkillRankChangeListener.class);
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 */
			SkillRankChangeEvent ccEvent = null;
			for (int i = listeners.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					ccEvent = new SkillRankChangeEvent(id, sk, oldRank, newRank);
				}
				listeners[i].rankChanged(ccEvent);
			}
		}
	}
}
