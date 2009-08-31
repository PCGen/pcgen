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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.PCClass;

public class ClassFacet extends AbstractDataFacet<PCClass>
{
	private final Class<?> thisClass = getClass();

	public void add(CharID id, PCClass obj)
	{
		if (getConstructingCachedSet(id).addClass(obj))
		{
			fireGraphNodeChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	public boolean setLevel(CharID id, PCClass obj, PCClassLevel pcl)
			throws CloneNotSupportedException
	{
		ClassInfo info = getClassInfo(id);
		return info != null && info.setClassLevel(obj, pcl);
	}

	public PCClassLevel getLevel(CharID id, PCClass obj, Integer level)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			return null;
		}
		return info.getClassLevel(obj, level);
	}

	public void remove(CharID id, PCClass obj)
	{
		ClassInfo info = getClassInfo(id);
		if (info != null)
		{
			if (info.removeClass(obj))
			{
				fireGraphNodeChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
	}

	public ClassInfo removeAll(CharID id)
	{
		ClassInfo info = (ClassInfo) FacetCache.remove(id, thisClass);
		if (info != null)
		{
			for (PCClass obj : info.getClassSet())
			{
				fireGraphNodeChangeEvent(id, obj,
						DataFacetChangeEvent.DATA_REMOVED);
			}
		}
		return info;
	}

	public void replace(CharID id, PCClass oldClass, PCClass newClass)
	{
		ClassInfo info = getClassInfo(id);
		if (info != null)
		{
			info.replace(oldClass, newClass);
		}
	}

	public Set<PCClass> getSet(CharID id)
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

	private ClassInfo getClassInfo(CharID id)
	{
		return (ClassInfo) FacetCache.get(id, thisClass);
	}

	private ClassInfo getConstructingCachedSet(CharID id)
	{
		ClassInfo info = getClassInfo(id);
		if (info == null)
		{
			info = new ClassInfo();
			FacetCache.set(id, thisClass, info);
		}
		return info;
	}

	private class ClassInfo
	{
		private Map<PCClass, Map<Integer, PCClassLevel>> map = new LinkedHashMap<PCClass, Map<Integer, PCClassLevel>>();

		public ClassInfo()
		{
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
			{
				map.remove(pcc);
			}
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
}
