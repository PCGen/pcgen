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

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.enumeration.CharID;
import pcgen.core.pclevelinfo.PCLevelInfo;

public class LevelInfoFacet
{

	public void add(CharID id, PCLevelInfo pcl)
	{
		if (pcl == null)
		{
			throw new IllegalArgumentException("Object to add may not be null");
		}
		Map<Integer, PCLevelInfo> sbMap = getConstructingCachedMap(id);
		int level = pcl.getClassLevel();
		sbMap.put(level, pcl);
	}

	public void removeAll(CharID id)
	{
		FacetCache.remove(id, getClass());
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
	private Map<Integer, PCLevelInfo> getCachedMap(CharID id)
	{
		return (Map<Integer, PCLevelInfo>) FacetCache.get(id, getClass());
	}

	/**
	 * Returns a type-safe Map for this AbstractSourcedListFacet and the given
	 * CharID. Will return a new, empty Map if no information has been set in
	 * this AbstractSourcedListFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * AbstractSourcedListFacet, and since it can be modified, a reference to
	 * that object should not be exposed to any object other than
	 * AbstractSourcedListFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<Integer, PCLevelInfo> getConstructingCachedMap(CharID id)
	{
		Map<Integer, PCLevelInfo> componentMap = getCachedMap(id);
		if (componentMap == null)
		{
			componentMap = new HashMap<Integer, PCLevelInfo>();
			FacetCache.set(id, getClass(), componentMap);
		}
		return componentMap;
	}

	public PCLevelInfo getLevel(CharID id, int idx)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int getCount(CharID id)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
