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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.facet.base.AbstractStorageFacet;
import pcgen.cdom.facet.event.DataFacetChangeEvent;
import pcgen.cdom.facet.event.DataFacetChangeListener;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.core.PCClass;
import pcgen.core.SpellProhibitor;

/**
 * SpellProhibitorFacet is a Facet to track SpellProhibitor costs for each
 * PCClass in a Player Character
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class SpellProhibitorFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<PCClass>
{
	private ClassFacet classFacet;

	/**
	 * Adds the SpellProhibitor objects granted by PCClasses added to the Player
	 * Character to this SpellProhibitorFacet.
	 * 
	 * Triggered when one of the Facets to which SpellProhibitorFacet listens
	 * fires a DataFacetChangeEvent to indicate a PCClass was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.event.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.event.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<PCClass> dfce)
	{
		PCClass pcc = dfce.getCDOMObject();
		CharID id = dfce.getCharID();
		Object source = dfce.getSource();
		for (SpellProhibitor prohibit : pcc
			.getSafeListFor(ListKey.PROHIBITED_SPELLS))
		{
			add(id, pcc, prohibit, source);
		}

		for (SpellProhibitor prohibit : pcc
			.getSafeListFor(ListKey.SPELL_PROHIBITOR))
		{
			add(id, pcc, prohibit, source);
		}
	}

	@Override
	public void dataRemoved(DataFacetChangeEvent<PCClass> dfce)
	{
		CacheInfo ci = getInfo(dfce.getCharID());
		if (ci != null)
		{
			ci.removeAll(dfce.getCDOMObject());
		}
	}

	private CacheInfo getConstructingInfo(CharID id)
	{
		CacheInfo rci = getInfo(id);
		if (rci == null)
		{
			rci = new CacheInfo();
			setCache(id, rci);
		}
		return rci;
	}

	private CacheInfo getInfo(CharID id)
	{
		return (CacheInfo) getCache(id);
	}

	/**
	 * CacheInfo is the data structure used by SpellProhibitorFacet to store a
	 * Player Character's SpellProhibitor Info
	 */
	private static class CacheInfo
	{
		Map<PCClass, Map<SpellProhibitor, Set<Object>>> map =
				new HashMap<PCClass, Map<SpellProhibitor, Set<Object>>>();

		public void add(PCClass pcc, SpellProhibitor sp, Object source)
		{
			Map<SpellProhibitor, Set<Object>> spMap = map.get(pcc);
			if (spMap == null)
			{
				spMap = new IdentityHashMap<SpellProhibitor, Set<Object>>();
				map.put(pcc, spMap);
			}
			Set<Object> set = spMap.get(sp);
			if (set == null)
			{
				set = new WrappedMapSet<Object>(IdentityHashMap.class);
				spMap.put(sp, set);
			}
			set.add(source);
		}

		public void removeAll(Object source)
		{
			for (Iterator<Map<SpellProhibitor, Set<Object>>> mit =
					map.values().iterator(); mit.hasNext();)
			{
				Map<SpellProhibitor, Set<Object>> spMap = mit.next();
				for (Iterator<Set<Object>> sit = spMap.values().iterator(); sit
					.hasNext();)
				{
					Set<Object> set = sit.next();
					if (set.remove(source) && set.isEmpty())
					{
						sit.remove();
					}
				}
				if (spMap.isEmpty())
				{
					mit.remove();
				}
			}
		}

		public boolean contains(PCClass pcc, SpellProhibitor sp)
		{
			Map<SpellProhibitor, Set<Object>> spMap = map.get(pcc);
			return (spMap != null) && spMap.containsKey(sp);
		}

		public Collection<SpellProhibitor> getSet(PCClass pcc)
		{
			Map<SpellProhibitor, Set<Object>> skMap = map.get(pcc);
			if (skMap == null)
			{
				return Collections.emptyList();
			}
			return new ArrayList<SpellProhibitor>(skMap.keySet());
		}
	}

	private void add(CharID id, PCClass pcc, SpellProhibitor sp, Object source)
	{
		getConstructingInfo(id).add(pcc, sp, source);
	}

	public Collection<SpellProhibitor> getSet(CharID id, PCClass pcc)
	{
		CacheInfo ci = getInfo(id);
		if (ci == null)
		{
			return Collections.emptyList();
		}
		return ci.getSet(pcc);
	}

	public boolean contains(CharID id, PCClass pcc, SpellProhibitor sp)
	{
		CacheInfo ci = getInfo(id);
		return ci != null && ci.contains(pcc, sp);
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	/**
	 * Initializes the connections for SpellProhibitorFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the SpellProhibitorFacet.
	 */
	public void init()
	{
		classFacet.addDataFacetChangeListener(this);
	}

	/**
	 * Copies the contents of the SpellProhibitorFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in SpellProhibitorFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to SpellProhibitorFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the SpellProhibitorFacet of one
	 * Player Character will only impact the Player Character where the
	 * SpellProhibitorFacet was changed).
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
		CacheInfo rci = getInfo(source);
		if (rci != null)
		{
			CacheInfo copyci = getConstructingInfo(copy);
			for (Map.Entry<PCClass, Map<SpellProhibitor, Set<Object>>> clme : rci.map
				.entrySet())
			{
				PCClass pcc = clme.getKey();
				for (Map.Entry<SpellProhibitor, Set<Object>> spme : clme
					.getValue().entrySet())
				{
					SpellProhibitor sp = spme.getKey();
					for (Object spsource : spme.getValue())
					{
						copyci.add(pcc, sp, spsource);
					}
				}
			}
		}
	}
}
