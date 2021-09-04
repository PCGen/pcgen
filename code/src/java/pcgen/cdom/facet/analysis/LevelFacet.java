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
package pcgen.cdom.facet.analysis;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.facet.FormulaResolvingFacet;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.cdom.facet.model.RaceFacet;
import pcgen.cdom.facet.model.TemplateFacet;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

/**
 * LevelFacet stores information about the Level of a Player Character. This
 * includes the ability to distinguish what is a Monster Level, PC level, NPC
 * level, etc.
 * 
 */
public class LevelFacet extends AbstractStorageFacet<CharID> implements ClassLevelChangeListener
{
	private TemplateFacet templateFacet;
	private RaceFacet raceFacet;
	private FormulaResolvingFacet formulaResolvingFacet;
	private final LevelChangeSupport support = new LevelChangeSupport();

	/**
	 * Returns the non-Monster level count for the Player Character identified
	 * by the given CharID.
	 * 
	 * @param id
	 *            The CharID of the Player Character for which the non-Monster
	 *            level count will be returned
	 * @return The non-Monster level count for the Player Character identified
	 *         by the given CharID
	 */
	public int getNonMonsterLevelCount(CharID id)
	{
		LevelCacheInfo info = getInfo(id);
		return info == null ? 0 : info.nonMonsterLevels;
	}

	/**
	 * Returns the Monster level count for the Player Character identified by
	 * the given CharID.
	 * 
	 * @param id
	 *            The CharID of the Player Character for which the Monster level
	 *            count will be returned
	 * @return The Monster level count for the Player Character identified by
	 *         the given CharID
	 */
	public int getMonsterLevelCount(CharID id)
	{
		LevelCacheInfo info = getInfo(id);
		return info == null ? 0 : info.monsterLevels;
	}

	/**
	 * Returns the level adjustment for the Player Character identified by the
	 * given CharID.
	 * 
	 * @param id
	 *            The CharID of the Player Character for which the level
	 *            adjustment will be returned
	 * @return The level adjustment for the Player Character identified by the
	 *         given CharID
	 */
	public int getLevelAdjustment(CharID id)
	{
		Race race = raceFacet.get(id);
		int levelAdj = 0;

		if (race != null)
		{
			Formula raceLA = race.getSafe(FormulaKey.LEVEL_ADJUSTMENT);
			levelAdj += formulaResolvingFacet.resolve(id, raceLA, "").intValue();
		}

		for (PCTemplate template : templateFacet.getSet(id))
		{
			Formula templateLA = template.getSafe(FormulaKey.LEVEL_ADJUSTMENT);
			levelAdj += formulaResolvingFacet.resolve(id, templateLA, "").intValue();
		}

		return levelAdj;
	}

	/**
	 * Returns the Effective Character Level (ECL) for the Player Character
	 * identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Effective Character Level will be returned
	 * @return The Effective Character Level (ECL) for the Player Character
	 *         identified by the given CharID
	 */
	public int getECL(CharID id)
	{
		int levelAdjustment = getLevelAdjustment(id);
		LevelCacheInfo info = getInfo(id);
		if (info == null)
		{
			return levelAdjustment;
		}
		return info.nonMonsterLevels + info.monsterLevels + levelAdjustment;
	}

	/**
	 * Returns the total levels for the Player Character identified by the given
	 * CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            total levels will be returned
	 * @return The total levels for the Player Character identified by the given
	 *         CharID
	 * 
	 */
	public int getTotalLevels(CharID id)
	{
		LevelCacheInfo info = getInfo(id);
		// Monster hit dice count towards total levels
		// sage_sam changed 03 Dec 2002 for Bug #646816
		return info == null ? 0 : info.nonMonsterLevels + info.monsterLevels;
	}

	/**
	 * Returns the LevelCacheInfofor this LevelFacet and the given CharID. Will
	 * return a new, empty LevelCacheInfo if no information has been set in this
	 * LevelFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The LevelCacheInfo object is
	 * owned by LevelFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than LevelFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The LevelCacheInfo for the Player Character represented by the
	 *         given CharID.
	 */
	private LevelCacheInfo getConstructingInfo(CharID id)
	{
		LevelCacheInfo lci = getInfo(id);
		if (lci == null)
		{
			lci = new LevelCacheInfo();
			setCache(id, lci);
		}
		return lci;
	}

	/**
	 * Returns the LevelCacheInfofor this LevelFacet and the given CharID. Will
	 * return null if no information has been set in this LevelFacet for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The LevelCacheInfo object is
	 * owned by LevelFacet, and since it can be modified, a reference to that
	 * object should not be exposed to any object other than LevelFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The LevelCacheInfo for the Player Character represented by the
	 *         given CharID.
	 */
	private LevelCacheInfo getInfo(CharID id)
	{
		return (LevelCacheInfo) getCache(id);
	}

	/**
	 * Data structure for caching level information about a Player Character
	 * 
	 */
	private static class LevelCacheInfo
	{
		public int monsterLevels;
		public int nonMonsterLevels;

		@Override
		public int hashCode()
		{
			return monsterLevels * nonMonsterLevels;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof LevelCacheInfo other)
			{
				return monsterLevels == other.monsterLevels && nonMonsterLevels == other.nonMonsterLevels;
			}
			return false;
		}
	}

	@Override
	public void levelChanged(ClassLevelChangeEvent lce)
	{
		CharID id = lce.getCharID();
		LevelCacheInfo lci = getConstructingInfo(id);
		int levelChange = lce.getNewLevel() - lce.getOldLevel();
		if (levelChange != 0)
		{
			if (lce.getPCClass().isMonster())
			{
				lci.monsterLevels += levelChange;
			}
			else
			{
				lci.nonMonsterLevels += levelChange;
			}
			support.fireLevelChangeEvent(id);
		}
	}

	@Override
	public void levelObjectChanged(ClassLevelObjectChangeEvent lce)
	{
		//ignore
	}

	/**
	 * Adds a LevelChangeListener to receive LevelChangeEvents from LevelFacet.
	 * 
	 * Note that the LevelChangeListeners are a list, meaning a given
	 * LevelChangeListener can be added more than once, and if that occurs, it
	 * must be removed an equivalent number of times in order to no longer
	 * receive events from this LevelFacet.
	 * 
	 * @param listener
	 *            The LevelChangeListener to receive LevelChangeEvents from this
	 *            LevelFacet
	 */
	public void addLevelChangeListener(LevelChangeListener listener)
	{
		support.addLevelChangeListener(listener);
	}

	/**
	 * Removes a LevelChangeListener so that it does not receive
	 * LevelChangeEvents from LevelFacet.
	 * 
	 * Note that the LevelChangeListeners are a list, meaning a given
	 * LevelChangeListener can be added more than once, and if that occurs, it
	 * must be removed an equivalent number of times in order to no longer
	 * receive events from this LevelFacet.
	 * 
	 * @param listener
	 *            The LevelChangeListener to no longer receive LevelChangeEvents
	 *            from this LevelFacet
	 */
	public void removeLevelChangeListener(LevelChangeListener listener)
	{
		support.removeLevelChangeListener(listener);
	}

	/**
	 * Interface for a LevelChangeListener that wants to receive
	 * LevelChangeEvents from LevelFacet.
	 * 
	 */
	@FunctionalInterface
	public static interface LevelChangeListener extends EventListener
	{
		public void levelChanged(LevelChangeEvent lce);
	}

	public static class LevelChangeEvent extends EventObject
	{

		/**
		 * The ID indicating the owning character for this DataFacetChangeEvent
		 */
		private final CharID charID;

		public LevelChangeEvent(CharID source)
		{
			super(source);
			charID = source;
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
	}

	public static class LevelChangeSupport
	{
		/**
		 * The listeners to which LevelChangeEvents will be fired when a change
		 * in the source DataFacet occurs.
		 */
		private final EventListenerList listenerList = new EventListenerList();

		/**
		 * Adds a new LevelChangeListener to receive LevelChangeEvents from the
		 * source DataFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to receive LevelChangeEvents
		 */
		public void addLevelChangeListener(LevelChangeListener listener)
		{
			listenerList.add(LevelChangeListener.class, listener);
		}

		/**
		 * Returns an Array of LevelChangeListeners receiving LevelChangeEvents
		 * from the source DataFacet.
		 * 
		 * Ownership of the returned Array is transferred to the calling Object.
		 * No reference to the Array is maintained by LevelChangeSupport.
		 * However, the LevelChangeListeners contained in the Array are
		 * (obviously!) returned BY REFERENCE, and care should be taken with
		 * modifying those LevelChangeListeners.*
		 * 
		 * @return An Array of LevelChangeListeners receiving LevelChangeEvents
		 *         from the source DataFacet
		 */
		public synchronized LevelChangeListener[] getLevelChangeListeners()
		{
			return listenerList.getListeners(LevelChangeListener.class);
		}

		/**
		 * Removes a LevelChangeListener so that it will no longer receive
		 * LevelChangeEvents from the source DataFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to be removed
		 */
		public void removeLevelChangeListener(LevelChangeListener listener)
		{
			listenerList.remove(LevelChangeListener.class, listener);
		}

		/**
		 * Sends a LevelChangeEvent to the LevelChangeListeners that are
		 * receiving LevelChangeEvents from the source DataFacet.
		 * 
		 * @param id
		 *            An identifier indicating the Player Character on which a
		 *            level change has occurred
		 */
		protected void fireLevelChangeEvent(CharID id)
		{
			LevelChangeListener[] listeners = listenerList.getListeners(LevelChangeListener.class);
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 */
			LevelChangeEvent ccEvent = null;
			for (int i = listeners.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					ccEvent = new LevelChangeEvent(id);
				}
				listeners[i].levelChanged(ccEvent);
			}
		}
	}

	/**
	 * @param templateFacet
	 *            the templateFacet to set
	 */
	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	/**
	 * @param raceFacet
	 *            the raceFacet to set
	 */
	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	/**
	 * @param resolveFacet
	 *            the resolveFacet to set
	 */
	public void setFormulaResolvingFacet(FormulaResolvingFacet resolveFacet)
	{
		this.formulaResolvingFacet = resolveFacet;
	}

	/**
	 * Copies the contents of the LevelFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in LevelFacet in order to avoid exposing the mutable
	 * LevelCacheInfo object to other classes. This should not be inlined, as
	 * the LevelCacheInfo is internal information to LevelFacet and should not
	 * be exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the LevelFacet of one Player
	 * Character will only impact the Player Character where the LevelFacet was
	 * changed).
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
		LevelCacheInfo info = getInfo(source);
		if (info != null)
		{
			LevelCacheInfo copyinfo = getConstructingInfo(copy);
			copyinfo.monsterLevels = info.monsterLevels;
			copyinfo.nonMonsterLevels = info.nonMonsterLevels;
		}
	}
}
