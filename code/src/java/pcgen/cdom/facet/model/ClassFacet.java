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
package pcgen.cdom.facet.model;

import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.swing.event.EventListenerList;

import pcgen.cdom.base.SetFacet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.base.AbstractDataFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;
import pcgen.output.publish.OutputDB;

/**
 * ClassFacet is a Facet that tracks the PCClass objects possessed by a Player
 * Character.
 * 
 */
public class ClassFacet extends AbstractDataFacet<CharID, PCClass> implements SetFacet<CharID, PCClass>
{
	private final ClassLevelChangeSupport support = new ClassLevelChangeSupport();

	/**
	 * Add the given PCClass to the list of PCClass objects stored in this
	 * ClassFacet for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given PCClass should be added
	 * @param obj
	 *            The PCClass to be added to the list of PCClass objects stored
	 *            in this AbstractListFacet for the Player Character represented
	 *            by the given CharID
	 */
	public void addClass(CharID id, PCClass obj)
	{
		Objects.requireNonNull(obj, "PCClass to add may not be null");
		if (getConstructingClassInfo(id).addClass(obj))
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Sets the PCClassLevel object associated with the given PCClass for the Player
	 * Character represented by the given CharID. Returns true if the set is successful.
	 * The set will be successful if the given PCClass is possessed by the given
	 * PlayerCharacter; false otherwise.
	 * 
	 * The (numeric) class level for which the given PCClassLevel should be applied is
	 * determined by the level value set in the PCClassLevel.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the given
	 *            PCClassLevel should be set
	 * @param pcc
	 *            The PCClass object for which the PCClassLevel object is set as the
	 *            PCClass
	 * @param pcl
	 *            The PCClassLevel object to be associated with the given PCClass and
	 *            Player Character represented by the given CharID
	 * @return true if the set is successful; false otherwise.
	 * @throws CloneNotSupportedException
	 *             if the class level cannot be thrown
	 */
	public boolean setClassLevel(CharID id, PCClass pcc, PCClassLevel pcl) throws CloneNotSupportedException
	{
		Objects.requireNonNull(pcc, "Class cannot be null in setClassLevel");
		Objects.requireNonNull(pcl, "Class Level cannot be null in setClassLevel");
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return false;
		}
		PCClassLevel old = info.getClassLevel(pcc, pcl.get(IntegerKey.LEVEL));
		boolean returnVal = info.setClassLevel(pcc, pcl);
		support.fireClassLevelObjectChangeEvent(id, pcc, old, pcl);
		return returnVal;
	}

	/**
	 * Returns the PCClassLevel object associated with the Player Character
	 * represented by the given CharID, the given PCClass, and the given
	 * (numeric) class level.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            associated PCClassLevel will be returned
	 * @param obj
	 *            The PCClass object for which the PCClassLevel object should be
	 *            returned
	 * @param level
	 *            The (numeric) class level for which the PCClassLevel object
	 *            should be returned
	 * @return The PCClassLevel object associated with the Player Character
	 *         represented by the given CharID, the given PCClass, and the given
	 *         (numeric) class level.
	 */
	public PCClassLevel getClassLevel(CharID id, PCClass obj, int level)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return null;
		}
		return info.getClassLevel(obj, level);
	}

	/**
	 * Remove the given PCClass from the list of PCClass objects stored in this
	 * ClassFacet for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given PCClass should be removed
	 * @param obj
	 *            The PCClass to be removed from the list of PCClass objects
	 *            stored in this AbstractListFacet for the Player Character
	 *            represented by the given CharID
	 */
	public void removeClass(CharID id, PCClass obj)
	{
		Objects.requireNonNull(obj, "PCClass to add may not be null");
		ClassInfo info = getClassInfo(id);
		if (info != null)
		{
			if (info.containsClass(obj))
			{
				setLevel(id, obj, 0);
				info.removeClass(obj);
				fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
			}
			if (info.isEmpty())
			{
				removeCache(id);
			}
		}
	}

	/**
	 * Removes all PCClass objects from the list of PCClass objects stored in
	 * this ClassFacet for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            PCClass objects should be removed
	 */
	public ClassInfo removeAllClasses(CharID id)
	{
		ClassInfo info = (ClassInfo) removeCache(id);
		if (info != null)
		{
			for (PCClass obj : info.getClassSet())
			{
				fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
				int oldLevel = info.getLevel(obj);
				support.fireClassLevelChangeEvent(id, obj, oldLevel, 0);
			}
		}
		return info;
	}

	/**
	 * Replaces the given old PCClass stored in this ClassFacet with the given
	 * new PCClass for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given old PCClass should be replaced
	 * @param oldClass
	 *            The old PCClass to be removed from the list of PCClass objects
	 *            stored in this ClassFacet for the Player Character represented
	 *            by the given CharID
	 * @param newClass
	 *            The new PCClass to replace the old PCClass stored in this
	 *            ClassFacet for the Player Character represented by the given
	 *            CharID
	 */
	public void replaceClass(CharID id, PCClass oldClass, PCClass newClass)
	{
		ClassInfo info = getClassInfo(id);
		if (info != null)
		{
			info.replace(oldClass, newClass);
		}
	}

	/**
	 * Returns a non-null copy of the Set of PCClass objects in this ClassFacet
	 * for the Player Character represented by the given CharID. This method
	 * returns an empty Set if no objects are in this ClassFacet for the Player
	 * Character identified by the given CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this ClassFacet and modification of this
	 * ClassFacet will not modify the returned List. Modifications to the
	 * returned List will also not modify any future or previous objects
	 * returned by this (or other) methods on ClassFacet. If you wish to modify
	 * the information stored in this ClassFacet, you must use the add*() and
	 * remove*() methods of ClassFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this ClassFacet should be returned.
	 * @return A non-null Set of PCClass objects in this ClassFacet for the
	 *         Player Character represented by the given CharID
	 */
	@Override
	public Set<PCClass> getSet(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return Collections.emptySet();
		}
		return info.getClassSet();
	}

	/**
	 * Returns the count of PCClass objects in this ClassFacet for the Player
	 * Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            count of PCClass objects should be returned
	 * @return The count of PCClass objects in this ClassFacet for the Player
	 *         Character represented by the given CharID
	 */
	@Override
	public int getCount(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return 0;
		}
		return info.classCount();
	}

	/**
	 * Returns true if this ClassFacet does not contain any PCClass objects for
	 * the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            PCClass objects are contained by this AbstractListFacet
	 * @return true if this ClassFacet does not contain any PCClass objects for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise (if it does contain items for the Player Character)
	 */
	public boolean isEmpty(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		return info == null || info.isEmpty();
	}

	/**
	 * Returns true if this ClassFacet contains the given PCClass in the list of
	 * PCClass objects for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param obj
	 *            The PCClass to test if this ClassFacet contains that PCClass
	 *            for the Player Character represented by the given CharID
	 * @return true if this AbstractListFacet contains the given PCClass for the
	 *         Player Character represented by the given CharID; false otherwise
	 */
	public boolean contains(CharID id, PCClass obj)
	{
		ClassInfo info = getClassInfo(id);
		return info != null && info.containsClass(obj);
	}

	/**
	 * Sets the level for the given PCClass and the Player Character identified
	 * by the given CharID to the given value.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which a level
	 *            value is being set
	 * @param pcc
	 *            The PCClass identifying which class level is being set
	 * @param level
	 *            The level of the PCClass for the Player Character identified
	 *            by the given CharID
	 */
	public void setLevel(CharID id, PCClass pcc, int level)
	{
		int oldLevel = getConstructingClassInfo(id).setLevel(pcc, level);
		support.fireClassLevelChangeEvent(id, pcc, oldLevel, level);
	}

	/**
	 * Returns the current (numerical) level for the given PCClass in the Player
	 * Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            level of the given PCClass should be returned
	 * @param pcc
	 *            The PCClass for which the level of the Player Character should
	 *            be returned
	 * @return The numeric value of the level for the given PCClass in the
	 *         Player Character identified by the given CharID
	 */
	public int getLevel(CharID id, PCClass pcc)
	{
		ClassInfo info = getClassInfo(id);
		return (info == null) ? 0 : info.getLevel(pcc);
	}

	/**
	 * Returns the ClassInfo for this ClassFacet and the given CharID. May
	 * return null if no information has been set in this ClassFacet for the
	 * given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The ClassInfo is owned by
	 * ClassFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than ClassFacet.
	 * 
	 * @param id
	 *            The CharID for which the ClassInfo should be returned
	 * @return The ClassInfo for the Player Character represented by the given
	 *         CharID; null if no information has been set in this ClassFacet
	 *         for the Player Character.
	 */
	private ClassInfo getClassInfo(CharID id)
	{
		return (ClassInfo) getCache(id);
	}

	/**
	 * Returns a ClassInfo for this ClassFacet and the given CharID. Will return
	 * a new, empty ClassInfo if no information has been set in this ClassFacet
	 * for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The ClassInfo object is owned
	 * by ClassFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than ClassFacet.
	 * 
	 * @param id
	 *            The CharID for which the ClassInfo should be returned
	 * @return The ClassInfo for the Player Character represented by the given
	 *         CharID.
	 */
	private ClassInfo getConstructingClassInfo(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			info = new ClassInfo();
			setCache(id, info);
		}
		return info;
	}

	/**
	 * ClassInfo is the Class used by ClassFacet to store information in the
	 * global character cache. This stores both the PCClassLevel objects active
	 * for a PCClass and Player Character, as well as the levels of the
	 * PCClasses for a Player Character.
	 * 
	 */
	public static class ClassInfo
	{
		/**
		 * Map that stores the PCClassLevel objects active for a Player
		 * Character.
		 */
		private Map<PCClass, Map<Integer, PCClassLevel>> map = new LinkedHashMap<>();
		/**
		 * Map that stores the numeric level values for the PCClasses in a
		 * Player Character.
		 */
		private Map<PCClass, Integer> levelmap = new HashMap<>();

		public ClassInfo()
		{
			//Default constructor for an empty ClassInfo
		}

		public ClassInfo(ClassInfo info)
		{
			for (Map.Entry<PCClass, Map<Integer, PCClassLevel>> me : info.map.entrySet())
			{
				map.put(me.getKey(), new HashMap<>(me.getValue()));
			}
			levelmap.putAll(info.levelmap);
		}

		public Integer setLevel(PCClass pcc, int level)
		{
			Objects.requireNonNull(pcc, "Class for setLevel must not be null");
			if (level < 0)
			{
				throw new IllegalArgumentException("Level for " + pcc.getDisplayName() + " must be > 0");
			}
			if (level != 0 && !map.containsKey(pcc))
			{
				throw new IllegalArgumentException(
					"Cannot set level for PCClass " + pcc.getKeyName() + " which is not added");
			}
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
			map = new LinkedHashMap<>();
			for (Map.Entry<PCClass, Map<Integer, PCClassLevel>> me : oldMap.entrySet())
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

		public boolean addClass(PCClass pcc)
		{
			if (map.containsKey(pcc))
			{
				return false;
			}
			Map<Integer, PCClassLevel> levelMap = new HashMap<>();
			map.put(pcc, levelMap);
			/*
			 * DO NOT initialize levelMap here - see CODE-208
			 */
			return true;
		}

		public boolean setClassLevel(PCClass pcc, PCClassLevel pcl) throws CloneNotSupportedException
		{
			Map<Integer, PCClassLevel> localMap = map.get(pcc);
			if (localMap == null)
			{
				return false;
			}
			pcl.ownBonuses(pcc);
			pcl.put(ObjectKey.PARENT, pcc);
			localMap.put(pcl.get(IntegerKey.LEVEL), pcl);
			return true;
		}

		public PCClassLevel getClassLevel(PCClass pcc, int level)
		{
			Objects.requireNonNull(pcc, "Class in getClassLevel cannot be null");
			if (level < 0)
			{
				throw new IllegalArgumentException("Level cannot be negative in getClassLevel");
			}
			//map.get(pcc) doesn't seem to find the monster class
			//Map<Integer, PCClassLevel> localMap = map.get(pcc);
			Map<Integer, PCClassLevel> localMap = null;
			for (final Map.Entry<PCClass, Map<Integer, PCClassLevel>> pcClassMapEntry : map.entrySet())
			{
				if (pcc.equals(pcClassMapEntry.getKey()))
				{
					localMap = pcClassMapEntry.getValue();
					break;
				}
			}
			if (localMap == null)
			{
				throw new IllegalArgumentException(
					"Level cannot be returned for Class " + pcc.getKeyName() + " which is not in the PC");
			}
			PCClassLevel classLevel = localMap.get(level);
			if (classLevel == null)
			{
				classLevel = pcc.getOriginalClassLevel(level);
				classLevel.put(ObjectKey.PARENT, pcc);
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

		@Override
		public int hashCode()
		{
			return map.hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof ClassInfo)
			{
				ClassInfo other = (ClassInfo) o;
				return map.equals(other.map) && levelmap.equals(other.levelmap);
			}
			return false;
		}
	}

	@Override
	public void copyContents(CharID source, CharID destination)
	{
		ClassInfo info = getClassInfo(source);
		if (info != null)
		{
			setCache(destination, new ClassInfo(info));
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

	public interface ClassLevelChangeListener extends EventListener
	{
		void levelChanged(ClassLevelChangeEvent lce);

		void levelObjectChanged(ClassLevelObjectChangeEvent lce);
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

		public ClassLevelChangeEvent(CharID source, PCClass pcc, int oldLevel, int newLevel)
		{
			super(source);
			Objects.requireNonNull(source, "CharID cannot be null");
			Objects.requireNonNull(pcc, "PCClass cannot be null");
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

	public static class ClassLevelObjectChangeEvent extends EventObject
	{

		/**
		 * The ID indicating the owning character for this ClassLevelChangeEvent
		 */
		private final CharID charID;

		private final PCClass pcClass;
		private final PCClassLevel oldLvl;
		private final PCClassLevel newLvl;

		public ClassLevelObjectChangeEvent(CharID source, PCClass pcc, PCClassLevel oldLevel, PCClassLevel newLevel)
		{
			super(source);
			Objects.requireNonNull(source, "CharID cannot be null");
			Objects.requireNonNull(pcc, "PCClass cannot be null");
			Objects.requireNonNull(newLevel, "New Level cannot be null");
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

		public PCClassLevel getOldLevel()
		{
			return oldLvl;
		}

		public PCClassLevel getNewLevel()
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
		 * @param id
		 *            The CharID that has beed added to or removed from the source
		 *            ClassFacet
		 * @param pcc
		 *			  The PCClass to be added to the list of PCClass objects stored
		 *            in this AbstractListFacet for the Player Character represented
		 *            by the given CharID
		 * @param oldLevel
		 * 			  The chracter's previous level
		 * 
		 * @param newLevel
		 * 			  The new level specified by the user.
		 */
		protected void fireClassLevelChangeEvent(CharID id, PCClass pcc, int oldLevel, int newLevel)
		{
			if (oldLevel == newLevel)
			{
				// Nothing to do
				return;
			}
			ClassLevelChangeListener[] listeners = listenerList.getListeners(ClassLevelChangeListener.class);
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
					ccEvent = new ClassLevelChangeEvent(id, pcc, oldLevel, newLevel);
				}
				listeners[i].levelChanged(ccEvent);
			}
		}

		public void fireClassLevelObjectChangeEvent(CharID id, PCClass pcc, PCClassLevel oldLevel,
			PCClassLevel newLevel)
		{
			if (oldLevel == newLevel)
			{
				// Nothing to do
				return;
			}
			ClassLevelChangeListener[] listeners = listenerList.getListeners(ClassLevelChangeListener.class);
			/*
			 * This list is decremented from the end of the list to the
			 * beginning in order to maintain consistent operation with how Java
			 * AWT and Swing listeners are notified of Events (they are in
			 * reverse order to how they were added to the Event-owning object).
			 */
			ClassLevelObjectChangeEvent ccEvent = null;
			for (int i = listeners.length - 1; i >= 0; i--)
			{
				// Lazily create event
				if (ccEvent == null)
				{
					ccEvent = new ClassLevelObjectChangeEvent(id, pcc, oldLevel, newLevel);
				}
				listeners[i].levelObjectChanged(ccEvent);
			}
		}

	}

	public void init()
	{
		OutputDB.register("classes", this);
	}
}
