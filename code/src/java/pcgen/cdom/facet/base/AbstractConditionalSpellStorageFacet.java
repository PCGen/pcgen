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
package pcgen.cdom.facet.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.core.spell.Spell;

public abstract class AbstractConditionalSpellStorageFacet extends
		AbstractSpellStorageFacet
{

	public void update(CharID id)
	{
		Set<CDOMList<Spell>> allLists = new HashSet<CDOMList<Spell>>();
		allLists.addAll(getConditionalFacet().getSpellLists(id));
		allLists.addAll(getSpellLists(id));
		for (CDOMList<Spell> list : allLists)
		{
			processUpdate(id, list);
		}
	}

	private void processUpdate(CharID id, CDOMList<Spell> list)
	{
		Map<Integer, List<Spell>> newSpells =
				getConditionalFacet().getSpells(id, list);
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> map =
				getCachedMap(id);
		Map<Integer, Map<Spell, Set<Object>>> currentSpells = null;
		if (map != null)
		{
			currentSpells = map.get(list);
		}
		if (currentSpells == null)
		{
			currentSpells = Collections.emptyMap();
		}
		Set<Integer> allLevels = new HashSet<Integer>();
		allLevels.addAll(newSpells.keySet());
		allLevels.addAll(currentSpells.keySet());
		for (Integer level : allLevels)
		{
			processUpdate(id, list, level, currentSpells.get(level),
				newSpells.get(level));
		}
	}

	private void processUpdate(CharID id, CDOMList<Spell> list, Integer level,
		Map<Spell, Set<Object>> currentSpells, List<Spell> newSpells)
	{
		AbstractConditionalSpellFacet conditionalFacet = getConditionalFacet();
		if (currentSpells != null)
		{
			List<Spell> toRemove = new ArrayList<Spell>(currentSpells.keySet());
			if (newSpells != null)
			{
				toRemove.removeAll(newSpells);
			}
			removeAll(id, list, level, toRemove, conditionalFacet);
		}
		if (newSpells != null)
		{
			List<Spell> toAdd = new ArrayList<Spell>(newSpells);
			if (currentSpells != null)
			{
				toAdd.removeAll(currentSpells.keySet());
			}
			addAll(id, list, level, toAdd, conditionalFacet);
		}
	}

	protected abstract AbstractConditionalSpellFacet getConditionalFacet();

}
