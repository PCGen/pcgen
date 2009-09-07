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

import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.event.EventListenerList;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;

public class ClassFacet extends AbstractDataFacet<PCClass>
{
	private final Class<?> thisClass = getClass();

	private final ClassLevelChangeSupport support = new ClassLevelChangeSupport();

	public void addClass(CharID id, PCClass obj)
	{
		if (getConstructingClassInfo(id).addClass(obj))
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	public boolean setClassLevel(CharID id, PCClass obj, PCClassLevel pcl)
			throws CloneNotSupportedException
	{
		ClassInfo info = getClassInfo(id);
		return info != null && info.setClassLevel(obj, pcl);
	}

	public PCClassLevel getClassLevel(CharID id, PCClass obj, Integer level)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return null;
		}
		return info.getClassLevel(obj, level);
	}

	public void removeClass(CharID id, PCClass obj)
	{
		ClassInfo info = getClassInfo(id);
		if (info != null)
		{
			if (info.removeClass(obj))
			{
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
				setLevel(id, obj, 0);
			}
		}
	}

	public ClassInfo removeAllClasses(CharID id)
	{
		ClassInfo info = (ClassInfo) FacetCache.remove(id, thisClass);
		if (info != null)
		{
			for (PCClass obj : info.getClassSet())
			{
				fireDataFacetChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
				int oldLevel = info.getLevel(obj);
				support.fireClassLevelChangeEvent(id, obj, oldLevel, 0);
			}
		}
		return info;
	}

	public void replaceClass(CharID id, PCClass oldClass, PCClass newClass)
	{
		ClassInfo info = getClassInfo(id);
		if (info != null)
		{
			info.replace(oldClass, newClass);
		}
	}

	public Set<PCClass> getClassSet(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return Collections.emptySet();
		}
		return info.getClassSet();
	}

	public int getCount(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return 0;
		}
		return info.classCount();
	}

	public boolean isEmpty(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		return info == null || info.isEmpty();
	}

	public boolean contains(CharID id, PCClass obj)
	{
		ClassInfo info = getClassInfo(id);
		return info != null && info.containsClass(obj);
	}

	public void setLevel(CharID id, PCClass pcc, int level)
	{
		int oldLevel = getConstructingClassInfo(id).setLevel(pcc, level);
		support.fireClassLevelChangeEvent(id, pcc, oldLevel, level);
	}

	public int getLevel(CharID id, PCClass pcc)
	{
		ClassInfo info = getClassInfo(id);
		return (info == null) ? 0 : info.getLevel(pcc);
	}

	private ClassInfo getClassInfo(CharID id)
	{
		return (ClassInfo) FacetCache.get(id, thisClass);
	}

	private ClassInfo getConstructingClassInfo(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			info = new ClassInfo();
			FacetCache.set(id, thisClass, info);
		}
		return info;
	}

	public class ClassInfo
	{
		private Map<PCClass, Map<Integer, PCClassLevel>> map = new LinkedHashMap<PCClass, Map<Integer, PCClassLevel>>();
		private Map<PCClass, Integer> levelmap = new HashMap<PCClass, Integer>();

		public ClassInfo()
		{
		}

		public Integer setLevel(PCClass pcc, int level)
		{
			Integer oldlvl = levelmap.put(pcc, level);
			return (oldlvl == null) ? 0 : oldlvl;
		}

		public int getLevel(PCClass pcc)
		{
			Integer level = levelmap.get(pcc);
			return (level == null) ? 0 : level;
		}

		public void replace(PCClass oldClass, PCClass newClass)
		{
			Map<PCClass, Map<Integer, PCClassLevel>> oldMap = map;
			map = new LinkedHashMap<PCClass, Map<Integer, PCClassLevel>>();
			for (Map.Entry<PCClass, Map<Integer, PCClassLevel>> me : oldMap
					.entrySet())
			{
				PCClass currentClass = me.getKey();
				if (oldClass.equals(currentClass))
				{
					addClass(newClass);
				}
				else
				{
					map.put(currentClass, me.getValue());
				}
			}
		}

		public ClassInfo(ClassInfo info)
		{
			for (Map.Entry<PCClass, Map<Integer, PCClassLevel>> me : info.map
					.entrySet())
			{
				map.put(me.getKey(), new HashMap<Integer, PCClassLevel>(me
						.getValue()));
			}
			levelmap.putAll(info.levelmap);
		}

		public boolean addClass(PCClass pcc)
		{
			if (map.containsKey(pcc))
			{
				return false;
			}
			HashMap<Integer, PCClassLevel> levelMap = new HashMap<Integer, PCClassLevel>();
			map.put(pcc, levelMap);
			for (PCClassLevel pcl : pcc.getOriginalClassLevelCollection())
			{
				levelMap.put(pcl.get(IntegerKey.LEVEL), pcl);
			}
			return true;
		}

		public boolean setClassLevel(PCClass pcc, PCClassLevel pcl)
				throws CloneNotSupportedException
		{
			Map<Integer, PCClassLevel> localMap = map.get(pcc);
			if (localMap == null)
			{
				return false;
			}
			pcl.ownBonuses(pcc);
			localMap.put(pcl.get(IntegerKey.LEVEL), pcl);
			return true;
		}

		public PCClassLevel getClassLevel(PCClass pcc, Integer level)
		{
			Map<Integer, PCClassLevel> localMap = map.get(pcc);
			if (localMap == null)
			{
				return null;
			}
			PCClassLevel classLevel = localMap.get(level);
			if (classLevel == null)
			{
				classLevel = pcc.getOriginalClassLevel(level);
				localMap.put(level, classLevel);
			}
			return classLevel;
		}

		public boolean removeClass(PCClass pcc)
		{
			boolean returnValue = map.containsKey(pcc);
			map.remove(pcc);
			return returnValue;
		}

		public PCClassLevel get(PCClass pcc, Integer level)
		{
			Map<Integer, PCClassLevel> localMap = map.get(pcc);
			if (localMap == null)
			{
				return null;
			}
			return localMap.get(level);
		}

		public Set<PCClass> getClassSet()
		{
			return Collections.unmodifiableSet(map.keySet());
		}

		public boolean isEmpty()
		{
			return map.isEmpty();
		}

		public int classCount()
		{
			return map.size();
		}

		public boolean containsClass(PCClass pcc)
		{
			return map.containsKey(pcc);
		}
	}

	public void copyContents(CharID source, CharID destination)
	{
		ClassInfo info = getClassInfo(source);
		if (info != null)
		{
			FacetCache.set(destination, thisClass, new ClassInfo(info));
		}
	}

	public void addLevelChangeListener(ClassLevelChangeListener listener)
	{
		support.addLevelChangeListener(listener);
	}

	public ClassLevelChangeListener[] getLevelChangeListeners()
	{
		return support.getLevelChangeListeners();
	}

	public void removeLevelChangeListener(ClassLevelChangeListener listener)
	{
		support.removeLevelChangeListener(listener);
	}

	public static interface ClassLevelChangeListener extends EventListener
	{
		public void levelChanged(ClassLevelChangeEvent lce);
	}

	public static class ClassLevelChangeEvent extends EventObject
	{

		/**
		 * The ID indicating the owning character for this ClassLevelChangeEvent
		 */
		private final CharID charID;

		private final PCClass pcClass;
		private final int oldLvl;
		private final int newLvl;

		public ClassLevelChangeEvent(CharID source, PCClass pcc, int oldLevel,
				int newLevel)
		{
			super(source);
			if (source == null)
			{
				throw new IllegalArgumentException ("CharID cannot be null");
			}
			if (pcc == null)
			{
				throw new IllegalArgumentException ("PCClass cannot be null");
			}
			charID = source;
			pcClass = pcc;
			oldLvl = oldLevel;
			newLvl = newLevel;
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

		public PCClass getPCClass()
		{
			return pcClass;
		}

		public int getOldLevel()
		{
			return oldLvl;
		}

		public int getNewLevel()
		{
			return newLvl;
		}
	}

	public static class ClassLevelChangeSupport
	{
		/**
		 * The listeners to which LevelChangeEvents will be fired when a change
		 * in the source ClassFacet occurs.
		 */
		private final EventListenerList listenerList = new EventListenerList();

		/**
		 * Adds a new ClassLevelChangeListener to receive LevelChangeEvents
		 * (EdgeChangeEvent and NodeChangeEvent) from the source ClassFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to receive LevelChangeEvents
		 */
		public void addLevelChangeListener(ClassLevelChangeListener listener)
		{
			listenerList.add(ClassLevelChangeListener.class, listener);
		}

		/**
		 * Returns an Array of LevelChangeListeners receiving LevelChangeEvents
		 * from the source ClassFacet.
		 * 
		 * Ownership of the returned Array is transferred to the calling Object.
		 * No reference to the Array is maintained by ClassLevelChangeSupport.
		 * However, the LevelChangeListeners contained in the Array are
		 * (obviously!) returned BY REFERENCE, and care should be taken with
		 * modifying those LevelChangeListeners.*
		 * 
		 * @return An Array of LevelChangeListeners receiving LevelChangeEvents
		 *         from the source ClassFacet
		 */
		public synchronized ClassLevelChangeListener[] getLevelChangeListeners()
		{
			return listenerList.getListeners(ClassLevelChangeListener.class);
		}

		/**
		 * Removes a LevelChangeListener so that it will no longer receive
		 * LevelChangeEvents from the source ClassFacet.
		 * 
		 * @param listener
		 *            The LevelChangeListener to be removed
		 */
		public void removeLevelChangeListener(ClassLevelChangeListener listener)
		{
			listenerList.remove(ClassLevelChangeListener.class, listener);
		}

		/**
		 * Sends a NodeChangeEvent to the LevelChangeListeners that are
		 * receiving LevelChangeEvents from the source ClassFacet.
		 * 
		 * @param node
		 *            The Node that has beed added to or removed from the source
		 *            ClassFacet
		 * @param type
		 *            An identifier indicating whether the given CDOMObject was
		 *            added to or removed from the source ClassFacet
		 */
		protected void fireClassLevelChangeEvent(CharID id, PCClass pcc,
				int oldLevel, int newLevel)
		{
			ClassLevelChangeListener[] listeners = listenerList
					.getListeners(ClassLevelChangeListener.class);
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 */
			ClassLevelChangeEvent ccEvent = null;
			for (int i = listeners.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					ccEvent = new ClassLevelChangeEvent(id, pcc, oldLevel,
							newLevel);
				}
				listeners[i].levelChanged(ccEvent);
			}
		}
	}
}
