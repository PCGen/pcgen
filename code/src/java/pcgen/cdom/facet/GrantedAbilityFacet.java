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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.cdom.base.Category;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.CategorizedAbilitySelection;
import pcgen.core.Ability;
import pcgen.core.PlayerCharacter;

/**
 * A GrantedAbilityFacet is a DataFacet that contains information about Ability
 * objects that are contained in a Player Character,
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class GrantedAbilityFacet extends AbstractDataFacet<Ability> implements
		DataFacetChangeListener<CategorizedAbilitySelection>
{
	private final PlayerCharacterTrackingFacet pcFacet = FacetLibrary
			.getFacet(PlayerCharacterTrackingFacet.class);

	/**
	 * Add the given Ability to the list of Abilities defined by the given
	 * Category and Nature, which is stored in this GrantedAbilityFacet for the
	 * Player Character represented by the given CharID.
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
	 *            GrantedAbilityFacet for the Player Character represented by
	 *            the given CharID
	 * @param source
	 *            The source for the given object
	 */
	public void add(CharID id, Category<Ability> cat, Nature nat, Ability obj,
			Object source)
	{
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Category in add must not be null");
		}
		if (nat == null)
		{
			throw new IllegalArgumentException("Nature in add must not be null");
		}
		if (obj == null)
		{
			throw new IllegalArgumentException(
					"Ability in add must not be null");
		}
		boolean isNew = ensureCachedSet(id, cat, nat, obj);
		getCachedMap(id, cat, nat).get(obj).add(source);
		if (isNew)
		{
			fireDataFacetChangeEvent(id, obj, DataFacetChangeEvent.DATA_ADDED);
		}
	}

	/**
	 * Adds all of the Abilities in the given Collection to the list of
	 * Abilities defined by the given Category and Nature, which is stored in
	 * this GrantedAbilityFacet for the Player Character represented by the
	 * given CharID
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
	 *            stored in this GrantedAbilityFacet for the Player Character
	 *            represented by the given CharID
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public void addAll(CharID id, Category<Ability> cat, Nature nature,
			Collection<Ability> abilities, Object source)
	{
		if (cat == null)
		{
			throw new IllegalArgumentException(
					"Category in addAll must not be null");
		}
		if (nature == null)
		{
			throw new IllegalArgumentException(
					"Nature in addAll must not be null");
		}
		for (Ability a : abilities)
		{
			add(id, cat, nature, a, source);
		}
	}

	/**
	 * Removes the given Ability from the list of Abilities defined by the given
	 * Category and Nature, which is stored in this GrantedAbilityFacet for the
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
	 *            GrantedAbilityFacet for the Player Character represented by
	 *            the given CharID
	 */
	public boolean remove(CharID id, Category<Ability> cat, Nature nat,
			Ability obj, Object source)
	{
		Map<Ability, List<Object>> cached = getCachedMap(id, cat, nat);
		boolean removed = false;
		if (cached != null)
		{
			List<Object> sourceSet = cached.get(obj);
			if (sourceSet != null && sourceSet.remove(source))
			{
				if (sourceSet.isEmpty())
				{
					removed = true;
					cached.remove(obj);
					cleanup(id, cat, nat);
					fireDataFacetChangeEvent(id, obj,
							DataFacetChangeEvent.DATA_REMOVED);
				}
			}
		}
		return removed;
	}

	private void cleanup(CharID id, Category<Ability> cat, Nature nat)
	{
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			Map<Nature, Map<Ability, List<Object>>> natureMap = catMap.get(cat);
			if (natureMap != null)
			{
				Map<Ability, List<Object>> abilMap = natureMap.get(nat);
				if (abilMap != null && abilMap.isEmpty())
				{
					natureMap.remove(nat);
				}
				if (natureMap.isEmpty())
				{
					catMap.remove(cat);
				}
			}
		}
	}

	/**
	 * Returns a non-null copy of the Set of Abilities in this
	 * GrantedAbilityFacet for the Player Character represented by the given
	 * CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned Set is
	 * transferred to the class calling this method. Modification of the
	 * returned Set will not modify this GrantedAbilityFacet and modification of
	 * this GrantedAbilityFacet will not modify the returned Set. Modifications
	 * to the returned Set will also not modify any future or previous objects
	 * returned by this (or other) methods on GrantedAbilityFacet. If you wish
	 * to modify the information stored in this GrantedAbilityFacet, you must
	 * use the add*() and remove*() methods of GrantedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this GrantedAbilityFacet should be returned.
	 * @param cat
	 *            The Ability Category identifying the list of Abilities to be
	 *            returned
	 * @param nat
	 *            The Ability Nature identifying the list of Abilities to be
	 *            returned
	 * @return A non-null copy of the Set of Abilities in this
	 *         GrantedAbilityFacet for the Player Character represented by the
	 *         given CharID
	 */
	public Set<Ability> get(CharID id, Category<Ability> cat, Nature nat)
	{
		Map<Ability, List<Object>> set = getCachedMap(id, cat, nat);
		if (set == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(set.keySet());
	}

	/**
	 * Returns true if this GrantedAbilityFacet contains the given Ability in
	 * the list of items for the Player Character represented by the given
	 * CharID.
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
	 *            The Ability to test if this GrantedAbilityFacet contains that
	 *            item for the Player Character represented by the given CharID
	 * @return true if this GrantedAbilityFacet contains the given Ability for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise
	 */
	public boolean contains(CharID id, Category<Ability> cat, Nature nat,
			Ability a)
	{
		Map<Ability, List<Object>> set = getCachedMap(id, cat, nat);
		if (set == null)
		{
			return false;
		}
		if (set.containsKey(a))
		{
			return true;
		}
		/*
		 * TODO Have to support slow method due to cloning issues :(
		 */
		for (Ability ab : set.keySet())
		{
			if (ab.equals(a))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the actual Ability in this GrantedAbilityFacet if this
	 * GrantedAbilityFacet contains an Ability matching the Category, Nature,
	 * and Key of the given Ability in the list of items for the Player
	 * Character represented by the given CharID.
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
	 *            The Ability to test if this GrantedAbilityFacet contains that
	 *            item for the Player Character represented by the given CharID
	 * @return the actual Ability in this GrantedAbilityFacet if this
	 *         GrantedAbilityFacet contains an Ability matching the Category,
	 *         Nature, and Key of the given Ability in the list of items for the
	 *         Player Character represented by the given CharID
	 */
	public Ability getContained(CharID id, Category<Ability> cat, Nature nat,
			Ability a)
	{
		Map<Ability, List<Object>> set = getCachedMap(id, cat, nat);
		if (set == null)
		{
			return null;
		}
		if (set.containsKey(a))
		{
			return a;
		}
		/*
		 * TODO Have to support slow method due to cloning issues :(
		 */
		for (Ability ab : set.keySet())
		{
			if (ab.equals(a))
			{
				return ab;
			}
		}
		return null;
	}

	private boolean ensureCachedSet(CharID id, Category<Ability> cat,
			Nature nat, Ability obj)
	{
		boolean isNew = false;
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		Map<Nature, Map<Ability, List<Object>>> natureMap = null;
		Map<Ability, List<Object>> abilityMap = null;
		List<Object> sourceSet = null;
		if (catMap == null)
		{
			catMap = new HashMap<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>>();
			setCache(id, getClass(), catMap);
		}
		else
		{
			natureMap = catMap.get(cat);
		}
		if (natureMap == null)
		{
			natureMap = new HashMap<Nature, Map<Ability, List<Object>>>();
			catMap.put(cat, natureMap);
		}
		else
		{
			abilityMap = natureMap.get(nat);
		}
		if (abilityMap == null)
		{
			abilityMap = new IdentityHashMap<Ability, List<Object>>();
			natureMap.put(nat, abilityMap);
		}
		else
		{
			sourceSet = abilityMap.get(obj);
		}
		if (sourceSet == null)
		{
			isNew = true;
			sourceSet = new ArrayList<Object>();
			abilityMap.put(obj, sourceSet);
		}
		return isNew;
	}

	/**
	 * Returns the type-safe Set for this GrantedAbilityFacet and the given
	 * CharID. May return null if no information has been set in this
	 * GrantedAbilityFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Set is owned by
	 * GrantedAbilityFacet, and since it can be modified, a reference to that
	 * Set should not be exposed to any object other than GrantedAbilityFacet.
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
	 *         null if no information has been set in this GrantedAbilityFacet
	 *         for the Player Character.
	 */
	private Map<Ability, List<Object>> getCachedMap(CharID id,
			Category<Ability> cat, Nature nat)
	{
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		if (catMap == null)
		{
			return null;
		}
		Map<Nature, Map<Ability, List<Object>>> natureMap = catMap.get(cat);
		if (natureMap == null)
		{
			return null;
		}
		return natureMap.get(nat);
	}

	/**
	 * Returns the type-safe Map for this GrantedAbilityFacet and the given
	 * CharID. May return null if no information has been set in this
	 * GrantedAbilityFacet for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * GrantedAbilityFacet, and since it can be modified, a reference to that
	 * Map should not be exposed to any object other than GrantedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID;
	 *         null if no information has been set in this GrantedAbilityFacet
	 *         for the Player Character.
	 */
	private Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> getCachedMap(
			CharID id)
	{
		return (Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>>) getCache(
			id, getClass());
	}

	/**
	 * Removes all of the objects of the given Category and Nature from the list
	 * of Abilities stored in this GrantedAbilityFacet for the Player Character
	 * represented by the given CharID
	 * 
	 * This method is value-semantic in that ownership of the returned Map is
	 * transferred to the class calling this method. Since this is a remove all
	 * function, modification of the returned Map will not modify this
	 * GrantedAbilityFacet and modification of this GrantedAbilityFacet will not
	 * modify the returned Map. Modifications to the returned Map will also not
	 * modify any future or previous objects returned by this (or other) methods
	 * on GrantedAbilityFacet. If you wish to modify the information stored in
	 * this GrantedAbilityFacet, you must use the add*() and remove*() methods
	 * of GrantedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character from which the
	 *            given Abilities should be removed
	 * @param cat
	 *            The Ability Category identifying which Ability objects are to
	 *            be removed from the lists of Abilities stored in this
	 *            GrantedAbilityFacet for the Player Character represented by
	 *            the given CharID
	 * @param nature
	 *            The Ability Nature identifying which Ability objects are to be
	 *            removed from the lists of Abilities stored in this
	 *            GrantedAbilityFacet for the Player Character represented by
	 *            the given CharID
	 * @return
	 * 			  an empty map
	 * @throws NullPointerException
	 *             if the given Collection is null
	 */
	public Map<Ability, List<Object>> removeAll(CharID id,
			Category<Ability> cat, Nature nature)
	{
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			Map<Nature, Map<Ability, List<Object>>> natMap = catMap.get(cat);
			if (natMap != null)
			{
				Map<Ability, List<Object>> abilitySet = natMap.remove(nature);
				if (abilitySet != null)
				{
					processRemoveAbilityMap(id, abilitySet);
					cleanup(id, cat, nature);
					return abilitySet;
				}
			}
		}
		return Collections.emptyMap();
	}

	private void processRemoveAbilityMap(CharID id,
			Map<Ability, List<Object>> abilitySet)
	{
		for (Ability a : abilitySet.keySet())
		{
			fireDataFacetChangeEvent(id, a, DataFacetChangeEvent.DATA_REMOVED);
		}
	}

	/**
	 * Returns a non-null copy of the Set of Ability Category objects in this
	 * GrantedAbilityFacet for the Player Character represented by the given
	 * CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned Set is
	 * transferred to the class calling this method. Modification of the
	 * returned Set will not modify this GrantedAbilityFacet and modification of
	 * this GrantedAbilityFacet will not modify the returned Set. Modifications
	 * to the returned Set will also not modify any future or previous objects
	 * returned by this (or other) methods on GrantedAbilityFacet. If you wish
	 * to modify the information stored in this GrantedAbilityFacet, you must
	 * use the add*() and remove*() methods of GrantedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which the
	 *            items in this GrantedAbilityFacet should be returned.
	 * @return A non-null Set of Ability Category objects in this
	 *         GrantedAbilityFacet for the Player Character represented by the
	 *         given CharID
	 */
	public Set<Category<Ability>> getCategories(CharID id)
	{
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> map = getCachedMap(id);
		if (map == null)
		{
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(map.keySet());
	}

	/**
	 * Copies the contents of the GrantedAbilityFacet from one Player Character
	 * to another Player Character, based on the given CharIDs representing
	 * those Player Characters.
	 * 
	 * This is a method in GrantedAbilityFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to GrantedAbilityFacet and should not be
	 * exposed to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the GrantedAbilityFacet of one
	 * Player Character will only impact the Player Character where the
	 * GrantedAbilityFacet was changed).
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
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(source);
		if (catMap != null)
		{
			for (Map.Entry<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catME : catMap
					.entrySet())
			{
				Category<Ability> cat = catME.getKey();
				for (Map.Entry<Nature, Map<Ability, List<Object>>> natME : catME
						.getValue().entrySet())
				{
					Nature nature = natME.getKey();
					for (Map.Entry<Ability, List<Object>> aME : natME.getValue()
							.entrySet())
					{
						Ability ability = aME.getKey();
						for (Object cdo : aME.getValue())
						{
							add(copy, cat, nature, ability, cdo);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the Ability Nature of the given Ability in the given Ability
	 * Category for the Player Character identified by the given CharID.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Ability Nature will be returned
	 * @param category
	 *            The Ability Category in which the given Ability is present, in
	 *            order to return the Ability Nature of the given Ability
	 * @param ability
	 *            The Ability for which the Ability Nature will be returned
	 * @return The Ability Nature of the given Ability in the given Ability
	 *         Category for the Player Character identified by the given CharID
	 */
	public Nature getNature(CharID id, Category<Ability> category,
			Ability ability)
	{
		Nature n = null;
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			for (Category<Ability> mapKeyCat : catMap.keySet())
			{
				if (mapKeyCat == category
					|| mapKeyCat.getParentCategory() == category)
				{
					Map<Nature, Map<Ability, List<Object>>> natMap = catMap
							.get(mapKeyCat);
					if (natMap != null)
					{
						for (Map.Entry<Nature, Map<Ability, List<Object>>> me : natMap
								.entrySet())
						{
							if (me.getValue().containsKey(ability))
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
	 * Returns a non-null Collection of the Ability Natures in the given Ability
	 * Category for the Player Character identified by the given CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * Collection is transferred to the class calling this method. Modification
	 * of the returned Collection will not modify this GrantedAbilityFacet and
	 * modification of this GrantedAbilityFacet will not modify the returned
	 * Collection. Modifications to the returned Collection will also not modify
	 * any future or previous objects returned by this (or other) methods on
	 * GrantedAbilityFacet. If you wish to modify the information stored in this
	 * GrantedAbilityFacet, you must use the add*() and remove*() methods of
	 * GrantedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            Ability Natures will be returned
	 * @param category
	 *            The Ability Category used to return the Collection of Ability
	 *            Natures
	 * @return The Ability Natures in the given Ability Category for the Player
	 *         Character identified by the given CharID
	 */
	public Collection<Nature> getNatures(CharID id, Category<Ability> category)
	{
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		if (catMap != null)
		{
			Map<Nature, Map<Ability, List<Object>>> natMap = catMap.get(category);
			if (natMap != null)
			{
				return Collections.unmodifiableSet(natMap.keySet());
			}
		}
		return Collections.emptySet();
	}

	/**
	 * Removes all information for the given source from this
	 * GrantedAbilityFacet for the PlayerCharacter represented by the given
	 * CharID.
	 * 
	 * This method is value-semantic in that ownership of the returned Set is
	 * transferred to the class calling this method. Modification of the
	 * returned Set will not modify this GrantedAbilityFacet and modification of
	 * this GrantedAbilityFacet will not modify the returned Set. Modifications
	 * to the returned Set will also not modify any future or previous objects
	 * returned by this (or other) methods on GrantedAbilityFacet. If you wish
	 * to modify the information stored in this GrantedAbilityFacet, you must
	 * use the add*() and remove*() methods of GrantedAbilityFacet.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which items
	 *            from the given source will be removed
	 * @param source
	 *            The source for the objects to be removed from the list of
	 *            items stored for the Player Character identified by the given
	 *            CharID
	 * @return A non-null Set of the Ability objects removed from this
	 *         GrantedAbilityFacet
	 */
	public Set<Ability> removeAll(CharID id, Object source)
	{
		Set<Ability> removed = new HashSet<Ability>();
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		DoubleKeyMapToList<Category<Ability>, Nature, Ability> removeMap = new DoubleKeyMapToList<Category<Ability>, Nature, Ability>();
		if (catMap == null)
		{
			return removed;
		}
		for (Map.Entry<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> me : catMap
				.entrySet())
		{
			Category<Ability> cat = me.getKey();
			Map<Nature, Map<Ability, List<Object>>> natureMap = me.getValue();
			for (Map.Entry<Nature, Map<Ability, List<Object>>> nME : natureMap
					.entrySet())
			{
				Nature nat = nME.getKey();
				Map<Ability, List<Object>> abilMap = nME.getValue();
				for (Map.Entry<Ability, List<Object>> aEntry : abilMap
						.entrySet())
				{
					Ability ab = aEntry.getKey();
					List<Object> sourceSet = aEntry.getValue();
					if (sourceSet.contains(source))
					{
						removeMap.addToListFor(cat, nat, ab);
					}
				}
			}
		}
		for (Category<Ability> cat : removeMap.getKeySet())
		{
			for (Nature nat : removeMap.getSecondaryKeySet(cat))
			{
				for (Ability ab : removeMap.getListFor(cat, nat))
				{
					if (remove(id, cat, nat, ab, source))
					{
						removed.add(ab);
					}
				}
			}
		}
		return removed;
	}

	/**
	 * Returns true if this GrantedAbilityFacet does not contain any items for
	 * the Player Character represented by the given CharID
	 * 
	 * @param id
	 *            The CharId representing the PlayerCharacter to test if any
	 *            items are contained by this GrantedAbilityFacet
	 * @return true if this GrantedAbilityFacet does not contain any items for
	 *         the Player Character represented by the given CharID; false
	 *         otherwise (if it does contain items for the Player Character)
	 */
	public boolean isEmpty(CharID id)
	{
		Map<Category<Ability>, Map<Nature, Map<Ability, List<Object>>>> catMap = getCachedMap(id);
		return catMap == null || catMap.isEmpty();
	}

	/**
	 * Adds the CategorizedAbilitySelection objects in the DataFacetChangeEvent
	 * to the Player Character.
	 * 
	 * Triggered when one of the Facets to which GrantedAbilityFacet listens
	 * fires a DataFacetChangeEvent to indicate a CategorizedAbilitySelection
	 * was added to a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CategorizedAbilitySelection> dfce)
	{
		CharID id = dfce.getCharID();
		CategorizedAbilitySelection cas = dfce.getCDOMObject();
		Ability ability = cas.getAbility();
		add(id, cas.getAbilityCategory(), cas.getNature(), ability, dfce
				.getSource());
		PlayerCharacter pc = pcFacet.getPC(id);
		String selection = cas.getSelection();
		if (selection != null)
		{
			pc.addAssociation(ability, selection);
		}
	}

	/**
	 * Removes the CategorizedAbilitySelection objects in the
	 * DataFacetChangeEvent from the Player Character.
	 * 
	 * Triggered when one of the Facets to which GrantedAbilityFacet listens
	 * fires a DataFacetChangeEvent to indicate a CategorizedAbilitySelection
	 * was removed from a Player Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(
			DataFacetChangeEvent<CategorizedAbilitySelection> dfce)
	{
		CharID id = dfce.getCharID();
		CategorizedAbilitySelection cas = dfce.getCDOMObject();
		PlayerCharacter pc = pcFacet.getPC(id);
		Ability ability = cas.getAbility();
		String selection = cas.getSelection();
		if (selection != null)
		{
			pc.removeAssociation(ability, selection);
		}
		remove(id, cas.getAbilityCategory(), cas.getNature(), ability, dfce
				.getSource());
	}
}