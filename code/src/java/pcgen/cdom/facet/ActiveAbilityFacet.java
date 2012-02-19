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

/**
 * An ActiveAbilityFacet is a DataFacet that contains information about Ability
 * objects that are contained in a PlayerCharacter
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ActiveAbilityFacet extends AbstractDataFacet<Ability>
{
	/**
	 * Add the given Ability to the list of Abilities defined by the given
	 * Category and Nature, which is stored in this ActiveAbilityFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given Ability should be added
	 * @param cat
	 *            The Ability Category identifying the list to which the given
	 *            Ability should be added
	 * @param nat
	 *            The Ability Nature identifying the list to which the given
	 *            Ability should be added
	 * @param obj
	 *            The Ability to be added to the list of Abilities defined by
	 *            the given Category and Nature, which is stored in this
	 *            ActiveAbilityFacet for the Player Character represented by the
	 *            given CharID
	 */
	public void add(CharID id, Category<Ability> cat, Nature nat, Ability obj)
	{
		boolean isNew = ensureCachedSet(id, cat, nat);
		if (getCachedSet(id, cat, nat).add(obj) || isNew)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED, cat, nat);
		}
	}

	/**
	 * Adds all of the Abilities in the given Collection to the list of
	 * Abilities defined by the given Category and Nature, which is stored in
	 * this ActiveAbilityFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            given Abilities should be added
	 * @param cat
	 *            The Ability Category identifying the list to which the given
	 *            Abilities should be added
	 * @param nature
	 *            The Ability Nature identifying the list to which the given
	 *            Abilities should be added
	 * @param abilities
	 *            The Collection of Abilities to be added to the list of
	 *            Abilities defined by the given Category and Nature, which is
	 *            stored in this ActiveAbilityFacet for the Player Character
	 *            represented by the given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAll(CharID id, Category<Ability> cat, Nature nature,
			Collection<Ability> abilities)
	{
		for (Ability a : abilities)
		{
			add(id, cat, nature, a);
		}
	}

	/**
	 * Removes the given Ability from the list of Abilities defined by the given
	 * Category and Nature, which is stored in this ActiveAbilityFacet for the
	 * Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given Ability should be removed
	 * @param cat
	 *            The Ability Category identifying the list from which the given
	 *            Ability should be removed
	 * @param nat
	 *            The Ability Nature identifying the list from which the given
	 *            Ability should be removed
	 * @param obj
	 *            The Ability to be removed from the list of Abilities defined
	 *            by the given Category and Nature, which is stored in this
	 *            ActiveAbilityFacet for the Player Character represented by the
	 *            given CharID
	 */
	public boolean remove(CharID id, Category<Ability> cat, Nature nat,
			Ability obj)
	{
		Set<Ability> cached = getCachedSet(id, cat, nat);
		boolean removed = cached != null && cached.remove(obj);
		if (removed)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_REMOVED, cat, nat);
		}
		return removed;
	}

	/**
	 * Returns non-null copy of the Set of Abilities in this ActiveAbilityFacet
	 * for the Player Character represented by the given CharID. This method
	 * returns an empty set if no objects are in this ActiveAbilityFacet for the
	 * Player Character identified by the given CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this ActiveAbilityFacet and modification of
	 * this ActiveAbilityFacet will not modify the returned List. If you wish to
	 * modify the information stored in this ActiveAbilityFacet, you must use
	 * the add*() and remove*() methods of ActiveAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this ActiveAbilityFacet should be returned.
	 * @param cat
	 *            The Ability Category identifying the list of Abilities to be
	 *            returned
	 * @param nat
	 *            The Ability Nature identifying the list of Abilities to be
	 *            returned
	 * @return A non-null Set of Abilities in this ActiveAbilityFacet for the
	 *         Player Character represented by the given CharID
	 */
	public Set<Ability> get(CharID id, Category<Ability> cat, Nature nat)
	{
		Set<Ability> set = getCachedSet(id, cat, nat);
		if (set == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set);
	}

	/**
	 * Returns true if this ActiveAbilityFacet contains the given Ability in the
	 * list of items for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param cat
	 *            The Ability Category identifying the list of Abilities to be
	 *            tested to see if it contains the given Ability
	 * @param nat
	 *            The Ability Nature identifying the list of Abilities to be
	 *            tested to see if it contains the given Ability
	 * @param a
	 *            The Ability to test if this ActiveAbilityFacet contains that
	 *            item for the Player Character represented by the given CharID
	 * @return true if this ActiveAbilityFacet contains the given Ability for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise
	 */
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

	/**
	 * Returns true if this ActiveAbilityFacet contains the given Ability in the
	 * list of items for the Player Character represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character used for testing
	 * @param cat
	 *            The Ability Category identifying the list of Abilities to be
	 *            tested to see if it contains the given Ability
	 * @param nat
	 *            The Ability Nature identifying the list of Abilities to be
	 *            tested to see if it contains the given Ability
	 * @param a
	 *            The Ability to test if this ActiveAbilityFacet contains that
	 *            item for the Player Character represented by the given CharID
	 * @return true if this ActiveAbilityFacet contains the given Ability for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise
	 */
	public Ability getContained(CharID id, Category<Ability> cat, Nature nat,
			Ability a)
	{
		Set<Ability> set = getCachedSet(id, cat, nat);
		if (set == null)
		{
			return null;
		}
		if (set.contains(a))
		{
			return a;
		}
		/*
		 * TODO Have to support slow method due to cloning issues :(
		 */
		for (Ability ab : set)
		{
			if (ab.equals(a))
			{
				return ab;
			}
		}
		return null;
	}

	private boolean ensureCachedSet(CharID id, Category<Ability> cat,
			Nature nat)
	{
		boolean isNew = false;
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap == null)
		{
			isNew = true;
			catMap = new HashMap<Category<Ability>, Map<Nature, Set<Ability>>>();
			setCache(id, getClass(), catMap);
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

	/**
	 * Returns the type-safe Set for this ActiveAbilityFacet and the given
	 * CharID. May return null if no information has been set in this
	 * ActiveAbilityFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Set is owned by
	 * ActiveAbilityFacet, and since it can be modified, a reference to that Set
	 * should not be exposed to any object other than ActiveAbilityFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @param cat
	 *            The Ability Category identifying the list of Abilities to be
	 *            returned
	 * @param nat
	 *            The Ability Nature identifying the list of Abilities to be
	 *            returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this ActiveAbilityFacet
	 *         for the Player Character.
	 */
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

	/**
	 * Returns the type-safe Map for this ActiveAbilityFacet and the given
	 * CharID. May return null if no information has been set in this
	 * ActiveAbilityFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * ActiveAbilityFacet, and since it can be modified, a reference to that Map
	 * should not be exposed to any object other than ActiveAbilityFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID;
	 *         null if no information has been set in this ActiveAbilityFacet
	 *         for the Player Character.
	 */
	private Map<Category<Ability>, Map<Nature, Set<Ability>>> getCachedMap(
			CharID id)
	{
		return (Map<Category<Ability>, Map<Nature, Set<Ability>>>) getCache(id,
			getClass());
	}

	/**
	 * Removes all Abilities from the list of Abilities stored in this
	 * ActiveAbilityFacet for the Player Character represented by the given
	 * CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which all
	 *            Abilities should be removed
	 */
	public void removeAll(CharID id)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap =
				(Map<Category<Ability>, Map<Nature, Set<Ability>>>) removeCache(
					id, getClass());
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

	/**
	 * Removes all of the Ability objects in the given Category from the lists
	 * of Abilities stored in this ActiveAbilityFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given Abilities should be removed
	 * @param cat
	 *            The Ability Category identifying which Ability objects are to
	 *            be removed from the lists of Abilities stored in this
	 *            ActiveAbilityFacet for the Player Character represented by the
	 *            given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
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

	/**
	 * Removes all of the objects of the given Category and Nature from the list
	 * of Abilities stored in this ActiveAbilityFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given Abilities should be removed
	 * @param cat
	 *            The Ability Category identifying which Ability objects are to
	 *            be removed from the lists of Abilities stored in this
	 *            ActiveAbilityFacet for the Player Character represented by the
	 *            given CharID
	 * @param nature
	 *            The Ability Nature identifying which Ability objects are to be
	 *            removed from the lists of Abilities stored in this
	 *            ActiveAbilityFacet for the Player Character represented by the
	 *            given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
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

	/**
	 * Removes all of the Ability objects in the given Nature from the lists of
	 * Abilities stored in this ActiveAbilityFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given Abilities should be removed
	 * @param nature
	 *            The Ability Nature identifying which Ability objects are to be
	 *            removed from the lists of Abilities stored in this
	 *            ActiveAbilityFacet for the Player Character represented by the
	 *            given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
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
			fireDataFacetChangeEvent(id, a, DataFacetChangeEvent.DATA_REMOVED);
		}
	}

	/**
	 * Returns a non-null copy of the Set of Ability Category objects in this
	 * ActiveAbilityFacet for the Player Character represented by the given
	 * CharID.
	 * 
	 * This method returns an empty set if no objects are in this
	 * ActiveAbilityFacet for the Player Character identified by the given
	 * CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this ActiveAbilityFacet and modification of
	 * this ActiveAbilityFacet will not modify the returned List. If you wish to
	 * modify the information stored in this ActiveAbilityFacet, you must use
	 * the add*() and remove*() methods of ActiveAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this ActiveAbilityFacet should be returned.
	 * @return A non-null Set of Ability Category objects in this
	 *         ActiveAbilityFacet for the Player Character represented by the
	 *         given CharID
	 */
	public Set<Category<Ability>> getCategories(CharID id)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> map = getCachedMap(id);
		if (map == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * Copies the contents of the ActiveAbilityFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in ActiveAbilityFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to ActiveAbilityFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the ActiveAbilityFacet of one
	 * Player Character will only impact the Player Character where the
	 * ActiveAbilityFacet was changed).
	 * 
	 * @param source
	 *            The CharID representing the Player Character from which the
	 *            information should be copied
	 * @param destination
	 *            The CharID representing the Player Character to which the
	 *            information should be copied
	 */
	@Override
	public void copyContents(CharID id, CharID id2)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap =
				getCachedMap(id);
		if (catMap != null)
		{
			for (Map.Entry<Category<Ability>, Map<Nature, Set<Ability>>> catME : catMap
				.entrySet())
			{
				Category<Ability> cat = catME.getKey();
				for (Map.Entry<Nature, Set<Ability>> natME : catME.getValue()
					.entrySet())
				{
					Nature nat = natME.getKey();
					ensureCachedSet(id2, cat, nat);
					getCachedSet(id2, cat, nat).addAll(natME.getValue());
				}
			}
		}
	}

	/**
	 * Returns the Ability Nature for the Ability within the given Ability
	 * Category and Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Ability Nature will be returned
	 * @param category
	 *            The Ability Category identifying the Ability for which the
	 *            Ability Nature will be returned
	 * @param ability
	 *            The Ability in the list for which the Ability Nature will be
	 *            returned
	 * @return The Ability Nature for the Ability within the given Ability
	 *         Category and Player Character identified by the given CharID.
	 */
	public Nature getNature(CharID id, Category<Ability> category,
			Ability ability)
	{
		Nature n = null;
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			for (Category<Ability> mapKeyCat : catMap.keySet())
			{
				if (mapKeyCat == category
					|| mapKeyCat.getParentCategory() == category)
				{
					Map<Nature, Set<Ability>> natMap = catMap.get(mapKeyCat);
					if (natMap != null)
					{
						for (Map.Entry<Nature, Set<Ability>> me : natMap.entrySet())
						{
							if (me.getValue().contains(ability))
							{
								n = Nature.getBestNature(me.getKey(), n);
							}
						}
					}
				}
			}
		}
		return n;
	}

	/**
	 * Returns the Natures for which Abilities exist within this
	 * ActiveAbilityFacet for the given Ability Category and Player Character
	 * identified by the given CharID.
	 * 
	 * This method returns an empty set if no Abilities are in this
	 * ActiveAbilityFacet for given Ability Category and the Player Character
	 * identified by the given CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this ActiveAbilityFacet and modification of
	 * this ActiveAbilityFacet will not modify the returned List. If you wish to
	 * modify the information stored in this ActiveAbilityFacet, you must use
	 * the add*() and remove*() methods of ActiveAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            Ability Natures will be returned
	 * @param cat
	 *            The Ability Category identifying the Abilities for which the
	 *            Ability Natures will be returned
	 * @return A Collection of the Natures for which Abilities exist within this
	 *         ActiveAbilityFacet for the given Ability Category and Player
	 *         Character identified by the given CharID.
	 */
	public Collection<Nature> getNatures(CharID id, Category<Ability> cat)
	{
		Map<Category<Ability>, Map<Nature, Set<Ability>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			Map<Nature, Set<Ability>> natMap = catMap.get(cat);
			if (natMap != null)
			{
				return Collections.unmodifiableSet(natMap.keySet());
			}
		}
		return Collections.emptySet();
	}

}