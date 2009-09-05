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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Nature;
import pcgen.core.Ability;

public class CategorizedAbilityFacet extends AbstractDataFacet<Ability>
{

	public void add(CharID id, Category<Ability> cat, Nature nat, Ability obj)
	{
		boolean isNew = ensureCachedSet(id, cat, nat, obj);
		if (getCachedSet(id, cat, nat).add(obj) || isNew)
		{
			fireGraphNodeChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	public void addAll(CharID id, Category<Ability> cat, Nature nature,
			Collection<Ability> abilities)
	{
		for (Ability a : abilities)
		{
			add(id, cat, nature, a);
		}
	}

	public void remove(CharID id, Category<Ability> cat, Nature nat, Ability obj)
	{
		Set<Ability> cached = getCachedSet(id, cat, nat);
		if (cached != null && cached.remove(obj))
		{
			fireGraphNodeChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED);
		}
	}

	public Set<Ability> get(CharID id, Category<Ability> cat, Nature nat)
	{
		Set<Ability> set = getCachedSet(id, cat, nat);
		if (set == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set);
	}

	public boolean contains(CharID id, Category<Ability> cat, Nature nat,
			Ability a)
	{
		Set<Ability> set = getCachedSet(id, cat, nat);
		if (set == null)
		{
			return false;
		}
		if (set.contains(a))
		{
			return true;
		}
		/*
		 * TODO Have to support slow method due to cloning issues :(
		 */
		for (Ability ab : set)
		{
			if (ab.equals(a))
			{
				return true;
			}
		}
		return false;
	}

	private boolean ensureCachedSet(CharID id, Category<Ability> cat,
			Nature nat, Ability obj)
	{
		boolean isNew = false;
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap == null)
		{
			isNew = true;
			catMap = new HashMap<Category<Ability>, Map<Nature, Set<Ability>>>();
			FacetCache.set(id, getClass(), catMap);
		}
		Map<Nature, Set<Ability>> natureMap = catMap.get(cat);
		if (natureMap == null)
		{
			isNew = true;
			natureMap = new HashMap<Nature, Set<Ability>>();
			catMap.put(cat, natureMap);
		}
		Set<Ability> abilitySet = natureMap.get(nat);
		if (abilitySet == null)
		{
			isNew = true;
			// abilitySet = new HashSet<Ability>();
			abilitySet = new WrappedMapSet<Ability>(IdentityHashMap.class);
			natureMap.put(nat, abilitySet);
		}
		return isNew;
	}

	private Set<Ability> getCachedSet(CharID id, Category<Ability> cat,
			Nature nat)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap == null)
		{
			return null;
		}
		Map<Nature, Set<Ability>> natureMap = catMap.get(cat);
		if (natureMap == null)
		{
			return null;
		}
		return natureMap.get(nat);
	}

	private Map<Category<Ability>, Map<Nature, Set<Ability>>> getCachedMap(
			CharID id)
	{
		return (Map<Category<Ability>, Map<Nature, Set<Ability>>>) FacetCache
				.get(id, getClass());
	}

	public void removeAll(CharID id)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = (Map<Category<Ability>, Map<Nature, Set<Ability>>>) FacetCache
				.remove(id, getClass());
		if (catMap != null)
		{
			for (Map.Entry<Category<Ability>, Map<Nature, Set<Ability>>> catME : catMap
					.entrySet())
			{
				// Category<Ability> cat = catME.getKey();
				Map<Nature, Set<Ability>> natMap = catME.getValue();
				processRemoveNatureMap(id, natMap);
			}
		}
	}

	public void removeAll(CharID id, Category<Ability> cat)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			Map<Nature, Set<Ability>> natMap = catMap.remove(cat);
			if (natMap != null)
			{
				processRemoveNatureMap(id, natMap);
			}
		}
	}

	public void removeAll(CharID id, Category<Ability> cat, Nature nature)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			Map<Nature, Set<Ability>> natMap = catMap.remove(cat);
			if (natMap != null)
			{
				Set<Ability> abilitySet = natMap.get(nature);
				if (abilitySet != null)
				{
					processRemoveAbilityMap(id, abilitySet);
				}
			}
		}
	}

	private void processRemoveNatureMap(CharID id,
			Map<Nature, Set<Ability>> natMap)
	{
		for (Map.Entry<Nature, Set<Ability>> natME : natMap.entrySet())
		{
			// Nature nature = natME.getKey();
			processRemoveAbilityMap(id, natME.getValue());
		}
	}

	public void removeAll(CharID id, Nature nature)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			for (Map.Entry<Category<Ability>, Map<Nature, Set<Ability>>> catME : catMap
					.entrySet())
			{
				// Category<Ability> cat = catME.getKey();
				Set<Ability> abilitySet = catME.getValue().remove(nature);
				if (abilitySet != null)
				{
					processRemoveAbilityMap(id, abilitySet);
				}
			}
		}
	}

	private void processRemoveAbilityMap(CharID id, Set<Ability> abilitySet)
	{
		for (Ability a : abilitySet)
		{
			fireGraphNodeChangeEvent(id, a, DataFacetChangeEvent.DATA_REMOVED);
		}
	}

	public Set<Category<Ability>> getCategories(CharID id)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> map = getCachedMap(id);
		if (map == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(map.keySet());
	}

	public String dump(CharID id)
	{
		return (getCachedMap(id) == null) ? "" : getCachedMap(id).toString();
	}

}
