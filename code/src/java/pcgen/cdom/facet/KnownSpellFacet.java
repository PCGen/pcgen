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
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSpellStorageFacet;
import pcgen.cdom.facet.base.AbstractSpellStorageFacet.SpellChangeListener;
import pcgen.core.spell.Spell;

/**
 * KnownSpellFacet is a Facet that tracks the Available Spells (and target
 * objects) that are contained in a Player Character. These are post-resolution
 * of spells for which the PC is qualified.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class KnownSpellFacet extends AbstractSpellStorageFacet implements
		SpellChangeListener
{

	public int getKnownSpellCountForLevel(CharID id, CDOMList<Spell> spellList,
		int spellLevel)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> map =
				getCachedMap(id);
		if (map == null)
		{
			return 0;
		}
		Map<Integer, Map<Spell, Set<Object>>> levelMap = map.get(spellList);
		if (levelMap == null)
		{
			return 0;
		}
		Map<Spell, Set<Object>> spellMap = levelMap.get(spellLevel);
		if (spellMap == null)
		{
			return 0;
		}
		return spellMap.size();
	}

	public Map<Integer, Collection<Spell>> getKnownSpells(CharID id,
		CDOMList<Spell> csl)
	{
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap == null)
		{
			return Collections.emptyMap();
		}
		Map<Integer, Map<Spell, Set<Object>>> spellMap = listMap.get(csl);
		if (spellMap == null)
		{
			return Collections.emptyMap();
		}
		HashMap<Integer, Collection<Spell>> retMap =
				new HashMap<Integer, Collection<Spell>>();
		for (Map.Entry<Integer, Map<Spell, Set<Object>>> entry : spellMap
			.entrySet())
		{
			retMap.put(entry.getKey(), new ArrayList<Spell>(entry.getValue()
				.keySet()));
		}
		return retMap;
	}

	@Override
	public void spellAdded(SpellChangeEvent sce)
	{
		add(sce.getCharID(), sce.getSpellList(), sce.getLevel(),
			sce.getSpell(), sce.getSource());
	}

	@Override
	public void spellRemoved(SpellChangeEvent sce)
	{
		remove(sce.getCharID(), sce.getSpellList(), sce.getLevel(),
			sce.getSpell(), sce.getSource());
	}
}
