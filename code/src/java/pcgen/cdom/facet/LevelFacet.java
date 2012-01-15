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

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import pcgen.base.formula.Formula;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.facet.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.core.PCTemplate;
import pcgen.core.Race;

public class LevelFacet implements ClassLevelChangeListener
{
	private TemplateFacet templateFacet;
	private RaceFacet raceFacet;
	private FormulaResolvingFacet resolveFacet;
	private final Class<?> thisClass = getClass();
	private final LevelChangeSupport support = new LevelChangeSupport();

	public int getNonMonsterLevelCount(CharID id)
	{
		LevelCacheInfo info = getInfo(id);
		return info == null ? 0 : info.nonMonsterLevels;
	}

	public int getMonsterLevelCount(CharID id)
	{
		LevelCacheInfo info = getInfo(id);
		return info == null ? 0 : info.monsterLevels;
	}

	public int getLevelAdjustment(CharID id)
	{
		Race race = raceFacet.get(id);
		int levelAdj = 0;

		if (race != null)
		{
			Formula raceLA = race.getSafe(FormulaKey.LEVEL_ADJUSTMENT);
			levelAdj += resolveFacet.resolve(id, raceLA, "").intValue();
		}

		for (PCTemplate template : templateFacet.getSet(id))
		{
			Formula templateLA = template.getSafe(FormulaKey.LEVEL_ADJUSTMENT);
			levelAdj += resolveFacet.resolve(id, templateLA, "").intValue();
		}

		return levelAdj;
	}

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

	public int getTotalLevels(CharID id)
	{
		LevelCacheInfo info = getInfo(id);
		// Monster hit dice count towards total levels
		// sage_sam changed 03 Dec 2002 for Bug #646816
		return info == null ? 0 : info.nonMonsterLevels + info.monsterLevels;
	}

	private LevelCacheInfo getConstructingInfo(CharID id)
	{
		LevelCacheInfo lci = getInfo(id);
		if (lci == null)
		{
			lci = new LevelCacheInfo();
			FacetCache.set(id, thisClass, lci);
		}
		return lci;
	}

	private LevelCacheInfo getInfo(CharID id)
	{
		return (LevelCacheInfo) FacetCache.get(id, thisClass);
	}

	private static class LevelCacheInfo
	{
		public int monsterLevels;
		public int nonMonsterLevels;
	}

	public void levelChanged(ClassLevelChangeEvent lce)
	{
		CharID id = lce.getCharID();
		LevelCacheInfo lci = getConstructingInfo(id);
		int levelChange = lce.getNewLevel() - lce.getOldLevel();
		if (levelChange != 0)
		{
			if (lce.getPCClass().isMonster())
			{
				lci.monsterLevels = lci.monsterLevels + levelChange;
			}
			else
			{
				lci.nonMonsterLevels = lci.nonMonsterLevels + levelChange;
			}
			support.fireLevelChangeEvent(id);
		}
	}

	public void levelObjectChanged(ClassLevelObjectChangeEvent lce)
	{
		//ignore
	}

	public void addLevelChangeListener(LevelChangeListener listener)
	{
		support.addLevelChangeListener(listener);
	}

	public LevelChangeListener[] getLevelChangeListeners()
	{
		return support.getLevelChangeListeners();
	}

	public void removeLevelChangeListener(LevelChangeListener listener)
	{
		support.removeLevelChangeListener(listener);
	}

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
		 * Adds a new DataFacetChangeListener to receive LevelChangeEvents
		 * (EdgeChangeEvent and NodeChangeEvent) from the source DataFacet.
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
		 * No reference to the Array is maintained by DataFacetChangeSupport.
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
		 * Sends a NodeChangeEvent to the LevelChangeListeners that are
		 * receiving LevelChangeEvents from the source DataFacet.
		 * 
		 * @param node
		 *            The Node that has beed added to or removed from the source
		 *            DataFacet
		 * @param type
		 *            An identifier indicating whether the given CDOMObject was
		 *            added to or removed from the source DataFacet
		 */
		protected void fireLevelChangeEvent(CharID id)
		{
			LevelChangeListener[] listeners = listenerList
					.getListeners(LevelChangeListener.class);
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
	 * @param templateFacet the templateFacet to set
	 */
	public void setTemplateFacet(TemplateFacet templateFacet)
	{
		this.templateFacet = templateFacet;
	}

	/**
	 * @param raceFacet the raceFacet to set
	 */
	public void setRaceFacet(RaceFacet raceFacet)
	{
		this.raceFacet = raceFacet;
	}

	/**
	 * @param resolveFacet the resolveFacet to set
	 */
	public void setResolveFacet(FormulaResolvingFacet resolveFacet)
	{
		this.resolveFacet = resolveFacet;
	}
}
