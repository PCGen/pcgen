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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.base.util.WrappedMapSet;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.character.CharacterSpell;
import pcgen.core.spell.Spell;

/**
 * KnownSpellFacet is a Facet that tracks the Known Spells (and target objects)
 * that are contained in a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class KnownSpellFacet extends AbstractStorageFacet implements
		DataFacetChangeListener<CDOMObject>
{

	private static final Class<Spell> SPELL_CLASS = Spell.class;

	private PrerequisiteFacet prerequisiteFacet;

	private CDOMObjectConsolidationFacet consolidationFacet;

	/**
	 * Triggered when one of the Facets to which KnownSpellFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was added to a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataAdded(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataAdded(DataFacetChangeEvent<CDOMObject> dfce)
	{
		CDOMObject cdo = dfce.getCDOMObject();
		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> listrefs =
				cdo.getModifiedLists();
		CharID id = dfce.getCharID();
		for (CDOMReference<? extends CDOMList<? extends PrereqObject>> ref : listrefs)
		{
			processListRef(id, cdo, ref);
		}
	}

	private void processListRef(CharID id, CDOMObject cdo,
		CDOMReference<? extends CDOMList<? extends PrereqObject>> listref)
	{
		for (CDOMList<? extends PrereqObject> list : listref
			.getContainedObjects())
		{
			if (!list.getListClass().equals(SPELL_CLASS))
			{
				continue;
			}
			CDOMList<Spell> spelllist = (CDOMList<Spell>) list;
			for (CDOMReference<Spell> objref : cdo
				.getListMods((CDOMReference<? extends CDOMList<Spell>>) listref))
			{
				for (AssociatedPrereqObject apo : cdo.getListAssociations(
					listref, objref))
				{
					Boolean known = apo.getAssociation(AssociationKey.KNOWN);
					if (known != null && known)
					{
						Collection<Spell> spells = objref.getContainedObjects();
						addAll(id, spelllist, spells, apo, cdo);
					}
				}
			}
		}
	}

	/**
	 * Triggered when one of the Facets to which KnownSpellFacet listens fires a
	 * DataFacetChangeEvent to indicate a CDOMObject was removed from a Player
	 * Character.
	 * 
	 * @param dfce
	 *            The DataFacetChangeEvent containing the information about the
	 *            change
	 * 
	 * @see pcgen.cdom.facet.DataFacetChangeListener#dataRemoved(pcgen.cdom.facet.DataFacetChangeEvent)
	 */
	@Override
	public void dataRemoved(DataFacetChangeEvent<CDOMObject> dfce)
	{
		removeAll(dfce.getCharID(), dfce.getCDOMObject());
	}

	private void addAll(CharID id, CDOMList<Spell> list,
		Collection<Spell> spells, AssociatedPrereqObject apo, CDOMObject cdo)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> map =
				getConstructingCachedMap(id);
		Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> subMap =
				map.get(list);
		boolean fireNew = (subMap == null);
		if (fireNew)
		{
			subMap =
					new HashMap<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>();
			map.put(list, subMap);
		}
		for (Spell spell : spells)
		{
			Map<AssociatedPrereqObject, Set<CDOMObject>> assocMap =
					subMap.get(spell);
			if (assocMap == null)
			{
				assocMap =
						new HashMap<AssociatedPrereqObject, Set<CDOMObject>>();
				subMap.put(spell, assocMap);
			}
			Set<CDOMObject> sources = assocMap.get(apo);
			if (sources == null)
			{
				sources = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
				assocMap.put(apo, sources);
			}
			sources.add(cdo);
		}
	}

	private void add(CharID id, CDOMList<Spell> list, Spell spell,
		AssociatedPrereqObject apo, CDOMObject cdo)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> map =
				getConstructingCachedMap(id);
		Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> subMap =
				map.get(list);
		boolean fireNew = (subMap == null);
		if (fireNew)
		{
			subMap =
					new HashMap<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>();
			map.put(list, subMap);
		}
		Map<AssociatedPrereqObject, Set<CDOMObject>> assocMap =
				subMap.get(spell);
		if (assocMap == null)
		{
			assocMap = new HashMap<AssociatedPrereqObject, Set<CDOMObject>>();
			subMap.put(spell, assocMap);
		}
		Set<CDOMObject> sources = assocMap.get(apo);
		if (sources == null)
		{
			sources = new WrappedMapSet<CDOMObject>(IdentityHashMap.class);
			assocMap.put(apo, sources);
		}
		sources.add(cdo);
	}

	/**
	 * Returns the type-safe Map for this KnownSpellFacet and the given CharID.
	 * May return null if no information has been set in this KnownSpellFacet
	 * for the given CharID.
	 * 
	 * Note that this method SHOULD NOT be public. The Map is owned by
	 * KnownSpellFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than KnownSpellFacet.
	 * 
	 * @param id
	 *            The CharID for which the Set should be returned
	 * @return The Set for the Player Character represented by the given CharID;
	 *         null if no information has been set in this KnownSpellFacet for
	 *         the Player Character.
	 */
	private Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> getCachedMap(
		CharID id)
	{
		return (Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>>) getCache(
			id, getClass());
	}

	/**
	 * Returns a type-safe Map for this KnownSpellFacet and the given CharID.
	 * Will return a new, empty Map if no information has been set in this
	 * KnownSpellFacet for the given CharID. Will not return null.
	 * 
	 * Note that this method SHOULD NOT be public. The Map object is owned by
	 * KnownSpellFacet, and since it can be modified, a reference to that object
	 * should not be exposed to any object other than KnownSpellFacet.
	 * 
	 * @param id
	 *            The CharID for which the Map should be returned
	 * @return The Map for the Player Character represented by the given CharID.
	 */
	private Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> getConstructingCachedMap(
		CharID id)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> componentMap =
				getCachedMap(id);
		if (componentMap == null)
		{
			componentMap =
					new HashMap<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>>();
			setCache(id, getClass(), componentMap);
		}
		return componentMap;
	}

	/**
	 * Removes all information for the given source from this KnownSpellFacet
	 * for the PlayerCharacter represented by the given CharID.
	 * 
	 * @param id
	 *            The CharID representing the Player Character for which items
	 *            from the given source will be removed
	 * @param source
	 *            The source for the objects to be removed from the list of
	 *            items stored for the Player Character identified by the given
	 *            CharID
	 */
	public void removeAll(CharID id, Object source)
	{
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> listMap =
				getCachedMap(id);
		if (listMap != null)
		{
			for (Iterator<Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> mit =
					listMap.values().iterator(); mit.hasNext();)
			{
				Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> objMap =
						mit.next();
				for (Iterator<Map<AssociatedPrereqObject, Set<CDOMObject>>> ait =
						objMap.values().iterator(); ait.hasNext();)
				{
					Map<AssociatedPrereqObject, Set<CDOMObject>> apoMap =
							ait.next();
					for (Iterator<Set<CDOMObject>> sit =
							apoMap.values().iterator(); sit.hasNext();)
					{
						Set<CDOMObject> set = sit.next();
						if (set.remove(source) && set.isEmpty())
						{
							sit.remove();
						}
					}
					if (apoMap.isEmpty())
					{
						ait.remove();
					}
				}
				if (objMap.isEmpty())
				{
					mit.remove();
				}
			}
		}
	}

	/**
	 * Returns a non-null Map of Spells (by spell level) known by the Player
	 * Character for the given ClassSpellList.
	 * 
	 * This method is value-semantic in that ownership of the returned Map is
	 * transferred to the class calling this method. Modification of the
	 * returned Map will not modify this KnownSpellFacet and modification of
	 * this KnownSpellFacet will not modify the returned Map. Modifications to
	 * the returned Map will also not modify any future or previous objects
	 * returned by this (or other) methods on KnownSpellFacet. If you wish to
	 * modify the information stored in this KnownSpellFacet, you must use the
	 * add*() and remove*() methods of KnownSpellFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the List
	 *            of known Spells will be returned
	 * @param csl
	 *            The ClassSpellList for which the List of known Spells will be
	 *            returned
	 * @return A non-null Map of Spells known (by level) by the Player Character
	 *         for the given ClassSpellList
	 * 
	 */
	public Map<Integer, List<Spell>> getKnownSpells(CharID id,
		ClassSpellList csl)
	{
		HashMap<Integer, List<Spell>> levelInfo =
				new HashMap<Integer, List<Spell>>();
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> listMap =
				getCachedMap(id);
		if (listMap == null)
		{
			//If this 
			return levelInfo;
		}
		Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> spellMap =
				listMap.get(csl);
		if (spellMap == null)
		{
			return levelInfo;
		}
		for (Map.Entry<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> me : spellMap
			.entrySet())
		{
			Spell spell = me.getKey();
			Map<AssociatedPrereqObject, Set<CDOMObject>> assocMap =
					me.getValue();

			for (Map.Entry<AssociatedPrereqObject, Set<CDOMObject>> ame : assocMap
				.entrySet())
			{
				AssociatedPrereqObject apo = ame.getKey();
				Set<CDOMObject> sources = ame.getValue();
				boolean passes = false;
				for (CDOMObject source : sources)
				{
					if (prerequisiteFacet.qualifies(id, apo, source))
					{
						passes = true;
						break;
					}
				}
				if (passes)
				{
					Integer lvl =
							apo.getAssociation(AssociationKey.SPELL_LEVEL);
					List<Spell> spellList = levelInfo.get(lvl);
					if (spellList == null)
					{
						spellList = new ArrayList<Spell>();
						levelInfo.put(lvl, spellList);
					}
					spellList.add(spell);
				}
			}
		}
		return levelInfo;
	}

	/**
	 * Returns a non-null List of Spells known by the Player Character for the
	 * given ClassSpellList and spell level.
	 * 
	 * This method is value-semantic in that ownership of the returned List is
	 * transferred to the class calling this method. Modification of the
	 * returned List will not modify this KnownSpellFacet and modification of
	 * this KnownSpellFacet will not modify the returned List. Modifications to
	 * the returned List will also not modify any future or previous objects
	 * returned by this (or other) methods on KnownSpellFacet. If you wish to
	 * modify the information stored in this KnownSpellFacet, you must use the
	 * add*() and remove*() methods of KnownSpellFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the List
	 *            of known Spells will be returned
	 * @param csl
	 *            The ClassSpellList for which the List of known Spells will be
	 *            returned
	 * @param spellLevel
	 *            The Spell level for which the List of known Spells will be
	 *            returned
	 * @return A non-null List of Spells known by the Player Character for the
	 *         given ClassSpellList and spell level
	 * 
	 */
	public List<Spell> getKnownSpellsForLevel(CharID id, ClassSpellList csl,
		int spellLevel)
	{
		List<Spell> spellList = new ArrayList<Spell>();
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> listMap =
				getCachedMap(id);
		if (listMap == null)
		{
			//You shouldn't call this on a non-spellcaster, but for now, handle that scenario
			return spellList;
		}
		Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> spellMap =
				listMap.get(csl);
		if (spellMap == null)
		{
			//Possible to be called on a ClassSpellList that doesn't have any known spells
			return spellList;
		}
		for (Map.Entry<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> me : spellMap
			.entrySet())
		{
			Spell spell = me.getKey();
			Map<AssociatedPrereqObject, Set<CDOMObject>> assocMap =
					me.getValue();

			for (Map.Entry<AssociatedPrereqObject, Set<CDOMObject>> ame : assocMap
				.entrySet())
			{
				AssociatedPrereqObject apo = ame.getKey();
				Integer lvl = apo.getAssociation(AssociationKey.SPELL_LEVEL);
				if (spellLevel != lvl.intValue())
				{
					continue;
				}
				Set<CDOMObject> sources = ame.getValue();
				boolean passes = false;
				for (CDOMObject source : sources)
				{
					if (prerequisiteFacet.qualifies(id, apo, source))
					{
						passes = true;
						break;
					}
				}
				if (passes)
				{
					spellList.add(spell);
				}
			}
		}
		return spellList;
	}

	/**
	 * Add to the supplied list the additional known spells for the class that
	 * are specified by SPELLKNOWN tags associated with the character. Any
	 * existing contents of the list are preserved.
	 * 
	 * @param id
	 *            The character being tested
	 * @param aClass
	 *            The PC class being checked
	 * @param cSpells
	 *            The list to be populated with the spells
	 */
	public void addBonusKnownSpellsToList(CharID id, CDOMObject aClass,
		List<CharacterSpell> cSpells)
	{
		if (!(aClass instanceof PCClass))
		{
			return;
		}
		ClassSpellList classSpellList =
				((PCClass) aClass).get(ObjectKey.CLASS_SPELLLIST);
		Map<Integer, List<Spell>> spellsMap =
				getKnownSpells(id, classSpellList);
		for (Integer spellLevel : spellsMap.keySet())
		{
			List<Spell> spells = spellsMap.get(spellLevel);
			for (Spell spell : spells)
			{
				CharacterSpell acs = new CharacterSpell(aClass, spell);
				acs.addInfo(spellLevel, 1, Globals.getDefaultSpellBook());
				cSpells.add(acs);
			}
		}
	}

	public void setPrerequisiteFacet(PrerequisiteFacet prerequisiteFacet)
	{
		this.prerequisiteFacet = prerequisiteFacet;
	}

	public void setConsolidationFacet(CDOMObjectConsolidationFacet consolidationFacet)
	{
		this.consolidationFacet = consolidationFacet;
	}

	/**
	 * Initializes the connections for KnwonSpellFacet to other facets.
	 * 
	 * This method is automatically called by the Spring framework during
	 * initialization of the KnwonSpellFacet.
	 */
	public void init()
	{
		consolidationFacet.addDataFacetChangeListener(this);
	}

	/**
	 * Copies the contents of the KnwonSpellFacet from one Player Character to
	 * another Player Character, based on the given CharIDs representing those
	 * Player Characters.
	 * 
	 * This is a method in KnwonSpellFacet in order to avoid exposing the
	 * mutable Map object to other classes. This should not be inlined, as the
	 * Map is internal information to KnwonSpellFacet and should not be exposed
	 * to other classes.
	 * 
	 * Note also the copy is a one-time event and no references are maintained
	 * between the Player Characters represented by the given CharIDs (meaning
	 * once this copy takes place, any change to the KnwonSpellFacet of one
	 * Player Character will only impact the Player Character where the
	 * KnwonSpellFacet was changed).
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
		Map<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> map =
				getCachedMap(source);
		if (map != null)
		{
			for (Map.Entry<CDOMList<Spell>, Map<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>>> me : map
				.entrySet())
			{
				CDOMList<Spell> list = me.getKey();
				for (Map.Entry<Spell, Map<AssociatedPrereqObject, Set<CDOMObject>>> fme : me
					.getValue().entrySet())
				{
					Spell s = fme.getKey();
					for (Map.Entry<AssociatedPrereqObject, Set<CDOMObject>> apme : fme
						.getValue().entrySet())
					{
						AssociatedPrereqObject apo = apme.getKey();
						for (CDOMObject cdo : apme.getValue())
						{
							add(copy, list, s, apo, cdo);
						}
					}
				}
			}
		}
	}
}
