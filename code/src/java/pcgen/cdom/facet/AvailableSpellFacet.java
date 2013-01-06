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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import pcgen.base.util.DoubleKeyMapToList;
import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.base.AbstractSpellStorageFacet;
import pcgen.cdom.facet.base.AbstractSpellStorageFacet.SpellChangeListener;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.spell.Spell;

/**
 * AvailableSpellFacet is a Facet that tracks the Available Spells (and target
 * objects) that are contained in a Player Character.
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class AvailableSpellFacet extends AbstractSpellStorageFacet implements
		SpellChangeListener
{

	/**
	 * Returns a non-null HashMapToList indicating the spell levels and sources
	 * of those spell levels available to a Player Character for a given Spell.
	 * 
	 * This may return multiple spell levels because it is possible for a spell
	 * to be accessible to a Player Character at multiple levels since it may be
	 * available from multiple sources. This also returns the spell lists
	 * associated with the given level, since it is possible for a multi-class
	 * character to have access to the same spell at different levels. By
	 * returning the source as well as the spell levels, such scenarios can be
	 * appropriately distinguished.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * HashMapToList is transferred to the class calling this method.
	 * Modification of the returned HashMapToList will not modify this
	 * AvailableSpellFacet and modification of this AvailableSpellFacet will not
	 * modify the returned HashMapToList. Modifications to the returned
	 * HashMapToList will also not modify any future or previous objects
	 * returned by this (or other) methods on AvailableSpellFacet. If you wish
	 * to modify the information stored in this AvailableSpellFacet, you must
	 * use the add*() and remove*() methods of AvailableSpellFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            spell levels should be returned
	 * @param sp
	 *            The Spell for which the spell levels should be returned
	 * @return A non-null HashMapToList indicating the spell levels and sources
	 *         of those spell levels available to a Player Character for a given
	 *         Spell.
	 */
	public HashMapToList<CDOMList<Spell>, Integer> getSpellLevelInfo(
		CharID id, Spell sp)
	{
		HashMapToList<CDOMList<Spell>, Integer> levelInfo =
				new HashMapToList<CDOMList<Spell>, Integer>();
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap == null)
		{
			return levelInfo;
		}
		for (Entry<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> me : listMap
			.entrySet())
		{
			CDOMList<Spell> list = me.getKey();
			//Check to ensure we don't use SPELLS:
			if (!(list instanceof ClassSpellList)
				&& !(list instanceof DomainSpellList))
			{
				continue;
			}
			Map<Integer, Map<Spell, Set<Object>>> levelMap = me.getValue();
			for (Map.Entry<Integer, Map<Spell, Set<Object>>> lme : levelMap
				.entrySet())
			{
				Integer level = lme.getKey();
				Map<Spell, Set<Object>> spellMap = lme.getValue();
				if (spellMap.containsKey(sp))
				{
					levelInfo.addToListFor(list, level);
				}
			}
		}
		return levelInfo;
	}

	/**
	 * Returns a non-null DoubleKeyMapToList indicating the spells, spell levels
	 * and sources of those spell levels available to a Player Character for a
	 * given Spell.
	 * 
	 * This may return multiple spell levels because it is possible for a spell
	 * to be accessible to a Player Character at multiple levels since it may be
	 * available from multiple sources. This also returns the spell lists
	 * associated with the given level, since it is possible for a multi-class
	 * character to have access to the same spell at different levels. By
	 * returning the source as well as the spell levels, such scenarios can be
	 * appropriately distinguished.
	 * 
	 * This method is value-semantic in that ownership of the returned
	 * DoubleKeyMapToList is transferred to the class calling this method.
	 * Modification of the returned DoubleKeyMapToList will not modify this
	 * AvailableSpellFacet and modification of this AvailableSpellFacet will not
	 * modify the returned DoubleKeyMapToList. Modifications to the returned
	 * DoubleKeyMapToList will also not modify any future or previous objects
	 * returned by this (or other) methods on AvailableSpellFacet. If you wish
	 * to modify the information stored in this AvailableSpellFacet, you must
	 * use the add*() and remove*() methods of AvailableSpellFacet.
	 * 
	 * @param id
	 *            The CharID identifying the Player Character for which the
	 *            spell levels should be returned
	 * @return A non-null DoubleKeyMapToList indicating the Spells, spell levels
	 *         and sources of those spell levels available to a Player Character
	 *         for a given Spell.
	 */
	public DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> getSpellLevelInfo(
		CharID id)
	{
		DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer> levelInfo =
				new DoubleKeyMapToList<Spell, CDOMList<Spell>, Integer>();
		Map<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> listMap =
				getCachedMap(id);
		if (listMap == null)
		{
			return levelInfo;
		}
		for (Entry<CDOMList<Spell>, Map<Integer, Map<Spell, Set<Object>>>> me : listMap
			.entrySet())
		{
			CDOMList<Spell> list = me.getKey();
			//Check to ensure we don't use SPELLS:
			if (!(list instanceof ClassSpellList)
				&& !(list instanceof DomainSpellList))
			{
				continue;
			}
			for (Entry<Integer, Map<Spell, Set<Object>>> sme : me.getValue()
				.entrySet())
			{
				Integer level = sme.getKey();
				for (Spell spell : sme.getValue().keySet())
				{
					levelInfo.addToListFor(spell, list, level);
				}
			}
		}
		return levelInfo;
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
